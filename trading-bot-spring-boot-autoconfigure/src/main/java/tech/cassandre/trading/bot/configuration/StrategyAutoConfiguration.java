package tech.cassandre.trading.bot.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.util.base.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.StringJoiner;

/**
 * StrategyAutoConfiguration class configures the strategy.
 */
@Configuration
public class StrategyAutoConfiguration extends BaseConfiguration {

    /** Number of threads. */
    private static final int NUMBER_OF_THREADS = 4;

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Scheduler. */
    private final Scheduler scheduler = Schedulers.newParallel("strategy-scheduler", NUMBER_OF_THREADS);

    /** Trade service. */
    private final TradeService tradeService;

    /** Account flux. */
    private final AccountFlux accountFlux;

    /** Ticker flux. */
    private final TickerFlux tickerFlux;

    /** Order flux. */
    private final OrderFlux orderFlux;

    /** Trade flux. */
    private final TradeFlux tradeFlux;

    /**
     * Constructor.
     *
     * @param newApplicationContext application context
     * @param newTradeService       trade service
     * @param newAccountFlux        account flux
     * @param newTickerFlux         ticker flux
     * @param newOrderFlux          order flux.
     * @param newTradeFlux          trade flux.
     */
    public StrategyAutoConfiguration(final ApplicationContext newApplicationContext,
                                     final TradeService newTradeService,
                                     final AccountFlux newAccountFlux,
                                     final TickerFlux newTickerFlux,
                                     final OrderFlux newOrderFlux,
                                     final TradeFlux newTradeFlux) {
        this.applicationContext = newApplicationContext;
        this.tradeService = newTradeService;
        this.accountFlux = newAccountFlux;
        this.tickerFlux = newTickerFlux;
        this.orderFlux = newOrderFlux;
        this.tradeFlux = newTradeFlux;
    }

    /**
     * Search for the strategy and instantiate it.
     */
    @PostConstruct
    public void configure() {
        // Retrieving all the beans have the annotation @Strategy.
        final Map<String, Object> strategyBeans = applicationContext.getBeansWithAnnotation(CassandreStrategy.class);

        // =============================================================================================================
        // Check if everything is ok.

        // Check if there is no strategy.
        if (strategyBeans.isEmpty()) {
            getLogger().error("No strategy found");
            throw new ConfigurationException("No strategy found",
                    "You must have one class with @Strategy");
        }

        // Check if there are several strategies.
        if (strategyBeans.size() > 1) {
            getLogger().error("Several strategies found");
            strategyBeans.forEach((s, o) -> getLogger().error(" - " + s));
            throw new ConfigurationException("Several strategies found",
                    "Cassandre trading bot only supports one strategy at a time (@Strategy)");
        }

        // Check if the strategy extends CassandreStrategy.
        Object o = strategyBeans.values().iterator().next();
        if (!(o instanceof BasicCassandreStrategy)) {
            throw new ConfigurationException("Your strategy doesn't extend CassandreStrategy",
                    o.getClass() + " must extend CassandreStrategy");
        }

        // =============================================================================================================
        // Getting strategy information.
        BasicCassandreStrategy strategy = (BasicCassandreStrategy) o;

        // Displaying strategy name.
        CassandreStrategy cassandreStrategyAnnotation = o.getClass().getAnnotation(CassandreStrategy.class);
        getLogger().info("StrategyConfiguration - Running strategy '{}'", cassandreStrategyAnnotation.name());

        // Displaying requested currency pairs.
        StringJoiner currencyPairList = new StringJoiner(", ");
        strategy.getRequestedCurrencyPairs()
                .forEach(currencyPair -> currencyPairList.add(currencyPair.toString()));
        getLogger().info("StrategyConfiguration - The strategy requires the following currency pair(s) : " + currencyPairList);

        // =============================================================================================================
        // Setting up strategy.

        // Setting service.
        strategy.setTradeService(tradeService);

        // Account flux.
        accountFlux.getFlux()
                .publishOn(scheduler)
                .subscribe(strategy::accountUpdate);
        // Ticker flux.
        tickerFlux.updateRequestedCurrencyPairs(strategy.getRequestedCurrencyPairs());
        tickerFlux.getFlux()
                .publishOn(scheduler)
                .subscribe(strategy::tickerUpdate);
        // Order flux.
        orderFlux.getFlux()
                .publishOn(scheduler)
                .subscribe(strategy::orderUpdate);
        // Trade flux.
        tradeFlux.getFlux()
                .publishOn(scheduler)
                .subscribe(strategy::tradeUpdate);
    }

}
