package tech.cassandre.trading.bot.test.util.strategies;

import lombok.Getter;
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

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.EUR;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.BTC_ETH;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.BTC_USDT;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.ETH_BTC;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.ETH_USDT;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.KCS_USDT;
import static tech.cassandre.trading.bot.test.util.strategies.LargeTestableCassandreStrategy.PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED;

/**
 * Testable strategy (used for tests).
 */
@SuppressWarnings("unused")
@CassandreStrategy(
        strategyId = "01",
        strategyName = "Large testable strategy")
@ConditionalOnProperty(
        value = PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED,
        havingValue = "true")
@Getter
public class LargeTestableCassandreStrategy extends BasicCassandreStrategy {

    /** Testable strategy enabled parameter. */
    public static final String PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED = "largeTestableStrategy.enabled";

    /** Waiting time during each method. */
    public static final int WAITING_TIME_IN_SECONDS = 1;

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** Accounts update received. */
    private final List<AccountDTO> accountsUpdatesReceived = new LinkedList<>();

    /** Tickers update received. */
    private final List<TickerDTO> tickersUpdatesReceived = new LinkedList<>();

    /** Orders update received. */
    private final List<OrderDTO> ordersUpdatesReceived = new LinkedList<>();

    /** Trades update received. */
    private final List<TradeDTO> tradesUpdatesReceived = new LinkedList<>();

    /** Positions update received. */
    private final List<PositionDTO> positionsUpdatesReceived = new LinkedList<>();

    /** Positions status update received. */
    private final List<PositionDTO> positionsStatusUpdatesReceived = new LinkedList<>();

    @Override
    public final Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        Set<CurrencyPairDTO> requestedTickers = new LinkedHashSet<>();
        requestedTickers.add(ETH_BTC);
        requestedTickers.add(ETH_USDT);
        requestedTickers.add(BTC_USDT);
        requestedTickers.add(KCS_USDT);
        requestedTickers.add(BTC_ETH);
        requestedTickers.add(new CurrencyPairDTO(ETH, EUR));
        return requestedTickers;
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
    public final void onAccountsUpdates(final Map<String, AccountDTO> accounts) {
        accounts.values()
                .stream()
                .peek(accountDTO -> logger.info("TestableStrategy-onAccountsUpdates n° {} : {} \n ",
                        getCount(accountsUpdatesReceived),
                        accountDTO))
                .forEach(accountsUpdatesReceived::add);

        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public final void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        tickers.values()
                .stream()
                .peek(tickerDTO -> logger.info("TestableStrategy-onTickersUpdates n° {} : {} \n ",
                        getCount(tickersUpdatesReceived),
                        tickerDTO))
                .forEach(tickersUpdatesReceived::add);

        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public final void onOrdersUpdates(final Map<String, OrderDTO> orders) {
        orders.values()
                .stream()
                .peek(orderDTO -> logger.info("TestableStrategy-onOrdersUpdates n° {} : {} \n ",
                        getCount(ordersUpdatesReceived),
                        orderDTO))
                .forEach(ordersUpdatesReceived::add);

        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onTradesUpdates(final Map<String, TradeDTO> trades) {
        trades.values()
                .stream()
                .peek(tradeDTO -> logger.info("TestableStrategy-onTradesUpdates n° {} : {} \n ",
                        getCount(tradesUpdatesReceived),
                        tradeDTO))
                .forEach(tradesUpdatesReceived::add);

        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onPositionsUpdates(final Map<Long, PositionDTO> positions) {
        positions.values()
                .stream()
                .peek(positionDTO -> logger.info("TestableStrategy-onPositionsUpdates n° {} : {} \n ",
                        getCount(positionsUpdatesReceived),
                        positionDTO))
                .forEach(positionsUpdatesReceived::add);

        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void onPositionsStatusUpdates(final Map<Long, PositionDTO> positions) {
        positions.values()
                .stream()
                .peek(positionDTO -> logger.info("TestableStrategy-onPositionsStatusUpdates n° {} : {} \n ",
                        getCount(positionsStatusUpdatesReceived),
                        positionDTO))
                .forEach(positionsStatusUpdatesReceived::add);

        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
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
        return String.format("%03d", list.size() + 1);
    }

}
