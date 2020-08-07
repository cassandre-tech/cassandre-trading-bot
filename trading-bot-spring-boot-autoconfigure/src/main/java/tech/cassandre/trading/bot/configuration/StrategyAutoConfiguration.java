package tech.cassandre.trading.bot.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.ConnectableFlux;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.TradeServiceInDryMode;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.StringJoiner;

/**
 * StrategyAutoConfiguration configures the strategy.
 */
@Configuration
public class StrategyAutoConfiguration extends BaseConfiguration {

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Trade service. */
    private final TradeService tradeService;

    /** Position service. */
    private final PositionService positionService;

    /** Account flux. */
    private final AccountFlux accountFlux;

    /** Ticker flux. */
    private final TickerFlux tickerFlux;

    /** Order flux. */
    private final OrderFlux orderFlux;

    /** Trade flux. */
    private final TradeFlux tradeFlux;

    /** Position flux. */
    private final PositionFlux positionFlux;

    /**
     * Constructor.
     *
     * @param newApplicationContext application context
     * @param newTradeService       trade service
     * @param newPositionService    position service
     * @param newAccountFlux        account flux
     * @param newTickerFlux         ticker flux
     * @param newOrderFlux          order flux
     * @param newTradeFlux          trade flux
     * @param newPositionFlux       position flux
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public StrategyAutoConfiguration(final ApplicationContext newApplicationContext,
                                     final TradeService newTradeService,
                                     final PositionService newPositionService,
                                     final AccountFlux newAccountFlux,
                                     final TickerFlux newTickerFlux,
                                     final OrderFlux newOrderFlux,
                                     final TradeFlux newTradeFlux,
                                     final PositionFlux newPositionFlux) {
        this.applicationContext = newApplicationContext;
        this.tradeService = newTradeService;
        this.positionService = newPositionService;
        this.accountFlux = newAccountFlux;
        this.tickerFlux = newTickerFlux;
        this.orderFlux = newOrderFlux;
        this.tradeFlux = newTradeFlux;
        this.positionFlux = newPositionFlux;
    }

    /**
     * Search for the strategy and runs it.
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
        if (!(o instanceof CassandreStrategyInterface)) {
            throw new ConfigurationException("Your strategy doesn't extend BasicCassandreStrategy or BasicTa4jCassandreStrategy",
                    o.getClass() + " must extend BasicCassandreStrategy or BasicTa4jCassandreStrategy");
        }

        // =============================================================================================================
        // Getting strategy information.
        CassandreStrategyInterface strategy = (CassandreStrategyInterface) o;

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

        // Setting services.
        strategy.setTradeService(tradeService);
        strategy.setPositionService(positionService);

        // Account flux.
        final ConnectableFlux<AccountDTO> connectableAccountFlux = accountFlux.getFlux().publish();
        connectableAccountFlux.subscribe(strategy::accountUpdate);
        connectableAccountFlux.connect();

        // Position flux.
        final ConnectableFlux<PositionDTO> connectablePositionFlux = positionFlux.getFlux().publish();
        connectablePositionFlux.subscribe(strategy::positionUpdate);
        connectablePositionFlux.connect();

        // Order flux.
        final ConnectableFlux<OrderDTO> connectableOrderFlux = orderFlux.getFlux().publish();
        connectableOrderFlux.subscribe(strategy::orderUpdate);
        connectableOrderFlux.connect();

        // Trade flux to strategy.
        final ConnectableFlux<TradeDTO> connectableTradeFlux = tradeFlux.getFlux().publish();
        connectableTradeFlux.subscribe(strategy::tradeUpdate);              // For strategy.
        connectableTradeFlux.subscribe(positionService::tradeUpdate);       // For position service.
        connectableTradeFlux.connect();

        // Ticker flux.
        tickerFlux.updateRequestedCurrencyPairs(strategy.getRequestedCurrencyPairs());
        final ConnectableFlux<TickerDTO> connectableTickerFlux = tickerFlux.getFlux().publish();
        connectableTickerFlux.subscribe(strategy::tickerUpdate);            // For strategy.
        connectableTickerFlux.subscribe(positionService::tickerUpdate);     // For position service.
        // if in dry mode, we send the ticker to the dry mode.
        if (tradeService instanceof TradeServiceInDryMode) {
            connectableTickerFlux.subscribe(((TradeServiceInDryMode) tradeService)::tickerUpdate);
        }

        connectableTickerFlux.connect();
    }

}
