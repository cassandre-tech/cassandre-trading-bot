package tech.cassandre.trading.bot.strategy.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.repository.ImportedCandleRepository;
import tech.cassandre.trading.bot.repository.ImportedTickerRepository;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;

import static lombok.AccessLevel.PRIVATE;

/**
 * CassandreStrategyDependencies contains all the dependencies required by a strategy and provided by the Cassandre framework.
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
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class CassandreStrategyDependencies {

    // =================================================================================================================
    // Flux.

    /** Position flux. */
    PositionFlux positionFlux;

    // =================================================================================================================
    // Repositories.

    /** Order repository. */
    OrderRepository orderRepository;

    /** Trade repository. */
    TradeRepository tradeRepository;

    /** Position repository. */
    PositionRepository positionRepository;

    /** "Imported candles" repository. */
    ImportedCandleRepository importedCandleRepository;

    /** "Imported tickers" repository. */
    ImportedTickerRepository importedTickerRepository;

    // =================================================================================================================
    // Services.

    /** Exchange service. */
    ExchangeService exchangeService;

    /** Trade service. */
    TradeService tradeService;

    /** Position service. */
    PositionService positionService;

}
