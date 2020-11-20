package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

/**
 * Service giving information about orders and allowing you to create new orders.
 */
public interface TradeService {

    /**
     * Creates a buy market order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createBuyMarketOrder(CurrencyPairDTO currencyPair, BigDecimal amount);

    /**
     * Creates a sell market order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createSellMarketOrder(CurrencyPairDTO currencyPair, BigDecimal amount);

    /**
     * Creates a buy limit order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the highest acceptable price
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createBuyLimitOrder(CurrencyPairDTO currencyPair, BigDecimal amount, BigDecimal limitPrice);

    /**
     * Creates a sell limit order.
     *
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the lowest acceptable price
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createSellLimitOrder(CurrencyPairDTO currencyPair, BigDecimal amount, BigDecimal limitPrice);

    /**
     * Get an open order by its id.
     *
     * @param orderId order id
     * @return order
     */
    Optional<OrderDTO> getOpenOrderByOrderId(String orderId);

    /**
     * Get open orders.
     *
     * @return list of open orders
     */
    Set<OrderDTO> getOpenOrders();

    /**
     * Get orders from database.
     * @return orders from database
     */
    Set<OrderDTO> getOrdersFromDatabase();

    /**
     * Cancel order.
     *
     * @param orderId order id
     * @return true if cancelled
     */
    boolean cancelOrder(String orderId);

    /**
     * Get last week trades.
     *
     * @return trades
     */
    Set<TradeDTO> getTrades();

    /**
     * Get trades from database.
     *
     * @return trades
     */
    Set<TradeDTO> getTradesFromDatabase();

}
