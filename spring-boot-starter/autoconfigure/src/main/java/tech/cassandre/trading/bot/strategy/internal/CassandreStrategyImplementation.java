package tech.cassandre.trading.bot.strategy.internal;

import lombok.NonNull;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.util.base.strategy.BaseStrategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;

/**
 * CassandreStrategyImplementation is the default implementation of CassandreStrategyInterface, this code manages the interaction between Cassandre framework and a strategy.
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
public abstract class CassandreStrategyImplementation extends BaseStrategy implements CassandreStrategyInterface {

    /** Cassandre configuration. */
    protected CassandreStrategyConfiguration configuration;

    /** Cassandre dependencies. */
    protected CassandreStrategyDependencies dependencies;

    /** The accounts owned by the user. */
    protected final Map<String, AccountDTO> userAccounts = new LinkedHashMap<>();

    /** Last tickers received. */
    protected final Map<CurrencyPairDTO, TickerDTO> lastTickers = new LinkedHashMap<>();

    /** Positions previous status - used for onPositionsStatusUpdates() - Internal use only. */
    private final Map<Long, PositionStatusDTO> previousPositionsStatus = new LinkedHashMap<>();

    // =================================================================================================================
    // Configuration & dependencies set by Cassandre.

    @Override
    public final void setConfiguration(final CassandreStrategyConfiguration cassandreStrategyConfiguration) {
        configuration = cassandreStrategyConfiguration;
    }

    @Override
    public final CassandreStrategyConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public final void setDependencies(final CassandreStrategyDependencies cassandreStrategyDependencies) {
        dependencies = cassandreStrategyDependencies;
        // To manage onPositionsStatusUpdates, we retrieve the positions' status from our database.
        dependencies.getPositionRepository()
                .findByStatusNot(CLOSED)
                .forEach(position -> previousPositionsStatus.put(position.getUid(), position.getStatus()));
    }

    // =================================================================================================================
    // Strategy initialization.

    @Override
    public final void initializeAccounts(final Map<String, AccountDTO> newAccounts) {
        userAccounts.putAll(newAccounts);
    }

    // =================================================================================================================
    // Internal methods called by Cassandre streams on data update.

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public void accountsUpdates(final Set<AccountDTO> accounts) {
        // We notify the strategy.
        onAccountsUpdates(accounts.stream()
                // We store the account values in the strategy.
                .peek(accountDTO -> userAccounts.put(accountDTO.getAccountId(), accountDTO))
                .collect(Collectors.toMap(AccountDTO::getAccountId, Function.identity(), (id, value) -> id, LinkedHashMap::new)));
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public void tickersUpdates(final Set<TickerDTO> tickers) {
        // We only retrieve the tickers requested by the strategy (in real time).
        final Set<CurrencyPairDTO> requestedCurrencyPairs = getRequestedCurrencyPairs();

        // We build the results.
        final Map<CurrencyPairDTO, TickerDTO> tickersUpdates = tickers.stream()
                .filter(tickerDTO -> requestedCurrencyPairs.contains(tickerDTO.getCurrencyPair()))
                // We also update the values of the last tickers received by the strategy.
                .peek(tickerDTO -> lastTickers.put(tickerDTO.getCurrencyPair(), tickerDTO))
                .collect(Collectors.toMap(TickerDTO::getCurrencyPair, Function.identity(), (id, value) -> id, LinkedHashMap::new));

        // We update the opened positions with tickers.
        updatePositionsWithTickersUpdates(tickersUpdates);

        // We notify the strategy.
        onTickersUpdates(tickersUpdates);
    }

    @Override
    public final BigDecimal getLastPriceForCurrencyPair(@NonNull final CurrencyPairDTO currencyPair) {
        return getLastTickerByCurrencyPair(currencyPair).map(TickerDTO::getLast).orElse(null);
    }

    /**
     * Return the last ticker for a currency pair.
     *
     * @param currencyPair currency pair
     * @return last ticker received
     */
    public final Optional<TickerDTO> getLastTickerByCurrencyPair(@NonNull final CurrencyPairDTO currencyPair) {
        return Optional.ofNullable(lastTickers.get(currencyPair));
    }

    @Override
    public final void ordersUpdates(final Set<OrderDTO> orders) {
        // We only retrieve the orders created by this strategy.
        final Map<String, OrderDTO> ordersUpdates = orders.stream()
                .filter(orderDTO -> orderDTO.getStrategy().getUid().equals(configuration.getStrategyUid()))
                .collect(Collectors.toMap(OrderDTO::getOrderId, Function.identity(), (id, value) -> id, LinkedHashMap::new));

        // We update the positions with orders.
        updatePositionsWithOrdersUpdates(ordersUpdates);

        // We notify the strategy.
        onOrdersUpdates(ordersUpdates);
    }

    @Override
    public final void tradesUpdates(final Set<TradeDTO> trades) {
        // We only retrieve the trades created by this strategy.
        final Map<String, TradeDTO> tradesUpdates = trades.stream()
                .filter(tradeDTO -> tradeDTO.getOrder().getStrategy().getUid().equals(configuration.getStrategyUid()))
                .collect(Collectors.toMap(TradeDTO::getTradeId, Function.identity(), (id, value) -> id, LinkedHashMap::new));

        // We update the positions with trades.
        updatePositionsWithTradesUpdates(tradesUpdates);

        // We notify the strategy.
        onTradesUpdates(tradesUpdates);
    }

    @Override
    public final void positionsUpdates(final Set<PositionDTO> positions) {
        final Map<Long, PositionDTO> positionsUpdates = new HashMap<>();
        final Map<Long, PositionDTO> positionsStatusUpdates = new HashMap<>();

        positions.stream()
                .filter(positionDTO -> positionDTO.getStrategy().getUid().equals(configuration.getStrategyUid()))
                .forEach(positionDTO -> {
                    positionsUpdates.put(positionDTO.getPositionId(), positionDTO);

                    // It's a position update, but we see if it's also a onPositionStatusUpdate().
                    if (previousPositionsStatus.get(positionDTO.getUid()) != positionDTO.getStatus()) {
                        if (positionDTO.getStatus() == CLOSED) {
                            // As CLOSED positions cannot change anymore, we don't need to store their previous positions.
                            previousPositionsStatus.remove(positionDTO.getUid());
                        } else {
                            // As we have a new status for this position, we update it in previousPositionsStatus.
                            previousPositionsStatus.put(positionDTO.getUid(), positionDTO.getStatus());
                        }
                        positionsStatusUpdates.put(positionDTO.getPositionId(), positionDTO);
                    }
                });

        // We notify the strategy (positions updates & positions status updates).
        onPositionsUpdates(positionsUpdates);
        onPositionsStatusUpdates(positionsStatusUpdates);
    }

    // =================================================================================================================
    // Internal methods that updates positions with other data updates.

    /**
     * Update strategy positions with tickers updates.
     *
     * @param tickers tickers updates
     */
    protected void updatePositionsWithTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        // We check if any ticker updates a position, and we close if it's time.
        dependencies.getPositionRepository()
                .findByStatusNot(CLOSED)
                .stream()
                .map(POSITION_MAPPER::mapToPositionDTO)
                // Only the positions of this strategy.
                .filter(positionDTO -> positionDTO.getStrategy().getUid().equals(configuration.getStrategyUid()))
                // Only if we received the ticker used by the position.
                .filter(positionDTO -> tickers.get(positionDTO.getCurrencyPair()) != null)
                // We send the ticker corresponding to the currency pair of the position. If it returns true, we emit because price changed.
                .filter(positionDTO -> positionDTO.tickerUpdate(tickers.get(positionDTO.getCurrencyPair())))
                .peek(positionDTO -> logger.debug("Position {} updated with ticker {}", positionDTO.getPositionId(), tickers.get(positionDTO.getCurrencyPair())))
                .peek(positionDTO -> dependencies.getPositionFlux().emitValue(positionDTO))
                // We only use tickers updates to close a position if position is set to autoclose.
                .filter(PositionDTO::isAutoClose)
                // We check if the position should be closed, if true, we closed and position service will emit the position.
                .filter(PositionDTO::shouldBeClosed)
                .peek(positionDTO -> logger.debug("Closing position {}", positionDTO.getPositionId()))
                .map(positionDTO -> dependencies.getPositionService().closePosition(this,
                        positionDTO.getUid(),
                        tickers.get(positionDTO.getCurrencyPair())))
                // Creation was not successful, we display a log.
                .filter(orderCreationResultDTO -> !orderCreationResultDTO.isSuccessful())
                .forEach(orderCreationResultDTO -> logger.error("Impossible to close position: {}", orderCreationResultDTO.getErrorMessage()));
    }

    /**
     * Update strategy positions with orders updates.
     *
     * @param orders orders updates
     */
    void updatePositionsWithOrdersUpdates(final Map<String, OrderDTO> orders) {
        // We check if any order updates a position.
        orders.values().forEach(orderDTO -> dependencies.getPositionRepository()
                .findByStatusNot(CLOSED)
                .stream()
                .map(POSITION_MAPPER::mapToPositionDTO)
                .filter(positionDTO -> positionDTO.getStrategy().getUid().equals(configuration.getStrategyUid()))
                .filter(positionDTO -> positionDTO.orderUpdate(orderDTO))
                .peek(positionDTO -> logger.debug("Position {} updated with order {}", positionDTO.getPositionId(), orderDTO))
                .forEach(dependencies.getPositionFlux()::emitValue));
    }

    /**
     * Update strategy positions with trades updates.
     *
     * @param trades trades updates
     */
    void updatePositionsWithTradesUpdates(final Map<String, TradeDTO> trades) {
        // We check if any trade updates a position.
        trades.values().forEach(tradeDTO -> dependencies.getPositionRepository()
                .findByStatusNot(CLOSED)
                .stream()
                .map(POSITION_MAPPER::mapToPositionDTO)
                .filter(positionDTO -> positionDTO.getStrategy().getUid().equals(configuration.getStrategyUid()))
                .filter(positionDTO -> positionDTO.tradeUpdate(tradeDTO))
                .peek(positionDTO -> logger.debug("Position {} updated with trade {}", positionDTO.getPositionId(), tradeDTO))
                .forEach(dependencies.getPositionFlux()::emitValue));
    }

}
