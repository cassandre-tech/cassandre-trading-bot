package tech.cassandre.trading.bot.test.util.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;

/**
 * Testable strategy (used for tests).
 */
@SuppressWarnings("unused")
@CassandreStrategy(name = "Testable strategy")
@ConditionalOnProperty(
        value = PARAMETER_TESTABLE_STRATEGY_ENABLED,
        havingValue = "true")
public class TestableCassandreStrategy extends BasicCassandreStrategy {

    /** Method duration. */
    private static final long METHOD_DURATION = 1000;

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

    @Override
    public final Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        Set<CurrencyPairDTO> requestedTickers = new LinkedHashSet<>();
        requestedTickers.add(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC));
        requestedTickers.add(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.USDT));
        return requestedTickers;
    }

    @Override
    public final void onAccountUpdate(final AccountDTO account) {
        accountsUpdateReceived.add(account);
        logger.info("TestableStrategy-onAccountUpdate " + getCount(accountsUpdateReceived) + " : " + account);
        try {
            Thread.sleep(METHOD_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void onTickerUpdate(final TickerDTO ticker) {
        tickersUpdateReceived.add(ticker);
        logger.info("TestableStrategy-onTickerUpdate " + getCount(tickersUpdateReceived) + " : " + ticker);
        try {
            Thread.sleep(METHOD_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void onOrderUpdate(final OrderDTO order) {
        ordersUpdateReceived.add(order);
        logger.info("TestableStrategy-onOrderUpdate " + getCount(ordersUpdateReceived) + " : " + order);
        try {
            Thread.sleep(METHOD_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTradeUpdate(TradeDTO trade) {
        tradesUpdateReceived.add(trade);
        logger.info("TestableStrategy-onTradeUpdate " + getCount(tradesUpdateReceived) + " : " + trade);
        try {
            Thread.sleep(METHOD_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPositionUpdate(PositionDTO position) {
        positionsUpdateReceived.add(position);
        logger.info("TestableStrategy-onPositionUpdate " + getCount(positionsUpdateReceived) + " : " + position);
        try {
            Thread.sleep(METHOD_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

}
