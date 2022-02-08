package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.internal.CassandreStrategyInterface;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Service getting information about orders and their management.
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
    OrderCreationResultDTO createBuyMarketOrder(CassandreStrategyInterface strategy,
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
    OrderCreationResultDTO createSellMarketOrder(CassandreStrategyInterface strategy,
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
    OrderCreationResultDTO createBuyLimitOrder(CassandreStrategyInterface strategy,
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
    OrderCreationResultDTO createSellLimitOrder(CassandreStrategyInterface strategy,
                                                CurrencyPairDTO currencyPair,
                                                BigDecimal amount,
                                                BigDecimal limitPrice);

    /**
     * Cancel order.
     *
     * @param orderUid order uid
     * @return true if cancelled
     */
    boolean cancelOrder(long orderUid);

    /**
     * Get orders from exchange.
     *
     * @return list of orders
     */
    Set<OrderDTO> getOrders();

    /**
     * Get trades from exchange.
     *
     * @return list of trades
     */
    Set<TradeDTO> getTrades();

}
