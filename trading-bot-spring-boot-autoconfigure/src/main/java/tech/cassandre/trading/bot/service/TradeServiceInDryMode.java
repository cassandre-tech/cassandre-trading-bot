package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.FILLED;

/**
 * Trade service in dry mode.
 */
public class TradeServiceInDryMode implements TradeService {

    /** Waiting time before sending orders and flux to flux. */
    private static final long WAITING_TIME = 3000L;

    /** Order counter. */
    private final AtomicInteger orderCounter = new AtomicInteger(1);

    /** Trade counter. */
    private final AtomicInteger tradeCounter = new AtomicInteger(1);

    /** Order flux. */
    private OrderFlux orderFlux;

    /** Trade flux. */
    private TradeFlux tradeFlux;

    /** Last received tickers. */
    private final Map<CurrencyPairDTO, TickerDTO> lastTickers = new LinkedHashMap<>();

    /** Orders. */
    private final Map<String, OrderDTO> orders = new LinkedHashMap<>();

    /** The trades owned by the user. */
    private final Map<String, TradeDTO> trades = new LinkedHashMap<>();

    /**
     * Set dependencies.
     *
     * @param newOrderFlux order flux
     * @param newTradeFlux trade flux
     */
    public void setDependencies(final OrderFlux newOrderFlux,
                                final TradeFlux newTradeFlux) {
        this.orderFlux = newOrderFlux;
        this.tradeFlux = newTradeFlux;
    }

    /**
     * Creates a fake market order.
     *
     * @param orderTypeDTO order type
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order creation result
     */
    private OrderCreationResultDTO createMarketOrder(final OrderTypeDTO orderTypeDTO, final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        // We retrieve the last pricing from tickers.
        TickerDTO t = lastTickers.get(currencyPair);

        // We create the order.
        if (t != null) {
            // We create and send the order.
            final String orderId = getNextOrderNumber();
            final OrderDTO order = OrderDTO.builder()
                    .id(orderId)
                    .currencyPair(currencyPair)
                    .type(orderTypeDTO)
                    .status(FILLED)
                    .averagePrice(t.getBid())
                    .originalAmount(amount)
                    .fee(new BigDecimal("0"))
                    .timestamp(ZonedDateTime.now())
                    .create();


            // We crate and send the trade.
            final String tradeId = getNextTradeNumber();
            final TradeDTO trade = TradeDTO.builder()
                    .id(tradeId)
                    .orderId(orderId)
                    .currencyPair(currencyPair)
                    .type(orderTypeDTO)
                    .originalAmount(amount)
                    .price(t.getBid())
                    .timestamp(ZonedDateTime.now())
                    .feeAmount(new BigDecimal("0"))
                    .feeCurrency(currencyPair.getBaseCurrency())
                    .create();


            // Sending the results after the return.
            Executors.newFixedThreadPool(1).submit(() -> {
                try {
                    Thread.sleep(WAITING_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                orderFlux.emitValue(order);
                orders.put(orderId, order);
                tradeFlux.emitValue(trade);
                trades.put(tradeId, trade);
            });

            // We create the result.
            return new OrderCreationResultDTO(orderId);
        } else {
            return new OrderCreationResultDTO("Ticker not found", new Exception("Ticker not found"));
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
        // TODO Implement this later.
        return new OrderCreationResultDTO("Not implemented", new Exception("Not implemented"));
    }

    @Override
    public final OrderCreationResultDTO createSellLimitOrder(final CurrencyPairDTO currencyPair, final BigDecimal amount, final BigDecimal limitPrice) {
        // TODO Implement this later.
        return new OrderCreationResultDTO("Not implemented", new Exception("Not implemented"));
    }

    @Override
    public final Optional<OrderDTO> getOpenOrderByOrderId(final String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public final Set<OrderDTO> getOpenOrders() {
        return new LinkedHashSet<>(orders.values());
    }

    @Override
    public final boolean cancelOrder(final String orderId) {
        return orders.remove(orderId) != null;
    }

    @Override
    public final Set<TradeDTO> getTrades() {
        return new LinkedHashSet<>(trades.values());
    }

    /**
     * Returns next order number.
     *
     * @return next order number
     */
    private String getNextOrderNumber() {
        return "DRY_ORDER_".concat(String.format("%09d", orderCounter.getAndIncrement()));
    }

    /**
     * Returns next trade number.
     *
     * @return next trade number
     */
    private String getNextTradeNumber() {
        return "DRY_TRADE_".concat(String.format("%09d", tradeCounter.getAndIncrement()));
    }

    /**
     * Method called by streams at every ticker update.
     *
     * @param ticker ticker
     */
    public void tickerUpdate(final TickerDTO ticker) {
        lastTickers.put(ticker.getCurrencyPair(), ticker);
    }

}
