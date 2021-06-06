package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Service getting information about orders and allowing you to create new ones.
 */
public interface TradeService {

    /**
     * Creates a buy market order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createBuyMarketOrder(GenericCassandreStrategy strategy,
                                                CurrencyPairDTO currencyPair,
                                                BigDecimal amount);

    /**
     * Creates a sell market order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createSellMarketOrder(GenericCassandreStrategy strategy,
                                                 CurrencyPairDTO currencyPair,
                                                 BigDecimal amount);

    /**
     * Creates a buy limit order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the highest acceptable price
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createBuyLimitOrder(GenericCassandreStrategy strategy,
                                               CurrencyPairDTO currencyPair,
                                               BigDecimal amount,
                                               BigDecimal limitPrice);

    /**
     * Creates a sell limit order.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @param limitPrice   the lowest acceptable price
     * @return order result (order id or error)
     */
    OrderCreationResultDTO createSellLimitOrder(GenericCassandreStrategy strategy,
                                                CurrencyPairDTO currencyPair,
                                                BigDecimal amount,
                                                BigDecimal limitPrice);

    /**
     * Cancel order.
     *
     * @param orderId order id
     * @return true if cancelled
     */
    boolean cancelOrder(String orderId);

    /**
     * Get orders from exchange.
     *
     * @return list of orders
     */
    Set<OrderDTO> getOrders();

    /**
     * Get trades from exchange.
     *
     * @return list of orders
     */
    Set<TradeDTO> getTrades();

}
