package tech.cassandre.trading.bot.service.xchange;

import org.apache.commons.lang3.time.DateUtils;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.base.BaseService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;

/**
 * Trade service - XChange implementation.
 */
public class TradeServiceXChangeImplementation extends BaseService implements TradeService {

    /** XChange service. */
    private final org.knowm.xchange.service.trade.TradeService tradeService;

    /** Hashmap used to store orders created locally. */
    private final HashMap<String, OrderDTO> localOrders = new HashMap<>();

    /**
     * Constructor.
     *
     * @param rate               rate in ms
     * @param newTradeService    market data service
     */
    public TradeServiceXChangeImplementation(final long rate,
                                             final org.knowm.xchange.service.trade.TradeService newTradeService) {
        super(rate);
        this.tradeService = newTradeService;
    }

    /**
     * Creates market order.
     *
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order creation result
     */
    private OrderCreationResultDTO createMarketOrder(final OrderTypeDTO orderTypeDTO, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        try {
            // Making the order.
            MarketOrder m = new MarketOrder(mapper.mapToOrderType(orderTypeDTO), amount, getCurrencyPair(currencyPair));
            getLogger().debug("TradeService - Sending market order : {} - {} - {}", orderTypeDTO, currencyPair, amount);

            // Sending the order.
            final String orderId = tradeService.placeMarketOrder(m);
            OrderDTO openingOrder = OrderDTO.builder()
                    .id(orderId)
                    .timestamp(ZonedDateTime.now())
                    .type(orderTypeDTO)
                    .amount(new CurrencyAmountDTO(amount, currencyPair.getBaseCurrency()))
                    .currencyPair(currencyPair)
                    .status(PENDING_NEW)
                    .build();
            localOrders.put(orderId, openingOrder);
            final OrderCreationResultDTO result = new OrderCreationResultDTO(openingOrder);
            getLogger().debug("TradeService - Order created : {}", result);
            return result;
        } catch (Exception e) {
            getLogger().error("TradeService - Error calling createBuyMarketOrder : {}", e.getMessage());
            return new OrderCreationResultDTO("TradeService - Error calling createBuyMarketOrder : " + e.getMessage(), e);
        }
    }

    /**
     * Creates limit order.
     *
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   In a BID this is the highest acceptable price, in an ASK this is the lowest acceptable price
     * @return order creation result
     */
    private OrderCreationResultDTO createLimitOrder(final OrderTypeDTO orderTypeDTO, final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        try {
            // Making the order.
            LimitOrder l = new LimitOrder(mapper.mapToOrderType(orderTypeDTO), amount, getCurrencyPair(currencyPair), null, null, limitPrice);
            getLogger().debug("TradeService - Sending market order : {} - {} - {}", orderTypeDTO, currencyPair, amount);

            // Sending & creating the order.
            final String orderId = tradeService.placeLimitOrder(l);
            OrderDTO openingOrder = OrderDTO.builder()
                    .id(orderId)
                    .timestamp(ZonedDateTime.now())
                    .type(orderTypeDTO)
                    .amount(new CurrencyAmountDTO(amount, currencyPair.getBaseCurrency()))
                    .currencyPair(currencyPair)
                    .status(PENDING_NEW)
                    .limitPrice(new CurrencyAmountDTO(limitPrice, currencyPair.getQuoteCurrency()))
                    .build();
            localOrders.put(orderId, openingOrder);
            final OrderCreationResultDTO result = new OrderCreationResultDTO(openingOrder);
            getLogger().debug("TradeService - Order creation result : {}", result);
            return result;
        } catch (Exception e) {
            getLogger().error("TradeService - Error calling createLimitOrder : {}", e.getMessage());
            return new OrderCreationResultDTO("TradeService - Error calling createLimitOrder : " + e.getMessage(), e);
        }
    }

    @Override
    public final OrderCreationResultDTO createBuyMarketOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(OrderTypeDTO.BID, currencyPair, amount);
    }

    @Override
    public final OrderCreationResultDTO createSellMarketOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(OrderTypeDTO.ASK, currencyPair, amount);
    }

    @Override
    public final OrderCreationResultDTO createBuyLimitOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        return createLimitOrder(OrderTypeDTO.BID, currencyPair, amount, limitPrice);
    }

    @Override
    public final OrderCreationResultDTO createSellLimitOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        return createLimitOrder(OrderTypeDTO.ASK, currencyPair, amount, limitPrice);
    }

    @Override
    public final boolean cancelOrder(final String orderId) {
        getLogger().debug("TradeService - Canceling order {}", orderId);
        if (orderId != null) {
            try {
                getLogger().debug("TradeService - Successfully canceled order {}", orderId);
                return tradeService.cancelOrder(orderId);
            } catch (Exception e) {
                getLogger().error("Error canceling order {} : {}", orderId, e.getMessage());
                return false;
            }
        } else {
            getLogger().error("Error canceling order, order id provided is null");
            return false;
        }
    }

    @Override
    public final Set<OrderDTO> getOpenOrders() {
        return getOrders();
    }

    @Override
    public final Set<OrderDTO> getOrders() {
        getLogger().debug("TradeService - Getting open orders from exchange");
        try {
            // Consume a token from the token bucket.
            // If a token is not available this method will block until the refill adds one to the bucket.
            getBucket().asScheduler().consume(1);

            // We add the local orders to orders received.
            Set<OrderDTO> results = new LinkedHashSet<>(localOrders.values());
            tradeService.getOpenOrders()
                    .getOpenOrders()
                    .forEach(order -> {
                        results.add(mapper.mapToOrderDTO(order));
                        // If we received the order from server, we remove local order.
                        localOrders.remove(order.getId());
                    });
            getLogger().debug("TradeService - {} order(s) found", results.size());
            return results;
        } catch (IOException e) {
            getLogger().error("TradeService - Error retrieving open orders : {}", e.getMessage());
            return Collections.emptySet();
        } catch (InterruptedException e) {
            getLogger().error("TradeService - InterruptedException : {}", e.getMessage());
            return Collections.emptySet();
        }
    }

    @Override
    public final Set<TradeDTO> getTrades() {
        getLogger().debug("TradeService - Getting trades from exchange");
        try {
            // Consume a token from the token bucket.
            // If a token is not available this method will block until the refill adds one to the bucket.
            getBucket().asScheduler().consume(1);

            // Query 1 week of trades.
            TradeHistoryParamsAll params = new TradeHistoryParamsAll();
            Date startDate = DateUtils.addWeeks(new Date(), -1);
            Date endDate = new Date();
            params.setStartTime(startDate);
            params.setEndTime(endDate);
            final Set<TradeDTO> results = tradeService.getTradeHistory(params)
                    .getUserTrades()
                    .stream()
                    .map(mapper::mapToTradeDTO)
                    .collect(Collectors.toSet());
            getLogger().debug("TradeService - {} trade(s) found", results.size());
            return results;
        } catch (IOException e) {
            getLogger().error("TradeService - Error retrieving trades : {}", e.getMessage());
            return Collections.emptySet();
        } catch (InterruptedException e) {
            getLogger().error("TradeService - InterruptedException : {}", e.getMessage());
            return Collections.emptySet();
        }
    }

}
