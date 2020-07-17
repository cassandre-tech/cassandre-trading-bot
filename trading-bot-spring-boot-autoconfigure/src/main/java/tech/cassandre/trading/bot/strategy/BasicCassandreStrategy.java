package tech.cassandre.trading.bot.strategy;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Basic strategy - Cassandre bot will run the first CassandreStrategy implementation found.
 */
@SuppressWarnings("unused")
public abstract class BasicCassandreStrategy {

    /** Trade service. */
    private TradeService tradeService;

    /** The accounts owned by the user. */
    private final Map<String, AccountDTO> accounts = new LinkedHashMap<>();

    /** The orders owned by the user. */
    private final Map<String, OrderDTO> orders = new LinkedHashMap<>();

    /** The trades owned by the user. */
    private final Map<String, TradeDTO> trades = new LinkedHashMap<>();

    /** The positions owned by the user. */
    private final Map<Long, PositionDTO> positions = new LinkedHashMap<>();

    /**
     * Getter tradeService.
     *
     * @return tradeService
     */
    public final TradeService getTradeService() {
        return tradeService;
    }

    /**
     * Setter tradeService.
     *
     * @param newTradeService the tradeService to set
     */
    public final void setTradeService(final TradeService newTradeService) {
        tradeService = newTradeService;
    }

    /**
     * Implements this method to tell the bot which currency pairs your strategy will receive.
     *
     * @return the list of currency pairs tickers your want to receive
     */
    public abstract Set<CurrencyPairDTO> getRequestedCurrencyPairs();

    /**
     * Method called by streams at every account update.
     *
     * @param account account
     */
    public void accountUpdate(final AccountDTO account) {
        accounts.put(account.getId(), account);
        onAccountUpdate(account);
    }

    /**
     * Method called by streams at every ticker update.
     *
     * @param ticker ticker
     */
    public void tickerUpdate(final TickerDTO ticker) {
        onTickerUpdate(ticker);
    }

    /**
     * Method called by streams on every order update.
     *
     * @param order order
     */
    public void orderUpdate(final OrderDTO order) {
        orders.put(order.getId(), order);
        onOrderUpdate(order);
    }

    /**
     * Method called by streams on every trade update.
     *
     * @param trade trade
     */
    public void tradeUpdate(final TradeDTO trade) {
        trades.put(trade.getId(), trade);
        onTradeUpdate(trade);
    }

    /**
     * Method called by streams on every position update.
     *
     * @param position trade
     */
    public void positionUpdate(final PositionDTO position) {
        positions.put(position.getId(), position);
        onPositionUpdate(position);
    }

    /**
     * Getter of accounts.
     *
     * @return accounts
     */
    public final Map<String, AccountDTO> getAccounts() {
        return accounts;
    }

    /**
     * Getter of orders.
     *
     * @return orders
     */
    public final Map<String, OrderDTO> getOrders() {
        return orders;
    }

    /**
     * Getter of trades.
     *
     * @return trades
     */
    public final Map<String, TradeDTO> getTrades() {
        return trades;
    }

    /**
     * Getter of positions.
     *
     * @return positions
     */
    public final Map<Long, PositionDTO> getPositions() {
        return positions;
    }

    /**
     * Method triggered at every account update.
     *
     * @param account account
     */
    public void onAccountUpdate(final AccountDTO account) {
    }

    /**
     * Method triggered at every ticker update.
     *
     * @param ticker ticker
     */
    public void onTickerUpdate(final TickerDTO ticker) {
    }

    /**
     * Method triggered on every order update.
     *
     * @param order order
     */
    public void onOrderUpdate(final OrderDTO order) {
    }

    /**
     * Method triggered on every trade update.
     *
     * @param trade trade
     */
    public void onTradeUpdate(final TradeDTO trade) {
    }

    /**
     * Method triggered on every position update.
     *
     * @param position position
     */
    public void onPositionUpdate(final PositionDTO position) {
    }

}
