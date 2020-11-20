package tech.cassandre.trading.bot.test.util.strategies;

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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;

/**
 * Testable strategy (used for tests).
 */
@SuppressWarnings("unused")
@CassandreStrategy(name = "Testable strategy")
@ConditionalOnProperty(
        value = PARAMETER_TESTABLE_STRATEGY_ENABLED,
        havingValue = "true")
public class TestableCassandreStrategy extends BasicCassandreStrategy {

    /** Testable strategy enabled parameter. */
    public static final String PARAMETER_TESTABLE_STRATEGY_ENABLED = "testableStrategy.enabled";

    /** Waiting time during each method. */
    public static final int WAITING_TIME_IN_SECONDS = 1;

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
    public final Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        Set<CurrencyPairDTO> requestedTickers = new LinkedHashSet<>();
        requestedTickers.add(new CurrencyPairDTO(ETH, BTC));
        requestedTickers.add(new CurrencyPairDTO(ETH, USDT));
        return requestedTickers;
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        return accounts.stream()
                .filter(a -> "trade".equals(a.getName()))
                .findFirst();
    }

    @Override
    public final void onAccountUpdate(final AccountDTO account) {
        accountsUpdateReceived.add(account);
        logger.info("TestableStrategy-onAccountUpdate " + getCount(accountsUpdateReceived) + " : " + account);
        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        } catch (InterruptedException e) {
            logger.debug("InterruptedException");
        }
    }

    @Override
    public final void onTickerUpdate(final TickerDTO ticker) {
        tickersUpdateReceived.add(ticker);
        logger.info("TestableStrategy-onTickerUpdate " + getCount(tickersUpdateReceived) + " : " + ticker);
        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        } catch (InterruptedException e) {
            logger.debug("InterruptedException");
        }
    }

    @Override
    public final void onOrderUpdate(final OrderDTO order) {
        ordersUpdateReceived.add(order);
        logger.info("TestableStrategy-onOrderUpdate " + getCount(ordersUpdateReceived) + " : " + order);
        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        } catch (InterruptedException e) {
            logger.debug("InterruptedException");
        }
    }

    @Override
    public void onTradeUpdate(TradeDTO trade) {
        tradesUpdateReceived.add(trade);
        logger.info("TestableStrategy-onTradeUpdate " + getCount(tradesUpdateReceived) + " : " + trade);
        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        } catch (InterruptedException e) {
            logger.debug("InterruptedException");
        }
    }

    @Override
    public void onPositionUpdate(PositionDTO position) {
        positionsUpdateReceived.add(position);
        logger.info("TestableStrategy-onPositionUpdate " + getCount(positionsUpdateReceived) + " : " + position);
        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        } catch (InterruptedException e) {
            logger.debug("InterruptedException");
        }
    }

    @Override
    public void onPositionStatusUpdate(PositionDTO position) {
        positionsStatusUpdateReceived.add(position);
        logger.info("TestableStrategy-onPositionStatusUpdate " + getCount(positionsStatusUpdateReceived) + " : " + position);

        try {
            TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        } catch (InterruptedException e) {
            logger.debug("InterruptedException");
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
