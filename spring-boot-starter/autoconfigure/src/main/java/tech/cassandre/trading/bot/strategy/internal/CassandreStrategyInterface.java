package tech.cassandre.trading.bot.strategy.internal;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * CassandreStrategyInterface list the methods a strategy type must implement to be able to interact with the Cassandre framework.
 * <p>
 * These are the classes used by Cassandre to manage a position.
 * - CassandreStrategyInterface list the methods a strategy type must implement to be able to interact with the Cassandre framework.
 * - CassandreStrategyConfiguration contains the configuration of the strategy.
 * - CassandreStrategyDependencies contains all the dependencies required by a strategy and provided by the Cassandre framework.
 * - CassandreStrategyImplementation is the default implementation of CassandreStrategyInterface, this code manages the interaction between Cassandre framework and a strategy.
 * - CassandreStrategy (class) is the class that every strategy used by user ({@link BasicCassandreStrategy} must extend. It contains methods to access data and manage orders, trades, positions.
 * There are the classes used by the developer.
 * - CassandreStrategy (interface) is the annotation allowing you Cassandre to recognize a user strategy.
 * - BasicCassandreStrategy - User inherits this class this one to make a basic strategy.
 */
public interface CassandreStrategyInterface {

    // =================================================================================================================
    // Configuration & dependencies set by Cassandre.

    /**
     * Set strategy configuration.
     *
     * @param cassandreStrategyConfiguration cassandre strategy configuration
     */
    void setConfiguration(CassandreStrategyConfiguration cassandreStrategyConfiguration);

    /**
     * Get strategy configuration.
     *
     * @return cassandre strategy configuration
     */
    CassandreStrategyConfiguration getConfiguration();

    /**
     * Set strategy dependencies.
     *
     * @param cassandreStrategyDependencies cassandre strategy dependencies
     */
    void setDependencies(CassandreStrategyDependencies cassandreStrategyDependencies);

    // =================================================================================================================
    // Configuration set by the strategy developer.

    /**
     * Implements this method to tell the bot which currency pairs your strategy will receive.
     *
     * @return the list of currency pairs tickers your want to receive in this strategy
     */
    Set<CurrencyPairDTO> getRequestedCurrencyPairs();

    /**
     * Implements this method to tell the bot which account from the accounts you own is the one you use for trading.
     *
     * @param accounts all your accounts
     * @return your trading account
     */
    Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts);

    // =================================================================================================================
    // Strategy initialization.

    /**
     * Initialize strategy accounts with exchange accounts data retrieved at Cassandre startup.
     *
     * @param accounts accounts
     */
    void initializeAccounts(Map<String, AccountDTO> accounts);

    /**
     * This method is called by Cassandre before flux are started.
     * For example, you can implement this method to integrate your historical data.
     */
    default void initialize() {
        // Can be implemented by a strategy developer.
    }

    // =================================================================================================================
    // Internal methods called by Cassandre streams on data update.

    /**
     * Method called by streams on accounts updates.
     *
     * @param accounts accounts updates
     */
    void accountsUpdates(Set<AccountDTO> accounts);

    /**
     * Method called by streams on tickers updates.
     *
     * @param tickers tickers updates
     */
    void tickersUpdates(Set<TickerDTO> tickers);

    /**
     * Method called by streams on orders updates.
     *
     * @param orders orders updates
     */
    void ordersUpdates(Set<OrderDTO> orders);

    /**
     * Method called by streams on trades updates.
     *
     * @param trades trades updates
     */
    void tradesUpdates(Set<TradeDTO> trades);

    /**
     * Method called by streams on positions updates.
     *
     * @param positions positions updates
     */
    void positionsUpdates(Set<PositionDTO> positions);

    // =================================================================================================================
    // Methods that can be overridden by the developer in its strategy to receive data updates.

    /**
     * Method called by Cassandre when there are accounts updates.
     *
     * @param accounts accounts updates
     */
    default void onAccountsUpdates(Map<String, AccountDTO> accounts) {
        // Can be overridden by a strategy developer to receive events.
    }

    /**
     * Method called by Cassandre when there are tickers updates.
     *
     * @param tickers tickers updates
     */
    default void onTickersUpdates(Map<CurrencyPairDTO, TickerDTO> tickers) {
        // Can be overridden by a strategy developer to receive events.
    }

    /**
     * Method called by Cassandre when there are orders updates.
     *
     * @param orders orders updates
     */
    default void onOrdersUpdates(Map<String, OrderDTO> orders) {
        // Can be overridden by a strategy developer to receive events.
    }

    /**
     * Method called by Cassandre when there are trades updates.
     *
     * @param trades trades updates
     */
    default void onTradesUpdates(Map<String, TradeDTO> trades) {
        // Can be overridden by a strategy developer to receive events.
    }

    /**
     * Method called by Cassandre when there are positions updates.
     *
     * @param positions positions updates
     */
    default void onPositionsUpdates(Map<Long, PositionDTO> positions) {
        // Can be overridden by a strategy developer to receive events.
    }

    /**
     * Method called by Cassandre when there are positions status updates.
     *
     * @param positions positions status updates
     */
    default void onPositionsStatusUpdates(Map<Long, PositionDTO> positions) {
        // Can be overridden by a strategy developer to receive events.
    }

    // =================================================================================================================
    // Utils.

    /**
     * Returns the last price received for a currency pair.
     *
     * @param currencyPair currency pair
     * @return last price
     */
    BigDecimal getLastPriceForCurrencyPair(CurrencyPairDTO currencyPair);

}
