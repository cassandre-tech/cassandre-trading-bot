package tech.cassandre.trading.bot.test.core.strategy.multiple;

import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicTa4jCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static tech.cassandre.trading.bot.test.core.strategy.multiple.MultipleStrategiesTest.BTC_ETH;
import static tech.cassandre.trading.bot.test.core.strategy.multiple.Strategy2.PARAMETER_STRATEGY_2_ENABLED;

/**
 * Strategy 2.
 */
@SuppressWarnings("unused")
@CassandreStrategy(
        strategyId = "02",
        strategyName = "Strategy 2")
@ConditionalOnProperty(
        value = PARAMETER_STRATEGY_2_ENABLED,
        havingValue = "true")
@Getter
public class Strategy2 extends BasicTa4jCassandreStrategy {

    /** Strategy enabled parameter. */
    public static final String PARAMETER_STRATEGY_2_ENABLED = "strategy2.enabled";

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

    @Override
    public CurrencyPairDTO getRequestedCurrencyPair() {
        return BTC_ETH;
    }

    @Override
    public int getMaximumBarCount() {
        return 24;
    }

    @Override
    public Duration getDelayBetweenTwoBars() {
        return Duration.ofHours(1);
    }

    @Override
    public Strategy getStrategy() {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(getSeries());
        SMAIndicator sma = new SMAIndicator(closePrice, 3);
        return new BaseStrategy(new UnderIndicatorRule(sma, closePrice), new OverIndicatorRule(sma, closePrice));
    }

    @Override
    public void shouldEnter() {
        logger.info("Strategy2 - Should enter");
    }

    @Override
    public void shouldExit() {
        logger.info("Strategy2 - Should exit");
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        if (accounts.size() == 1) {
            return accounts.stream().findAny();
        } else {
            return accounts.stream()
                    .filter(a -> "trade".equals(a.getName()))
                    .findFirst();
        }
    }

    @Override
    @SneakyThrows
    public final void onAccountsUpdates(final Map<String, AccountDTO> accounts) {
        accounts.values()
                .stream()
                .peek(accountDTO -> logger.info(this.getClass().getSimpleName() + "-onAccountsUpdates n° {} : {} \n ",
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
                .peek(tickerDTO -> logger.info(this.getClass().getSimpleName() + "-onTickersUpdates n° {} : {} \n ",
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
                .peek(orderDTO -> logger.info(this.getClass().getSimpleName() + "-onOrdersUpdates n° {} : {} \n ",
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
                .peek(tradeDTO -> logger.info(this.getClass().getSimpleName() + "-onTradesUpdates n° {} : {} \n ",
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
                .peek(positionDTO -> logger.info(this.getClass().getSimpleName() + "-onPositionsUpdates n° {} : {} \n ",
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
                .peek(positionDTO -> logger.info(this.getClass().getSimpleName() + "-onPositionsStatusUpdates n° {} : {} \n ",
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
     * Returns last position status update.
     *
     * @return last position status update
     */
    public PositionDTO getLastPositionStatusUpdate() {
        return getPositionsStatusUpdatesReceived().get(getPositionsStatusUpdatesReceived().size() - 1);
    }

}
