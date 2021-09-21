package tech.cassandre.trading.bot.configuration;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import reactor.core.publisher.ConnectableFlux;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.ImportedTicker;
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.ImportedTickersRepository;
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
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
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

    /** Tickers file prefix. */
    private static final String TICKERS_FILE_PREFIX = "tickers-to-import";

    /** Tickers file suffix. */
    private static final String TICKERS_FILE_SUFFIX = ".csv";

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Strategy repository. */
    private final StrategyRepository strategyRepository;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Position repository. */
    private final PositionRepository positionRepository;

    /** Imported tickers repository. */
    private final ImportedTickersRepository importedTickersRepository;

    /** Exchange service. */
    private final ExchangeService exchangeService;

    /** User service. */
    private final UserService userService;

    /** Trade service. */
    private final TradeService tradeService;

    /** Position service. */
    private PositionService positionService;

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
            throw new ConfigurationException("Impossible to retrieve your user information",
                    "Impossible to retrieve your user information - Check logs.");
        } else {
            logger.info("Available accounts on the exchange:");
            user.get()
                    .getAccounts()
                    .values()
                    .forEach(account -> {
                        logger.info("- Account id / name: {} / {}.",
                                account.getAccountId(),
                                account.getName());
                        account.getBalances()
                                .stream()
                                .filter(balance -> balance.getAvailable().compareTo(ZERO) != 0)
                                .forEach(balance -> logger.info(" - {} {}.", balance.getAvailable(), balance.getCurrency()));
                    });
        }

        // Check that there is at least one strategy.
        if (strategies.isEmpty()) {
            throw new ConfigurationException("No strategy found", "You must have one class with @CassandreStrategy annotation.");
        }

        // Check that all strategies extends CassandreStrategyInterface.
        Set<String> strategiesWithoutExtends = strategies.values()
                .stream()
                .filter(strategy -> !(strategy instanceof CassandreStrategyInterface))
                .map(strategy -> strategy.getClass().getSimpleName())
                .collect(Collectors.toSet());
        if (!strategiesWithoutExtends.isEmpty()) {
            final String list = String.join(",", strategiesWithoutExtends);
            throw new ConfigurationException(list + " doesn't extend BasicCassandreStrategy or BasicTa4jCassandreStrategy",
                    list + " must extend BasicCassandreStrategy or BasicTa4jCassandreStrategy");
        }

        // Check that all strategies specifies an existing trade account.
        final Set<AccountDTO> accountsAvailableOnExchange = new HashSet<>(user.get().getAccounts().values());
        Set<String> strategiesWithoutTradeAccount = strategies.values()
                .stream()
                .filter(strategy -> ((CassandreStrategyInterface) strategy).getTradeAccount(accountsAvailableOnExchange).isEmpty())
                .map(strategy -> strategy.getClass().toString())
                .collect(Collectors.toSet());
        if (!strategiesWithoutTradeAccount.isEmpty()) {
            final String strategyList = String.join(",", strategiesWithoutTradeAccount);
            throw new ConfigurationException("Your strategies specifies a trading account that doesn't exist",
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

        // Check that the currency pairs required by the strategies are available on the exchange.
        final Set<CurrencyPairDTO> availableCurrencyPairs = exchangeService.getAvailableCurrencyPairs();
        final Set<String> notAvailableCurrencyPairs = applicationContext
                .getBeansWithAnnotation(CassandreStrategy.class)
                .values()
                .stream()
                .map(o -> (CassandreStrategyInterface) o)
                .map(CassandreStrategyInterface::getRequestedCurrencyPairs)
                .flatMap(Set::stream)
                .filter(currencyPairDTO -> !availableCurrencyPairs.contains(currencyPairDTO))
                .map(CurrencyPairDTO::toString)
                .collect(Collectors.toSet());
        if (!notAvailableCurrencyPairs.isEmpty()) {
            logger.warn("Your exchange doesn't support the following currency pairs you requested: {}.", String.join(", ", notAvailableCurrencyPairs));
        }

        // =============================================================================================================
        // Maintenance code.
        // If a position was blocked in OPENING or CLOSING, we send again the trades.
        // This could happen if cassandre crashes after saving a trade and did not have time to send it to
        // positionService. Here we force the status recalculation, and we save it.
        positionRepository.findByStatusIn(Stream.of(OPENING, CLOSING).collect(Collectors.toSet()))
                .stream()
                .map(positionMapper::mapToPositionDTO)
                .map(positionMapper::mapToPosition)
                .forEach(positionRepository::save);

        // =============================================================================================================
        // Creating position service.
        this.positionService = new PositionServiceCassandreImplementation(positionRepository, tradeService, positionFlux);

        // =============================================================================================================
        // Loading imported tickers into database.
        loadImportedTickers();

        // =============================================================================================================
        // Creating flux.
        final ConnectableFlux<Set<AccountDTO>> connectableAccountFlux = accountFlux.getFlux().publish();
        final ConnectableFlux<Set<PositionDTO>> connectablePositionFlux = positionFlux.getFlux().publish();
        final ConnectableFlux<Set<OrderDTO>> connectableOrderFlux = orderFlux.getFlux().publish();
        final ConnectableFlux<Set<TickerDTO>> connectableTickerFlux = tickerFlux.getFlux().publish();
        final ConnectableFlux<Set<TradeDTO>> connectableTradeFlux = tradeFlux.getFlux().publish();

        // =============================================================================================================
        // Configuring strategies.
        logger.info("Running the following strategies:");
        strategies.values()
                .forEach(s -> {
                    CassandreStrategyInterface strategy = (CassandreStrategyInterface) s;
                    CassandreStrategy annotation = s.getClass().getAnnotation(CassandreStrategy.class);

                    // Displaying information about strategy.
                    logger.info("- Strategy '{}/{}' (requires {}).",
                            annotation.strategyId(),
                            annotation.strategyName(),
                            strategy.getRequestedCurrencyPairs().stream()
                                    .map(CurrencyPairDTO::toString)
                                    .collect(Collectors.joining(", ")));

                    // Saving or updating strategy in database.
                    strategyRepository.findByStrategyId(annotation.strategyId()).ifPresentOrElse(existingStrategy -> {
                        // Update.
                        existingStrategy.setName(annotation.strategyName());
                        strategyRepository.save(existingStrategy);
                        final StrategyDTO strategyDTO = strategyMapper.mapToStrategyDTO(existingStrategy);
                        strategyDTO.initializeLastPositionIdUsed(positionRepository.getLastPositionIdUsedByStrategy(strategyDTO.getId()));
                        strategy.setStrategy(strategyDTO);
                        logger.debug("Strategy updated in database: {}", existingStrategy);
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
                        logger.debug("Strategy created in database: {}", newStrategy);
                        StrategyDTO strategyDTO = strategyMapper.mapToStrategyDTO(strategyRepository.save(newStrategy));
                        strategyDTO.initializeLastPositionIdUsed(positionRepository.getLastPositionIdUsedByStrategy(strategyDTO.getId()));
                        strategy.setStrategy(strategyDTO);
                    });

                    // Initialize accounts values in strategy.
                    strategy.initializeAccounts(user.get().getAccounts());

                    // Setting services & repositories to strategy.
                    strategy.setPositionFlux(positionFlux);
                    strategy.setOrderRepository(orderRepository);
                    strategy.setTradeRepository(tradeRepository);
                    strategy.setPositionRepository(positionRepository);
                    strategy.setImportedTickersRepository(importedTickersRepository);
                    strategy.setExchangeService(exchangeService);
                    strategy.setTradeService(tradeService);
                    strategy.setPositionService(positionService);

                    // Calling user defined initialize() method.
                    strategy.initialize();

                    // Connecting flux to strategy.
                    connectableAccountFlux.subscribe(strategy::accountsUpdates);
                    connectablePositionFlux.subscribe(strategy::positionsUpdates);
                    connectableOrderFlux.subscribe(strategy::ordersUpdates);
                    connectableTradeFlux.subscribe(strategy::tradesUpdates);
                    connectableTickerFlux.subscribe(strategy::tickersUpdates);
                });

        // Start flux.
        connectableAccountFlux.connect();
        connectablePositionFlux.connect();
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

    /**
     * Load imported tickers into database.
     */
    private void loadImportedTickers() {
        // Deleting everything before import.
        if (importedTickersRepository.count() > 0) {
            importedTickersRepository.deleteAllInBatch();
        }

        // Getting the list of files to import and insert them in database.
        AtomicLong counter = new AtomicLong(0);
        logger.info("Importing tickers");
        getFilesToLoad()
                .parallelStream()
                .filter(resource -> resource.getFilename() != null)
                .peek(resource -> logger.info("Importing file {}", resource.getFilename()))
                .forEach(resource -> {
                    try {
                        // Insert the tickers in database.
                        new CsvToBeanBuilder<ImportedTicker>(Files.newBufferedReader(resource.getFile().toPath()))
                                .withType(ImportedTicker.class)
                                .withIgnoreLeadingWhiteSpace(true)
                                .build()
                                .parse()
                                .forEach(importedTicker -> {
                                    logger.debug("Importing ticker {}", importedTicker);
                                    importedTicker.setId(counter.incrementAndGet());
                                    importedTickersRepository.save(importedTicker);
                                });
                    } catch (IOException e) {
                        logger.error("Impossible to load imported tickers: {}", e.getMessage());
                    }
                });
        logger.info("{} tickers imported", importedTickersRepository.count());
    }

    /**
     * Returns the list of files to import.
     *
     * @return files to import.
     */
    public List<Resource> getFilesToLoad() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            final Resource[] resources = resolver.getResources("classpath*:" + TICKERS_FILE_PREFIX + "*" + TICKERS_FILE_SUFFIX);
            return Arrays.asList(resources);
        } catch (IOException e) {
            logger.error("Impossible to load imported tickers: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

}
