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
import tech.cassandre.trading.bot.util.base.service.BaseService;

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
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

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
     * @param rate            rate in ms
     * @param newTradeService market data service
     */
    public TradeServiceXChangeImplementation(final long rate,
                                             final org.knowm.xchange.service.trade.TradeService newTradeService) {
        super(rate);
        this.tradeService = newTradeService;
    }

    /**
     * Creates market order.
     *
     * @param strategy     strategy
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order creation result
     */
    private OrderCreationResultDTO createMarketOrder(final StrategyDTO strategy,
                                                     final OrderTypeDTO orderTypeDTO,
                                                     final CurrencyPairDTO currencyPair,
                                                     final BigDecimal amount) {
        try {
            // Making the order.
            MarketOrder m = new MarketOrder(utilMapper.mapToOrderType(orderTypeDTO),
                    amount,
                    currencyMapper.mapToCurrencyPair(currencyPair));
            logger.debug("TradeService - Sending market order : {} - {} - {}", orderTypeDTO, currencyPair, amount);

            // Sending the order.
            final String orderId = tradeService.placeMarketOrder(m);
            OrderDTO openingOrder = OrderDTO.builder()
                    .orderId(orderId)
                    .type(orderTypeDTO)
                    .strategy(strategy)
                    .currencyPair(currencyPair)
                    .amount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .status(PENDING_NEW)
                    .timestamp(ZonedDateTime.now())
                    .build();
            localOrders.put(orderId, openingOrder);
            final OrderCreationResultDTO result = new OrderCreationResultDTO(openingOrder);
            logger.debug("TradeService - Order created : {}", result);
            return result;
        } catch (Exception e) {
            logger.error("TradeService - Error calling createBuyMarketOrder : {}", e.getMessage());
            return new OrderCreationResultDTO("TradeService - Error calling createBuyMarketOrder : " + e.getMessage(), e);
        }
    }

    /**
     * Creates limit order.
     *
     * @param strategy     strategy
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   In a BID this is the highest acceptable price, in an ASK this is the lowest acceptable price
     * @return order creation result
     */
    private OrderCreationResultDTO createLimitOrder(final StrategyDTO strategy,
                                                    final OrderTypeDTO orderTypeDTO,
                                                    final CurrencyPairDTO currencyPair,
                                                    final BigDecimal amount,
                                                    final BigDecimal limitPrice) {
        try {
            // Making the order.
            LimitOrder l = new LimitOrder(utilMapper.mapToOrderType(orderTypeDTO),
                    amount,
                    currencyMapper.mapToCurrencyPair(currencyPair),
                    null,
                    null,
                    limitPrice);
            logger.debug("TradeService - Sending market order : {} - {} - {}", orderTypeDTO, currencyPair, amount);

            // Sending & creating the order.
            final String orderId = tradeService.placeLimitOrder(l);
            OrderDTO openingOrder = OrderDTO.builder()
                    .orderId(orderId)
                    .type(orderTypeDTO)
                    .strategy(strategy)
                    .currencyPair(currencyPair)
                    .amount(CurrencyAmountDTO.builder()
                            .value(amount)
                            .currency(currencyPair.getBaseCurrency())
                            .build())
                    .limitPrice(CurrencyAmountDTO.builder()
                            .value(limitPrice)
                            .currency(currencyPair.getQuoteCurrency())
                            .build())
                    .status(PENDING_NEW)
                    .timestamp(ZonedDateTime.now())
                    .build();
            localOrders.put(orderId, openingOrder);
            final OrderCreationResultDTO result = new OrderCreationResultDTO(openingOrder);
            logger.debug("TradeService - Order creation result : {}", result);
            return result;
        } catch (Exception e) {
            logger.error("TradeService - Error calling createLimitOrder : {}", e.getMessage());
            return new OrderCreationResultDTO("TradeService - Error calling createLimitOrder : " + e.getMessage(), e);
        }
    }

    @Override
    public final OrderCreationResultDTO createBuyMarketOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(strategy, BID, currencyPair, amount);
    }

    @Override
    public final OrderCreationResultDTO createSellMarketOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(strategy, ASK, currencyPair, amount);
    }

    @Override
    public final OrderCreationResultDTO createBuyLimitOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        return createLimitOrder(strategy, BID, currencyPair, amount, limitPrice);
    }

    @Override
    public final OrderCreationResultDTO createSellLimitOrder(final StrategyDTO strategy, final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        return createLimitOrder(strategy, ASK, currencyPair, amount, limitPrice);
    }

    @Override
    public final boolean cancelOrder(final String orderId) {
        logger.debug("TradeService - Canceling order {}", orderId);
        if (orderId != null) {
            try {
                logger.debug("TradeService - Successfully canceled order {}", orderId);
                return tradeService.cancelOrder(orderId);
            } catch (Exception e) {
                logger.error("TradeService - Error canceling order {} : {}", orderId, e.getMessage());
                return false;
            }
        } else {
            logger.error("TradeService - Error canceling order, order id provided is null");
            return false;
        }
    }

    @Override
    public final Set<OrderDTO> getOrders() {
        logger.debug("TradeService - Getting open orders from exchange");
        try {
            // Consume a token from the token bucket.
            // If a token is not available this method will block until the refill adds one to the bucket.
            getBucket().asScheduler().consume(1);

            // We add the local orders to orders received.
            Set<OrderDTO> results = new LinkedHashSet<>(localOrders.values());
            tradeService.getOpenOrders()
                    .getOpenOrders()
                    .forEach(order -> {
                        // If we received the order from server, we remove local order.
                        localOrders.remove(order.getId());
                        results.add(orderMapper.mapToOrderDTO(order));
                    });
            logger.debug("TradeService - {} order(s) found", results.size());
            return results;
        } catch (IOException e) {
            logger.error("TradeService - Error retrieving open orders : {}", e.getMessage());
            return Collections.emptySet();
        } catch (InterruptedException e) {
            logger.error("TradeService - InterruptedException : {}", e.getMessage());
            return Collections.emptySet();
        }
    }

    @Override
    public final Set<TradeDTO> getTrades() {
        logger.debug("TradeService - Getting trades from exchange");
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
                    .map(tradeMapper::mapToTradeDTO)
                    .collect(Collectors.toSet());
            logger.debug("TradeService - {} trade(s) found", results.size());
            return results;
        } catch (IOException e) {
            logger.error("TradeService - Error retrieving trades : {}", e.getMessage());
            return Collections.emptySet();
        } catch (InterruptedException e) {
            logger.error("TradeService - InterruptedException : {}", e.getMessage());
            return Collections.emptySet();
        }
    }

}
