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
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.StrategyRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.ExchangeService;
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
import tech.cassandre.trading.bot.util.base.configuration.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;
import tech.cassandre.trading.bot.util.parameters.ExchangeParameters;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.strategy.StrategyTypeDTO.BASIC_STRATEGY;
import static tech.cassandre.trading.bot.dto.strategy.StrategyTypeDTO.BASIC_TA4J_STRATEGY;

/**
 * StrategyAutoConfiguration configures the strategy.
 */
@Configuration
public class StrategiesAutoConfiguration extends BaseConfiguration {

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

    /** Exchange service. */
    private final ExchangeService exchangeService;

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
     * @param newExchangeService           exchange service
     * @param newExchangeParameters        exchange parameters
     * @param newUserService               user service
     * @param newTradeService              trade service
     * @param newAccountFlux               account flux
     * @param newTickerFlux                ticker flux
     * @param newOrderFlux                 order flux
     * @param newTradeFlux                 trade flux
     * @param newStrategyRepository        strategy repository
     * @param newOrderRepository           order repository
     * @param newTradeRepository           trade repository
     * @param newPositionRepository        position repository
     * @param newPositionFlux              position flux
     */
    @SuppressWarnings("checkstyle:ParameterNumber")
    public StrategiesAutoConfiguration(final ApplicationContext newApplicationContext,
                                       final ExchangeService newExchangeService,
                                       final ExchangeParameters newExchangeParameters,
                                       final UserService newUserService,
                                       final TradeService newTradeService,
                                       final AccountFlux newAccountFlux,
                                       final TickerFlux newTickerFlux,
                                       final OrderFlux newOrderFlux,
                                       final TradeFlux newTradeFlux,
                                       final StrategyRepository newStrategyRepository,
                                       final OrderRepository newOrderRepository,
                                       final TradeRepository newTradeRepository,
                                       final PositionRepository newPositionRepository,
                                       final PositionFlux newPositionFlux) {
        this.applicationContext = newApplicationContext;
        this.exchangeService = newExchangeService;
        this.exchangeParameters = newExchangeParameters;
        this.userService = newUserService;
        this.tradeService = newTradeService;
        this.accountFlux = newAccountFlux;
        this.tickerFlux = newTickerFlux;
        this.orderFlux = newOrderFlux;
        this.tradeFlux = newTradeFlux;
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
    @SuppressWarnings("checkstyle:MethodLength")
    public void configure() {
        // Retrieving all the beans have the annotation @Strategy.
        final Map<String, Object> strategies = applicationContext.getBeansWithAnnotation(CassandreStrategy.class);

        // =============================================================================================================
        // Check if everything is ok.

        // Retrieve accounts information.
        final Optional<UserDTO> user = userService.getUser();
        if (user.isEmpty()) {
            throw new ConfigurationException("Impossible to retrieve your user information.",
                    "Impossible to retrieve your user information. Check logs");
        } else {
            user.get()
                    .getAccounts()
                    .values()
                    .forEach(account -> logger.info("StrategyConfiguration - Accounts available : '{}/{}'.",
                            account.getAccountId(),
                            account.getName()));
        }

        // Check that there is at least one strategy.
        if (strategies.isEmpty()) {
            throw new ConfigurationException("No strategy found", "You must have one class with @CassandreStrategy.");
        }

        // Check that all strategies extends CassandreStrategyInterface.
        Set<String> strategiesWithErrors = strategies.values()
                .stream()
                .filter(strategy -> !(strategy instanceof CassandreStrategyInterface))
                .map(strategy -> strategy.getClass().getSimpleName())
                .collect(Collectors.toSet());
        if (!strategiesWithErrors.isEmpty()) {
            final String list = String.join(",", strategiesWithErrors);
            throw new ConfigurationException(list + " doesn't extend BasicCassandreStrategy or BasicTa4jCassandreStrategy.",
                    list + " must extend BasicCassandreStrategy or BasicTa4jCassandreStrategy");
        }

        // Check that all strategies specifies an existing trade account.
        final Set<AccountDTO> accountsAvailableOnExchange = new HashSet<>(user.get().getAccounts().values());
        strategiesWithErrors = strategies.values()
                .stream()
                .filter(strategy -> ((CassandreStrategyInterface) strategy).getTradeAccount(accountsAvailableOnExchange).isEmpty())
                .map(strategy -> strategy.getClass().toString())
                .collect(Collectors.toSet());
        if (!strategiesWithErrors.isEmpty()) {
            final String strategyList = String.join(",", strategiesWithErrors);
            throw new ConfigurationException("Your strategies specifies a trading account that doesn't exist.",
                    "Check your getTradeAccount(Set<AccountDTO> accounts) method as it returns an empty result - Strategies in error : " + strategyList);
        }

        // Check that there is no duplicated strategy id.
        final Set<String> strategyIds = strategies.values()
                .stream()
                .map(o -> o.getClass().getAnnotation(CassandreStrategy.class).strategyId())
                .collect(Collectors.toSet());
        final Set<String> duplicatedStrategyId = strategies.values()
                .stream()
                .map(o -> o.getClass().getAnnotation(CassandreStrategy.class).strategyId())
                .filter(s -> Collections.frequency(strategyIds, s) > 1)
                .collect(Collectors.toSet());
        if (!duplicatedStrategyId.isEmpty()) {
            throw new ConfigurationException("You have duplicated strategy ids",
                    "You have duplicated strategy ids : " + String.join(",", duplicatedStrategyId));
        }

        // =============================================================================================================
        // Setting up position service.
        this.positionService = new PositionServiceImplementation(positionRepository, tradeService, positionFlux);

        // =============================================================================================================
        // Creating flux.
        final ConnectableFlux<AccountDTO> connectableAccountFlux = accountFlux.getFlux().publish();
        final ConnectableFlux<PositionDTO> connectablePositionFlux = positionFlux.getFlux().publish();
        final ConnectableFlux<OrderDTO> connectableOrderFlux = orderFlux.getFlux().publish();
        final LinkedHashSet<CurrencyPairDTO> currencyPairs = strategies.values()  // We get the list of all required cp of all strategies.
                .stream()
                .map(o -> ((CassandreStrategyInterface) o))
                .map(CassandreStrategyInterface::getRequestedCurrencyPairs)
                .flatMap(Set::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        tickerFlux.updateRequestedCurrencyPairs(currencyPairs);
        final ConnectableFlux<TickerDTO> connectableTickerFlux = tickerFlux.getFlux().publish();
        final ConnectableFlux<TradeDTO> connectableTradeFlux = tradeFlux.getFlux().publish();
        // =============================================================================================================
        // Connecting flux.
        // if in dry mode, we also send the ticker to the trade service in dry mode.
        if (tradeService instanceof TradeServiceDryModeImplementation) {
            connectableTickerFlux.subscribe(((TradeServiceDryModeImplementation) tradeService)::tickerUpdate);
        }
        connectableOrderFlux.subscribe(positionService::orderUpdate);
        connectableTradeFlux.subscribe(positionService::tradeUpdate);

        // =============================================================================================================
        // Configuring strategies.
        strategies.values()
                .forEach(s -> {
                    CassandreStrategyInterface strategy = ((CassandreStrategyInterface) s);
                    CassandreStrategy annotation = s.getClass().getAnnotation(CassandreStrategy.class);

                    // Displaying information about strategy.
                    logger.info("StrategyConfiguration - Running strategy '{}/{}' (requires {}).",
                            annotation.strategyId(),
                            annotation.strategyName(),
                            strategy.getRequestedCurrencyPairs().stream()
                                    .map(CurrencyPairDTO::toString)
                                    .collect(Collectors.joining(", ")));

                    // Saving strategy in database.
                    final Optional<Strategy> strategyInDatabase = strategyRepository.findByStrategyId(annotation.strategyId());
                    strategyInDatabase.ifPresentOrElse(existingStrategy -> {
                        // Update.
                        existingStrategy.setName(annotation.strategyName());
                        strategyRepository.save(existingStrategy);
                        final StrategyDTO strategyDTO = strategyMapper.mapToStrategyDTO(existingStrategy);
                        strategyDTO.initializeLastPositionIdUsed(positionRepository.getLastPositionIdUsedByStrategy(strategyDTO.getId()));
                        strategy.setStrategy(strategyDTO);
                        logger.debug("StrategyConfiguration - Strategy updated in database {}", existingStrategy);
                    }, () -> {
                        // Creation.
                        Strategy newStrategy = new Strategy();
                        newStrategy.setStrategyId(annotation.strategyId());
                        newStrategy.setName(annotation.strategyName());
                        // Set type.
                        if (strategy instanceof BasicCassandreStrategy) {
                            newStrategy.setType(BASIC_STRATEGY);
                        }
                        if (strategy instanceof BasicTa4jCassandreStrategy) {
                            newStrategy.setType(BASIC_TA4J_STRATEGY);
                        }
                        logger.debug("StrategyConfiguration - Strategy saved in database {}", newStrategy);
                        StrategyDTO strategyDTO = strategyMapper.mapToStrategyDTO(strategyRepository.save(newStrategy));
                        strategyDTO.initializeLastPositionIdUsed(positionRepository.getLastPositionIdUsedByStrategy(strategyDTO.getId()));
                        strategy.setStrategy(strategyDTO);
                    });

                    // Setting services & repositories.
                    strategy.setOrderRepository(orderRepository);
                    strategy.setTradeRepository(tradeRepository);
                    strategy.setExchangeService(exchangeService);
                    strategy.setTradeService(tradeService);
                    strategy.setPositionService(positionService);
                    strategy.setPositionRepository(positionRepository);
                    // Initialize accounts.
                    strategy.initializeAccounts(user.get().getAccounts());

                    // Setting flux.
                    connectableAccountFlux.subscribe(strategy::accountUpdate);
                    connectablePositionFlux.subscribe(strategy::positionUpdate);
                    connectableOrderFlux.subscribe(strategy::orderUpdate);
                    connectableTradeFlux.subscribe(strategy::tradeUpdate);
                    connectableTickerFlux.subscribe(strategy::tickerUpdate);
                    // If in dry mode, we setup dependencies.
                    if (userService instanceof UserServiceDryModeImplementation) {
                        ((UserServiceDryModeImplementation) userService).setDependencies((GenericCassandreStrategy) strategy);
                    }
                });
        connectableTickerFlux.subscribe(positionService::tickerUpdate);
        // Start flux.
        connectableAccountFlux.connect();
        connectablePositionFlux.connect();
        // If a position was stuck in OPENING or CLOSING, we fix the order set to null.
        positionRepository.findByStatus(OPENING).forEach(p -> {
            final Optional<Order> order = orderRepository.findByOrderId(p.getOpeningOrderId());
            if (order.isPresent()) {
                if (p.getOpeningOrder() == null) {
                    p.setOpeningOrder(order.get());
                    positionRepository.save(p);
                }
                order.get()
                        .getTrades()
                        .stream()
                        .map(tradeMapper::mapToTradeDTO)
                        .forEach(tradeDTO -> positionService.tradeUpdate(tradeDTO));
            }
        });
        positionRepository.findByStatus(CLOSING).forEach(p -> {
            final Optional<Order> order = orderRepository.findByOrderId(p.getClosingOrderId());
            if (order.isPresent()) {
                if (p.getClosingOrder() == null) {
                    p.setClosingOrder(order.get());
                    positionRepository.save(p);
                }
                order.get()
                        .getTrades()
                        .stream()
                        .map(tradeMapper::mapToTradeDTO)
                        .forEach(tradeDTO -> positionService.tradeUpdate(tradeDTO));
            }
        });
        connectableOrderFlux.connect();
        connectableTradeFlux.connect();
        connectableTickerFlux.connect();
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
