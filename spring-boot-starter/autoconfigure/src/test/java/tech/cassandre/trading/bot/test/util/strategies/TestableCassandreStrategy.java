package tech.cassandre.trading.bot.test.util.strategies;

import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static tech.cassandre.trading.bot.test.util.junit.BaseTest.ETH_BTC;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.ETH_USDT;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;

/**
 * Testable strategy (used for tests).
 */
@SuppressWarnings("unused")
@CassandreStrategy(
        strategyId = "01",
        strategyName = "Testable strategy")
@ConditionalOnProperty(
        value = PARAMETER_TESTABLE_STRATEGY_ENABLED,
        havingValue = "true")
@Getter
public class TestableCassandreStrategy extends BasicCassandreStrategy {

    /** Testable strategy enabled parameter. */
    public static final String PARAMETER_TESTABLE_STRATEGY_ENABLED = "testableStrategy.enabled";

    /** Waiting time during each method. */
    public static final Duration MINIMUM_METHOD_DURATION = Duration.ofSeconds(1);

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** Accounts update received. */
    private final List<AccountDTO> accountsUpdatesReceived = Collections.synchronizedList(new LinkedList<>());

    /** Tickers update received. */
    private final List<TickerDTO> tickersUpdatesReceived = Collections.synchronizedList(new LinkedList<>());

    /** Orders update received. */
    private final List<OrderDTO> ordersUpdatesReceived = Collections.synchronizedList(new LinkedList<>());

    /** Trades update received. */
    private final List<TradeDTO> tradesUpdatesReceived = Collections.synchronizedList(new LinkedList<>());

    /** Positions update received. */
    private final List<PositionDTO> positionsUpdatesReceived = Collections.synchronizedList(new LinkedList<>());

    /** Positions status update received. */
    private final List<PositionDTO> positionsStatusUpdatesReceived = Collections.synchronizedList(new LinkedList<>());

    /** Requested currency pairs. */
    Set<CurrencyPairDTO> requestedCurrencyPairs = ConcurrentHashMap.newKeySet();

    /** Initialize flag. */
    private boolean initialized = false;

    /**
     * Constructor.
     */
    public TestableCassandreStrategy() {
        requestedCurrencyPairs.add(ETH_BTC);
        requestedCurrencyPairs.add(ETH_USDT);
    }

    @Override
    public final Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        return requestedCurrencyPairs;
    }

    /**
     * Updates the requested currency pairs.
     *
     * @param newRequestedCurrencyPairs new list of requested currency pairs
     */
    public final void updateRequestedCurrencyPairs(Set<CurrencyPairDTO> newRequestedCurrencyPairs) {
        requestedCurrencyPairs.clear();
        requestedCurrencyPairs.addAll(newRequestedCurrencyPairs);
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        if (accounts.size() == 1) {
            // Used for Gemini integration tests.
            return accounts.stream().findFirst();
        } else {
            return accounts.stream()
                    .filter(a -> "trade".equals(a.getName()))
                    .findFirst();
        }
    }

    @Override
    public void initialize() {
        initialized = true;
    }

    @Override
    @SneakyThrows
    public final void onAccountsUpdates(final Map<String, AccountDTO> accounts) {
        accounts.values()
                .stream()
                .peek(accountDTO -> logger.info("TestableStrategy-onAccountsUpdates n° {} : {} \n ",
                        getUpdatesCount(accountsUpdatesReceived),
                        accountDTO))
                .forEach(accountsUpdatesReceived::add);

        Thread.sleep(MINIMUM_METHOD_DURATION.toMillis());
    }

    @Override
    @SneakyThrows
    public final void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        tickers.values()
                .stream()
                .peek(tickerDTO -> logger.info("TestableStrategy-onTickersUpdates n° {} : {} \n ",
                        getUpdatesCount(tickersUpdatesReceived),
                        tickerDTO))
                .forEach(tickersUpdatesReceived::add);

        Thread.sleep(MINIMUM_METHOD_DURATION.toMillis());
    }

    @Override
    @SneakyThrows
    public final void onOrdersUpdates(final Map<String, OrderDTO> orders) {
        orders.values()
                .stream()
                .peek(orderDTO -> logger.info("TestableStrategy-onOrdersUpdates n° {} : {} \n ",
                        getUpdatesCount(ordersUpdatesReceived),
                        orderDTO))
                .forEach(ordersUpdatesReceived::add);

        Thread.sleep(MINIMUM_METHOD_DURATION.toMillis());
    }

    @Override
    @SneakyThrows
    public void onTradesUpdates(final Map<String, TradeDTO> trades) {
        trades.values()
                .stream()
                .peek(tradeDTO -> logger.info("TestableStrategy-onTradesUpdates n° {} : {} \n ",
                        getUpdatesCount(tradesUpdatesReceived),
                        tradeDTO))
                .forEach(tradesUpdatesReceived::add);

        Thread.sleep(MINIMUM_METHOD_DURATION.toMillis());
    }

    @Override
    @SneakyThrows
    public void onPositionsUpdates(final Map<Long, PositionDTO> positions) {
        positions.values()
                .stream()
                .peek(positionDTO -> logger.info("TestableStrategy-onPositionsUpdates n° {} : {} \n ",
                        getUpdatesCount(positionsUpdatesReceived),
                        positionDTO))
                .forEach(positionsUpdatesReceived::add);

        Thread.sleep(MINIMUM_METHOD_DURATION.toMillis());
    }

    @Override
    @SneakyThrows
    public void onPositionsStatusUpdates(final Map<Long, PositionDTO> positions) {
        positions.values()
                .stream()
                .peek(positionDTO -> logger.info("TestableStrategy-onPositionsStatusUpdates n° {} : {} \n ",
                        getUpdatesCount(positionsStatusUpdatesReceived),
                        positionDTO))
                .forEach(positionsStatusUpdatesReceived::add);

        Thread.sleep(MINIMUM_METHOD_DURATION.toMillis());
    }

    /**
     * Return formatted list count.
     *
     * @param list list to count
     * @return int value with format
     */
    private String getUpdatesCount(final List<?> list) {
        return String.format("%03d", list.size() + 1);
    }

    /**
     * Returns positions updates count.
     *
     * @return positions updates count
     */
    public int getPositionsUpdatesCount() {
        return getPositionsUpdatesReceived().size();
    }

    /**
     * Returns positions status updates count.
     *
     * @return positions status updates count
     */
    public int getPositionsStatusUpdatesCount() {
        return getPositionsStatusUpdatesReceived().size();
    }

    /**
     * Returns last position update.
     *
     * @return last position update
     */
    public PositionDTO getLastPositionUpdate() {
        return getPositionsUpdatesReceived().get(getPositionsUpdatesReceived().size() - 1);
    }

    /**
     * Returns last position status update.
     *
     * @return last position status update
     */
    public PositionDTO getLastPositionStatusUpdate() {
        return getPositionsStatusUpdatesReceived().get(getPositionsStatusUpdatesReceived().size() - 1);
    }

}
