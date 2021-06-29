package tech.cassandre.trading.bot.configuration;

import lombok.RequiredArgsConstructor;
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
import tech.cassandre.trading.bot.service.PositionServiceCassandreImplementation;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.BasicTa4jCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.configuration.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.strategy.StrategyTypeDTO.BASIC_STRATEGY;
import static tech.cassandre.trading.bot.dto.strategy.StrategyTypeDTO.BASIC_TA4J_STRATEGY;

/**
 * StrategyAutoConfiguration configures the strategies.
 */
@Configuration
@RequiredArgsConstructor
public class StrategiesAutoConfiguration extends BaseConfiguration {

    /** Application context. */
    private final ApplicationContext applicationContext;

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
     * Search for strategies and runs them.
     */
    @PostConstruct
    @SuppressWarnings("checkstyle:MethodLength")
    public void configure() {
        // Retrieving all the beans have the @Strategy annotation.
        final Map<String, Object> strategies = applicationContext.getBeansWithAnnotation(CassandreStrategy.class);

        // =============================================================================================================
        // Check if everything is ok.

        // Retrieve accounts information.
        final Optional<UserDTO> user = userService.getUser();
        if (user.isEmpty()) {
            throw new ConfigurationException("Impossible to retrieve your user information.",
                    "Impossible to retrieve your user information. Check logs");
        } else {
            logger.info("Accounts available on the exchange:");
            user.get()
                    .getAccounts()
                    .values()
                    .forEach(account -> logger.info("- Account id / Account name: {} / {}.",
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

        // Check that there is no duplicated strategy ids.
        final List<String> strategyIds = strategies.values()
                .stream()
                .map(o -> o.getClass().getAnnotation(CassandreStrategy.class).strategyId())
                .collect(Collectors.toList());
        final Set<String> duplicatedStrategyIds = strategies.values()
                .stream()
                .map(o -> o.getClass().getAnnotation(CassandreStrategy.class).strategyId())
                .filter(strategyId -> Collections.frequency(strategyIds, strategyId) > 1)
                .collect(Collectors.toSet());
        if (!duplicatedStrategyIds.isEmpty()) {
            throw new ConfigurationException("You have duplicated strategy ids",
                    "You have duplicated strategy ids: " + String.join(", ", duplicatedStrategyIds));
        }

        // =============================================================================================================
        // Creating position service.
        this.positionService = new PositionServiceCassandreImplementation(applicationContext, positionRepository, tradeService, positionFlux);

        // =============================================================================================================
        // Creating flux.
        final ConnectableFlux<Set<AccountDTO>> connectableAccountFlux = accountFlux.getFlux().publish();
        final ConnectableFlux<Set<PositionDTO>> connectablePositionFlux = positionFlux.getFlux().publish();
        final ConnectableFlux<Set<OrderDTO>> connectableOrderFlux = orderFlux.getFlux().publish();
        final ConnectableFlux<Set<TickerDTO>> connectableTickerFlux = tickerFlux.getFlux().publish();
        final ConnectableFlux<Set<TradeDTO>> connectableTradeFlux = tradeFlux.getFlux().publish();

        // =============================================================================================================
        // Connecting flux to positions that requires them.
        connectableOrderFlux.subscribe(positionService::ordersUpdates);
        connectableTradeFlux.subscribe(positionService::tradesUpdates);

        // =============================================================================================================
        // Configuring strategies.
        logger.info("Running the following strategies:");
        strategies.values()
                .forEach(s -> {
                    CassandreStrategyInterface strategy = ((CassandreStrategyInterface) s);
                    CassandreStrategy annotation = s.getClass().getAnnotation(CassandreStrategy.class);

                    // Displaying information about strategy.
                    logger.info("- Strategy '{}/{}' (requires {}).",
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
                        logger.debug("StrategyConfiguration - Strategy updated in database: {}", existingStrategy);
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
                        logger.debug("StrategyConfiguration - Strategy saved in database: {}", newStrategy);
                        StrategyDTO strategyDTO = strategyMapper.mapToStrategyDTO(strategyRepository.save(newStrategy));
                        strategyDTO.initializeLastPositionIdUsed(positionRepository.getLastPositionIdUsedByStrategy(strategyDTO.getId()));
                        strategy.setStrategy(strategyDTO);
                    });

                    // Initialize accounts values in strategy.
                    strategy.initializeAccounts(user.get().getAccounts());

                    // Setting services & repositories.
                    strategy.setOrderRepository(orderRepository);
                    strategy.setTradeRepository(tradeRepository);
                    strategy.setExchangeService(exchangeService);
                    strategy.setTradeService(tradeService);
                    strategy.setPositionService(positionService);
                    strategy.setPositionRepository(positionRepository);

                    // Setting flux.
                    connectableAccountFlux.subscribe(strategy::accountsUpdates);
                    connectablePositionFlux.subscribe(strategy::positionsUpdates);
                    connectableOrderFlux.subscribe(strategy::ordersUpdates);
                    connectableTradeFlux.subscribe(strategy::tradesUpdates);
                    connectableTickerFlux.subscribe(strategy::tickersUpdates);
                });

        // Position service should receive tickers after strategies.
        connectableTickerFlux.subscribe(positionService::tickersUpdates);

        // Start flux.
        connectableAccountFlux.connect();
        connectablePositionFlux.connect();
        connectableOrderFlux.connect();
        connectableTradeFlux.connect();
        connectableTickerFlux.connect();

        // =============================================================================================================
        // Maintenance code.
        // If a position was blocked in OPENING or CLOSING, we send again the trades.
        // This could happen if cassandre crashes after saving a trade and did not have time to send it to
        // positionService.
        positionRepository.findByStatus(OPENING).forEach(p -> {
            final Optional<Order> openingOrder = orderRepository.findByOrderId(p.getOpeningOrder().getOrderId());
            openingOrder.ifPresent(order -> order
                    .getTrades()
                    .stream()
                    .map(tradeMapper::mapToTradeDTO)
                    .forEach(tradeDTO -> positionService.tradesUpdates(Set.of(tradeDTO))));
        });
        positionRepository.findByStatus(CLOSING).forEach(p -> {
            final Optional<Order> closingOrder = orderRepository.findByOrderId(p.getClosingOrder().getOrderId());
            closingOrder.ifPresent(order -> order
                    .getTrades()
                    .stream()
                    .map(tradeMapper::mapToTradeDTO)
                    .forEach(tradeDTO -> positionService.tradesUpdates(Set.of((tradeDTO)))));
        });
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
