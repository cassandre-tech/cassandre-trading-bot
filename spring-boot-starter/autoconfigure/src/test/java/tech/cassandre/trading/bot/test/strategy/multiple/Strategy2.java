package tech.cassandre.trading.bot.test.strategy.multiple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicTa4jCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static tech.cassandre.trading.bot.test.strategy.multiple.MultipleStrategiesTest.BTC_ETH;
import static tech.cassandre.trading.bot.test.strategy.multiple.Strategy2.PARAMETER_STRATEGY_2_ENABLED;

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
public class Strategy2 extends BasicTa4jCassandreStrategy {

    /** Strategy enabled parameter. */
    public static final String PARAMETER_STRATEGY_2_ENABLED = "strategy2.enabled";

    /** Waiting time during each method. */
    public static final int WAITING_TIME_IN_MILLISECONDS = 10;

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** Accounts update received. */
    private final List<AccountDTO> accountsUpdateReceived = new LinkedList<>();

    /** Tickers update received. */
    private final List<TickerDTO> tickersUpdateReceived = new LinkedList<>();

    /** Orders update received. */
    private final List<OrderDTO> ordersUpdateReceived = new LinkedList<>();

    /** Trades update received. */
    private final List<TradeDTO> tradesUpdateReceived = new LinkedList<>();

    /** Positions update received. */
    private final List<PositionDTO> positionsUpdateReceived = new LinkedList<>();

    /** Positions status update received. */
    private final List<PositionDTO> positionsStatusUpdateReceived = new LinkedList<>();

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

    }

    @Override
    public void shouldExit() {

    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        if (accounts.size() == 1) {
            // Used for Gemini integration tests.
            return accounts.stream().findAny();
        } else {
            return accounts.stream()
                    .filter(a -> "trade".equals(a.getName()))
                    .findFirst();
        }
    }

    @Override
    public final void onAccountUpdate(final AccountDTO account) {
        accountsUpdateReceived.add(account);
        logger.info(getClass().getSimpleName() + "-onAccountUpdate " + getCount(accountsUpdateReceived) + " : " + account + "\n");
        try {
            TimeUnit.MILLISECONDS.sleep(WAITING_TIME_IN_MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public final void onTickerUpdate(final TickerDTO ticker) {
        tickersUpdateReceived.add(ticker);
        logger.info(getClass().getSimpleName() + "-onTickerUpdate " + getCount(tickersUpdateReceived) + " : " + ticker + "\n");
        try {
            TimeUnit.MILLISECONDS.sleep(WAITING_TIME_IN_MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public final void onOrderUpdate(final OrderDTO order) {
        ordersUpdateReceived.add(order);
        logger.info(getClass().getSimpleName() + "-onOrderUpdate " + getCount(ordersUpdateReceived) + " : " + order + "\n");
        try {
            TimeUnit.MILLISECONDS.sleep(WAITING_TIME_IN_MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onTradeUpdate(TradeDTO trade) {
        tradesUpdateReceived.add(trade);
        logger.info(getClass().getSimpleName() + "-onTradeUpdate " + getCount(tradesUpdateReceived) + " : " + trade + "\n");
        try {
            TimeUnit.MILLISECONDS.sleep(WAITING_TIME_IN_MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onPositionUpdate(PositionDTO position) {
        positionsUpdateReceived.add(position);
        logger.info(getClass().getSimpleName() + "-onPositionUpdate " + getCount(positionsUpdateReceived) + " : " + position + "\n");
        try {
            TimeUnit.MILLISECONDS.sleep(WAITING_TIME_IN_MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onPositionStatusUpdate(PositionDTO position) {
        positionsStatusUpdateReceived.add(position);
        logger.info(getClass().getSimpleName() + "-onPositionStatusUpdate " + getCount(positionsStatusUpdateReceived) + " : " + position + "\n");

        try {
            TimeUnit.MILLISECONDS.sleep(WAITING_TIME_IN_MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Return formatted list count.
     *
     * @param list list to count
     * @return int value with format
     */
    private String getCount(final List<?> list) {
        return String.format("%03d", list.size());
    }

    /**
     * Getter lastAccountsReceived.
     *
     * @return last accounts received.
     */
    public final List<AccountDTO> getAccountsUpdatesReceived() {
        return accountsUpdateReceived;
    }

    /**
     * Getter lastTickersReceived.
     *
     * @return lastTickersReceived
     */
    public final List<TickerDTO> getTickersUpdateReceived() {
        return tickersUpdateReceived;
    }

    /**
     * Getter lastOrdersReceived.
     *
     * @return lastOrderReceived
     */
    public final List<OrderDTO> getOrdersUpdateReceived() {
        return ordersUpdateReceived;
    }

    /**
     * Getter tradesUpdateReceived.
     *
     * @return tradesUpdateReceived
     */
    public final List<TradeDTO> getTradesUpdateReceived() {
        return tradesUpdateReceived;
    }

    /**
     * Getter accountsUpdateReceived.
     *
     * @return accountsUpdateReceived
     */
    public final List<AccountDTO> getAccountsUpdateReceived() {
        return accountsUpdateReceived;
    }

    /**
     * Getter positionsUpdateReceived.
     *
     * @return positionsUpdateReceived
     */
    public final List<PositionDTO> getPositionsUpdateReceived() {
        return positionsUpdateReceived;
    }

    /**
     * Getter positionsStatusUpdateReceived.
     *
     * @return positionsStatusUpdateReceived
     */
    public final List<PositionDTO> getPositionsStatusUpdateReceived() {
        return positionsStatusUpdateReceived;
    }

}
