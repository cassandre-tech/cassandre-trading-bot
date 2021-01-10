package tech.cassandre.trading.bot.configuration;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.ConnectableFlux;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.ExchangeAccount;
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.repository.ExchangeAccountRepository;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.StrategyRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.service.dry.TradeServiceDryModeImplementation;
import tech.cassandre.trading.bot.service.dry.UserServiceDryModeImplementation;
import tech.cassandre.trading.bot.service.intern.PositionServiceImplementation;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.BasicTa4jCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategyInterface;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;
import tech.cassandre.trading.bot.util.base.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;
import tech.cassandre.trading.bot.util.parameters.ExchangeParameters;

import javax.annotation.PostConstruct;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

import static tech.cassandre.trading.bot.dto.strategy.StrategyTypeDTO.BASIC_STRATEGY;
import static tech.cassandre.trading.bot.dto.strategy.StrategyTypeDTO.BASIC_TA4J_STRATEGY;

/**
 * StrategyAutoConfiguration configures the strategy.
 */
@Configuration
public class StrategyAutoConfiguration extends BaseConfiguration {

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Exchange parameters. */
    private final ExchangeParameters exchangeParameters;

    /** Trade service. */
    private final TradeService tradeService;

    /** Position service. */
    private PositionService positionService;

    /** User service. */
    private final UserService userService;

    /** Account flux. */
    private final AccountFlux accountFlux;

    /** Ticker flux. */
    private final TickerFlux tickerFlux;

    /** Order flux. */
    private final OrderFlux orderFlux;

    /** Exchange account repository. */
    private final ExchangeAccountRepository exchangeAccountRepository;

    /** Strategy repository. */
    private final StrategyRepository strategyRepository;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Position repository. */
    private final PositionRepository positionRepository;

    /** Trade flux. */
    private final TradeFlux tradeFlux;

    /** Position flux. */
    private final PositionFlux positionFlux;

    /**
     * Constructor.
     *
     * @param newApplicationContext        application context
     * @param newExchangeParameters        exchange parameters
     * @param newUserService               user service
     * @param newTradeService              trade service
     * @param newAccountFlux               account flux
     * @param newTickerFlux                ticker flux
     * @param newOrderFlux                 order flux
     * @param newTradeFlux                 trade flux
     * @param newExchangeAccountRepository exchange account repository
     * @param newStrategyRepository        strategy repository
     * @param newOrderRepository           order repository
     * @param newTradeRepository           trade repository
     * @param newPositionRepository        position repository
     * @param newPositionFlux              position flux
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public StrategyAutoConfiguration(final ApplicationContext newApplicationContext,
                                     final ExchangeParameters newExchangeParameters,
                                     final UserService newUserService,
                                     final TradeService newTradeService,
                                     final AccountFlux newAccountFlux,
                                     final TickerFlux newTickerFlux,
                                     final OrderFlux newOrderFlux,
                                     final TradeFlux newTradeFlux,
                                     final ExchangeAccountRepository newExchangeAccountRepository,
                                     final StrategyRepository newStrategyRepository,
                                     final OrderRepository newOrderRepository,
                                     final TradeRepository newTradeRepository,
                                     final PositionRepository newPositionRepository,
                                     final PositionFlux newPositionFlux) {
        this.applicationContext = newApplicationContext;
        this.exchangeParameters = newExchangeParameters;
        this.userService = newUserService;
        this.tradeService = newTradeService;
        this.accountFlux = newAccountFlux;
        this.tickerFlux = newTickerFlux;
        this.orderFlux = newOrderFlux;
        this.tradeFlux = newTradeFlux;
        this.exchangeAccountRepository = newExchangeAccountRepository;
        this.strategyRepository = newStrategyRepository;
        this.orderRepository = newOrderRepository;
        this.tradeRepository = newTradeRepository;
        this.positionRepository = newPositionRepository;
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
            logger.error("StrategyConfiguration - No strategy found");
            throw new ConfigurationException("No strategy found",
                    "You must have one class with @CassandreStrategy");
        }

        // Check if there are several strategies.
        if (strategyBeans.size() > 1) {
            logger.error("StrategyConfiguration - Several strategies found");
            strategyBeans.forEach((s, o) -> logger.error(" - " + s));
            throw new ConfigurationException("Several strategies found",
                    "Cassandre trading bot only supports one strategy at a time (@CassandreStrategy)");
        }

        // Check if the strategy extends CassandreStrategy.
        Object o = strategyBeans.values().iterator().next();
        if (!(o instanceof CassandreStrategyInterface)) {
            throw new ConfigurationException("Your strategy doesn't extend BasicCassandreStrategy or BasicTa4jCassandreStrategy",
                    o.getClass() + " must extend BasicCassandreStrategy or BasicTa4jCassandreStrategy");
        }

        // Check that the trading account the strategy asks for really exists.
        final Optional<UserDTO> user = userService.getUser();
        if (user.isPresent()) {
            final Optional<AccountDTO> tradeAccount = ((CassandreStrategyInterface) o).getTradeAccount(new LinkedHashSet<>(user.get().getAccounts().values()));
            if (tradeAccount.isEmpty()) {
                StringJoiner accountList = new StringJoiner(", ");
                user.get().getAccounts().values().forEach(accountDTO -> accountList.add(accountDTO.getName()));
                throw new ConfigurationException("Your strategy specifies a trading account that doesn't exist",
                        "Check your getTradeAccount(Set<AccountDTO> accounts) method as it returns an empty result - Account list : " + accountList);
            }
        } else {
            throw new ConfigurationException("Impossible to retrieve your user information",
                    "Impossible to retrieve your user information. Check logs");
        }

        // =============================================================================================================
        // Getting strategy information.
        CassandreStrategyInterface strategy = (CassandreStrategyInterface) o;

        // Displaying strategy name.
        CassandreStrategy cassandreStrategyAnnotation = o.getClass().getAnnotation(CassandreStrategy.class);
        logger.info("StrategyConfiguration - Running strategy '{}'", cassandreStrategyAnnotation.strategyName());

        // Displaying requested currency pairs.
        StringJoiner currencyPairList = new StringJoiner(", ");
        strategy.getRequestedCurrencyPairs().forEach(currencyPair -> currencyPairList.add(currencyPair.toString()));
        logger.info("StrategyConfiguration - The strategy requires the following currency pair(s) : {}", currencyPairList);

        // =============================================================================================================
        // Setting up position service.
        this.positionService = new PositionServiceImplementation(positionRepository, tradeService, positionFlux);

        // =============================================================================================================
        // Setting up strategy.

        // Saving strategy in database.
        final Optional<Strategy> strategyInDatabase = strategyRepository.findByStrategyId(cassandreStrategyAnnotation.strategyId());
        strategyInDatabase.ifPresentOrElse(existingStrategy -> {
            // Update.
            existingStrategy.setName(cassandreStrategyAnnotation.strategyName());
            strategyRepository.save(existingStrategy);
            strategy.setStrategyDTO(strategyMapper.mapToStrategyDTO(existingStrategy));
            logger.debug("StrategyConfiguration - strategy updated in database {}", existingStrategy);
        }, () -> {
            // Creation.
            Strategy newStrategy = new Strategy();
            newStrategy.setStrategyId(cassandreStrategyAnnotation.strategyId());
            newStrategy.setName(cassandreStrategyAnnotation.strategyName());
            // Set exchange account.
            Optional<ExchangeAccount> exchangeAccount = exchangeAccountRepository.findByExchangeAndAccount(exchangeParameters.getName(), exchangeParameters.getUsername());
            exchangeAccount.ifPresent(newStrategy::setExchangeAccount);
            // Set type.
            if (o instanceof BasicCassandreStrategy) {
                newStrategy.setType(BASIC_STRATEGY);
            }
            if (o instanceof BasicTa4jCassandreStrategy) {
                newStrategy.setType(BASIC_TA4J_STRATEGY);
            }
            strategyRepository.save(newStrategy);
            logger.debug("StrategyConfiguration - strategy saved in database {}", newStrategy);
            strategy.setStrategyDTO(strategyMapper.mapToStrategyDTO(newStrategy));
        });

        // Setting services & repositories.
        strategy.setOrderRepository(orderRepository);
        strategy.setTradeRepository(tradeRepository);
        strategy.setTradeService(tradeService);
        strategy.setPositionService(positionService);
        strategy.setPositionRepository(positionRepository);

        // Account flux.
        final ConnectableFlux<AccountDTO> connectableAccountFlux = accountFlux.getFlux().publish();
        connectableAccountFlux.subscribe(strategy::accountUpdate);          // For strategy.
        connectableAccountFlux.connect();

        // Position flux.
        final ConnectableFlux<PositionDTO> connectablePositionFlux = positionFlux.getFlux().publish();
        connectablePositionFlux.subscribe(strategy::positionUpdate);        // For strategy.
        connectablePositionFlux.connect();

        // Order flux.
        final ConnectableFlux<OrderDTO> connectableOrderFlux = orderFlux.getFlux().publish();
        connectableOrderFlux.subscribe(strategy::orderUpdate);              // For strategy.
        connectableOrderFlux.subscribe(positionService::orderUpdate);       // For position service.
        connectableOrderFlux.connect();

        // Trade flux to strategy.
        final ConnectableFlux<TradeDTO> connectableTradeFlux = tradeFlux.getFlux().publish();
        connectableTradeFlux.subscribe(strategy::tradeUpdate);              // For strategy.
        connectableTradeFlux.subscribe(positionService::tradeUpdate);       // For position service.
        connectableTradeFlux.connect();

        // Ticker flux.
        tickerFlux.updateRequestedCurrencyPairs(strategy.getRequestedCurrencyPairs());
        final ConnectableFlux<TickerDTO> connectableTickerFlux = tickerFlux.getFlux().publish();
        // if in dry mode, we also send the ticker to the trade service in dry mode.
        if (tradeService instanceof TradeServiceDryModeImplementation) {
            connectableTickerFlux.subscribe(((TradeServiceDryModeImplementation) tradeService)::tickerUpdate);
        }
        connectableTickerFlux.subscribe(strategy::tickerUpdate);            // For strategy.
        connectableTickerFlux.subscribe(positionService::tickerUpdate);     // For position service.
        connectableTickerFlux.connect();

        // If in dry mode, we setup dependencies.
        if (userService instanceof UserServiceDryModeImplementation) {
            ((UserServiceDryModeImplementation) userService).setDependencies((GenericCassandreStrategy) strategy);
        }
    }

    /**
     * Getter for positionService.
     *
     * @return positionService
     */
    @Bean
    public PositionService getPositionService() {
        return positionService;
    }

}
