package tech.cassandre.trading.bot.service;

import org.apache.commons.lang3.time.DateUtils;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.service.trade.params.TradeHistoryParamsAll;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.util.base.BaseService;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Trade service - XChange implementation.
 */
public class TradeServiceXChangeImplementation extends BaseService implements TradeService {

    /** XChange service. */
    private final org.knowm.xchange.service.trade.TradeService tradeService;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** The trades restored from backup. */
    private final Set<TradeDTO> tradesFromBackup = new LinkedHashSet<>();

    /**
     * Constructor.
     *
     * @param rate               rate in ms
     * @param newTradeService    market data service
     * @param newTradeRepository new trade repository
     */
    public TradeServiceXChangeImplementation(final long rate,
                                             final org.knowm.xchange.service.trade.TradeService newTradeService,
                                             final TradeRepository newTradeRepository) {
        super(rate);
        this.tradeService = newTradeService;
        this.tradeRepository = newTradeRepository;
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
            MarketOrder m = new MarketOrder(getMapper().mapToOrderType(orderTypeDTO), amount, getCurrencyPair(currencyPair));
            getLogger().debug("TradeService - Sending market order : {} - {} - {}", orderTypeDTO, currencyPair, amount);

            // Sending the order.
            final OrderCreationResultDTO result = new OrderCreationResultDTO(tradeService.placeMarketOrder(m));
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
            LimitOrder l = new LimitOrder(getMapper().mapToOrderType(orderTypeDTO), amount, getCurrencyPair(currencyPair), null, null, limitPrice);
            getLogger().debug("TradeService - Sending market order : {} - {} - {}", orderTypeDTO, currencyPair, amount);

            // Sending the order.
            final OrderCreationResultDTO result = new OrderCreationResultDTO(tradeService.placeLimitOrder(l));
            getLogger().debug("TradeService - Order creation result : {}", result);
            return result;
        } catch (Exception e) {
            getLogger().error("TradeService - Error calling createLimitOrder : {}", e.getMessage());
            return new OrderCreationResultDTO("TradeService - Error calling createLimitOrder : " + e.getMessage(), e);
        }
    }

    @Override
    public final OrderCreationResultDTO createBuyMarketOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(OrderTypeDTO.BID, currencyPair, amount);
    }

    @Override
    public final OrderCreationResultDTO createSellMarketOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        return createMarketOrder(OrderTypeDTO.ASK, currencyPair, amount);
    }

    @Override
    public final OrderCreationResultDTO createBuyLimitOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        return createLimitOrder(OrderTypeDTO.BID, currencyPair, amount, limitPrice);
    }

    @Override
    public final OrderCreationResultDTO createSellLimitOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        return createLimitOrder(OrderTypeDTO.ASK, currencyPair, amount, limitPrice);
    }

    @Override
    public final Optional<OrderDTO> getOpenOrderByOrderId(final String orderId) {
        if (orderId != null) {
            return getOpenOrders()
                    .stream()
                    .filter(o -> orderId.equalsIgnoreCase(o.getId()))
                    .findFirst();
        } else {
            return Optional.empty();
        }
    }

    @Override
    public final Set<OrderDTO> getOpenOrders() {
        getLogger().debug("TradeService - Getting open orders from exchange");
        try {
            // Consume a token from the token bucket.
            // If a token is not available this method will block until the refill adds one to the bucket.
            getBucket().asScheduler().consume(1);

            Set<OrderDTO> results = new LinkedHashSet<>();
            tradeService.getOpenOrders()
                    .getOpenOrders()
                    .forEach(order -> results.add(getMapper().mapToOrderDTO(order)));
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
    public final Set<TradeDTO> getTrades() {
        getLogger().debug("TradeService - Getting trades from exchange");
        try {
            // Consume a token from the token bucket.
            // If a token is not available this method will block until the refill adds one to the bucket.
            getBucket().asScheduler().consume(1);

            // Query 1 week of trades.
            Set<TradeDTO> results = new LinkedHashSet<>(tradesFromBackup);
            TradeHistoryParamsAll params = new TradeHistoryParamsAll();
            Date startDate = DateUtils.addWeeks(new Date(), -1);
            Date endDate = new Date();
            params.setStartTime(startDate);
            params.setEndTime(endDate);
            tradeService.getTradeHistory(params)
                    .getUserTrades()
                    .forEach(userTrade -> results.add(getMapper().mapToTradeDTO(userTrade)));
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

    @Override
    public final void restoreTrade(final TradeDTO trade) {
        tradesFromBackup.add(trade);
    }

    @Override
    public final void backupTrade(final TradeDTO trade) {
        Trade t = new Trade();
        t.setId(trade.getId());
        t.setOrderId(trade.getOrderId());
        t.setType(trade.getType().toString());
        t.setOriginalAmount(trade.getOriginalAmount());
        t.setCurrencyPair(trade.getCurrencyPair().toString());
        t.setPrice(trade.getPrice());
        t.setTimestamp(trade.getTimestamp());
        t.setFeeAmount(trade.getFee().getValue());
        t.setFeeCurrency(trade.getFee().getCurrency().toString());
        tradeRepository.save(t);
    }

}
