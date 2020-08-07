package tech.cassandre.trading.bot.strategy;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Basic strategy - Cassandre bot will run the first BasicCassandreStrategy implementation found.
 */
@SuppressWarnings("unused")
public abstract class BasicCassandreStrategy implements CassandreStrategyInterface {

    /** Trade service. */
    private TradeService tradeService;

    /** Position service. */
    private PositionService positionService;

    /** The accounts owned by the user. */
    private final Map<String, AccountDTO> accounts = new LinkedHashMap<>();

    /** The orders owned by the user. */
    private final Map<String, OrderDTO> orders = new LinkedHashMap<>();

    /** The trades owned by the user. */
    private final Map<String, TradeDTO> trades = new LinkedHashMap<>();

    /** The positions owned by the user. */
    private final Map<Long, PositionDTO> positions = new LinkedHashMap<>();

    @Override
    public final void setTradeService(final TradeService newTradeService) {
        this.tradeService = newTradeService;
    }

    @Override
    public final void setPositionService(final PositionService newPositionService) {
        this.positionService = newPositionService;
    }

    @Override
    public final TradeService getTradeService() {
        return tradeService;
    }

    @Override
    public final PositionService getPositionService() {
        return positionService;
    }

    @Override
    public final void accountUpdate(final AccountDTO account) {
        accounts.put(account.getId(), account);
        onAccountUpdate(account);
    }

    @Override
    public final void tickerUpdate(final TickerDTO ticker) {
        onTickerUpdate(ticker);
    }

    @Override
    public final void orderUpdate(final OrderDTO order) {
        orders.put(order.getId(), order);
        onOrderUpdate(order);
    }

    @Override
    public final void tradeUpdate(final TradeDTO trade) {
        trades.put(trade.getId(), trade);
        onTradeUpdate(trade);
    }

    @Override
    public final void positionUpdate(final PositionDTO position) {
        positions.put(position.getId(), position);
        onPositionUpdate(position);
    }

    /**
     * Getter for of accounts.
     *
     * @return accounts
     */
    public final Map<String, AccountDTO> getAccounts() {
        return accounts;
    }

    /**
     * Getter for of orders.
     *
     * @return orders
     */
    public final Map<String, OrderDTO> getOrders() {
        return orders;
    }

    /**
     * Getter for of trades.
     *
     * @return trades
     */
    public final Map<String, TradeDTO> getTrades() {
        return trades;
    }

    /**
     * Getter for of positions.
     *
     * @return positions
     */
    public final Map<Long, PositionDTO> getPositions() {
        return positions;
    }

    @Override
    public void onAccountUpdate(final AccountDTO account) {

    }

    @Override
    public void onTickerUpdate(final TickerDTO ticker) {

    }

    @Override
    public void onOrderUpdate(final OrderDTO order) {

    }

    @Override
    public void onTradeUpdate(final TradeDTO trade) {

    }

    @Override
    public void onPositionUpdate(final PositionDTO position) {

    }

}
