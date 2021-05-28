package tech.cassandre.trading.bot.test.strategy.multiple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Abstract class for strategy.
 */
public abstract class Strategy extends BasicCassandreStrategy {

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
    public final void onTickersUpdate(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        tickersUpdateReceived.addAll(tickers.values());
        tickers.values().forEach(ticker -> logger.info(this.getClass().getSimpleName() + "-onTickersUpdate " + getCount(tickersUpdateReceived) + " : " + ticker + "\n"));
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
