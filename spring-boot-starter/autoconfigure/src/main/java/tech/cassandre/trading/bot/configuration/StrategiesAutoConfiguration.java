package tech.cassandre.trading.bot.configuration;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import reactor.core.publisher.ConnectableFlux;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TickerStreamFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.ImportedCandle;
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
import tech.cassandre.trading.bot.repository.ImportedCandleRepository;
import tech.cassandre.trading.bot.repository.ImportedTickerRepository;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.StrategyRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.internal.CassandreStrategyConfiguration;
import tech.cassandre.trading.bot.strategy.internal.CassandreStrategyDependencies;
import tech.cassandre.trading.bot.strategy.internal.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.configuration.BaseConfiguration;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;
import tech.cassandre.trading.bot.util.parameters.ExchangeParameters;

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

/**
 * StrategyAutoConfiguration configures the strategies.
 */
@Configuration
@EnableConfigurationProperties(ExchangeParameters.class)
@RequiredArgsConstructor
public class StrategiesAutoConfiguration extends BaseConfiguration {

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Exchange parameters. */
    private final ExchangeParameters exchangeParameters;

    /** Strategy repository. */
    private final StrategyRepository strategyRepository;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Position repository. */
    private final PositionRepository positionRepository;

    /** Imported candles' repository. */
    private final ImportedCandleRepository importedCandleRepository;

    /** Imported tickers' repository. */
    private final ImportedTickerRepository importedTickerRepository;

    /** Exchange service. */
    private final ExchangeService exchangeService;

    /** User service. */
    private final UserService userService;

    /** Trade service. */
    private final TradeService tradeService;

    /** Position service. */
    private final PositionService positionService;

    /** Account flux. */
    private final AccountFlux accountFlux;

    /** Ticker flux. */
    private final TickerFlux tickerFlux;

    /** Ticker Stream flux. */
    private final TickerStreamFlux tickerStreamFlux;

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
        // Configuration check.
        // We run tests to display and check if everything is ok with the configuration.
        final UserDTO user = checkConfiguration(strategies);

        // =============================================================================================================
        // Maintenance code.
        // If a position is blocked in OPENING or CLOSING, we send again the trades.
        // This could happen if cassandre crashes after saving a trade and did not have time to send it to
        // positionService. Here we force the status recalculation in PositionDTO, and we save it.
        positionRepository.findByStatusIn(Stream.of(OPENING, CLOSING).toList())
                .stream()
                .map(POSITION_MAPPER::mapToPositionDTO)
                .map(POSITION_MAPPER::mapToPosition)
                .forEach(positionRepository::save);

        // =============================================================================================================
        // Importing tickers & candles into database.
        // Feature documentation is here: https://trading-bot.cassandre.tech/learn/import-historical-data.html
        loadTickersFromFiles();
        loadCandlesFromFiles();

        // =============================================================================================================
        // Creating flux.
        final ConnectableFlux<Set<AccountDTO>> connectableAccountFlux = accountFlux.getFlux().publish();
        final ConnectableFlux<Set<PositionDTO>> connectablePositionFlux = positionFlux.getFlux().publish();
        final ConnectableFlux<Set<OrderDTO>> connectableOrderFlux = orderFlux.getFlux().publish();
        final ConnectableFlux<Set<TradeDTO>> connectableTradeFlux = tradeFlux.getFlux().publish();
        final ConnectableFlux<Set<TickerDTO>> connectableTickerFlux;
        if (exchangeParameters.isTickerStreamEnabled()) {
            connectableTickerFlux = tickerStreamFlux.getFlux().publish();
        } else {
            connectableTickerFlux = tickerFlux.getFlux().publish();
        }

        // =============================================================================================================
        // Configuring strategies.
        // Data in database, services, flux...
        logger.info("Running the following strategies:");
        strategies.values()
                .forEach(s -> {
                    CassandreStrategyInterface strategy = (CassandreStrategyInterface) s;
                    CassandreStrategy annotation = s.getClass().getAnnotation(CassandreStrategy.class);

                    // Retrieving strategy information from annotation.
                    final String strategyId = annotation.strategyId();
                    final String strategyName = annotation.strategyName();

                    // Displaying information about strategy.
                    logger.info("- Strategy '{}/{}' (requires {})",
                            strategyId,
                            strategyName,
                            strategy.getRequestedCurrencyPairs().stream()
                                    .map(CurrencyPairDTO::toString)
                                    .collect(Collectors.joining(", ")));

                    // StrategyDTO: saving or updating the strategy in database.
                    StrategyDTO strategyDTO;
                    final Optional<Strategy> strategyInDatabase = strategyRepository.findByStrategyId(annotation.strategyId());
                    if (strategyInDatabase.isEmpty()) {
                        // =============================================================================================
                        // If the strategy is NOT in database.
                        Strategy newStrategy = new Strategy();
                        newStrategy.setStrategyId(annotation.strategyId());
                        newStrategy.setName(annotation.strategyName());
                        strategyDTO = STRATEGY_MAPPER.mapToStrategyDTO(strategyRepository.save(newStrategy));
                        logger.debug("Strategy created in database: {}", newStrategy);
                    } else {
                        // =============================================================================================
                        // If the strategy is in database.
                        strategyInDatabase.get().setName(strategyName);
                        strategyDTO = STRATEGY_MAPPER.mapToStrategyDTO(strategyRepository.save(strategyInDatabase.get()));
                        logger.debug("Strategy updated in database: {}", strategyInDatabase.get());
                    }
                    strategyDTO.initializeLastPositionIdUsed(positionRepository.getLastPositionIdUsedByStrategy(strategyDTO.getUid()));

                    // Setting up configuration, dependencies and accounts in strategy.
                    strategy.initializeAccounts(user.getAccounts());
                    strategy.setConfiguration(getCassandreStrategyConfiguration(strategyDTO));
                    strategy.setDependencies(getCassandreStrategyDependencies());

                    // Calling user defined initialize() method.
                    strategy.initialize();

                    // Connecting flux to strategy.
                    connectableAccountFlux.subscribe(strategy::accountsUpdates, throwable -> logger.error("AccountsUpdates failing: {}", throwable.getMessage()));
                    connectablePositionFlux.subscribe(strategy::positionsUpdates, throwable -> logger.error("PositionsUpdates failing: {}", throwable.getMessage()));
                    connectableOrderFlux.subscribe(strategy::ordersUpdates, throwable -> logger.error("OrdersUpdates failing: {}", throwable.getMessage()));
                    connectableTradeFlux.subscribe(strategy::tradesUpdates, throwable -> logger.error("TradesUpdates failing: {}", throwable.getMessage()));
                    connectableTickerFlux.subscribe(strategy::tickersUpdates, throwable -> logger.error("TickersUpdates failing: {}", throwable.getMessage()));
                });

        // =============================================================================================================
        // Starting flux.
        connectableAccountFlux.connect();
        connectablePositionFlux.connect();
        connectableOrderFlux.connect();
        connectableTradeFlux.connect();
        connectableTickerFlux.connect();
    }

    /**
     * Check and display Cassandre configuration.
     *
     * @param strategies strategies
     * @return user information
     */
    private UserDTO checkConfiguration(final Map<String, Object> strategies) {
        // Prints all the supported currency pairs.
        logger.info("Supported currency pairs by the exchange: {}",
                exchangeService.getAvailableCurrencyPairs()
                        .stream()
                        .map(CurrencyPairDTO::toString)
                        .collect(Collectors.joining(", ")));

        // Retrieve accounts information.
        final Optional<UserDTO> user = userService.getUser();
        if (user.isEmpty()) {
            // Unable to retrieve user information.
            throw new ConfigurationException("Impossible to retrieve your user information",
                    "Impossible to retrieve your user information - Check logs");
        } else {
            if (user.get().getAccounts().isEmpty()) {
                // We were able to retrieve the user from the exchange but no account was found.
                throw new ConfigurationException("User information retrieved but no associated accounts found",
                        "Check the permissions you set on the API you created");
            } else {
                logger.info("Accounts available on the exchange:");
                user.get()
                        .getAccounts()
                        .values()
                        .forEach(account -> {
                            logger.info("- Account id / name: {} / {}",
                                    account.getAccountId(),
                                    account.getName());
                            account.getBalances()
                                    .stream()
                                    .filter(balance -> balance.getAvailable().compareTo(ZERO) != 0)
                                    .forEach(balance -> logger.info(" - {} {}", balance.getAvailable(), balance.getCurrency()));
                        });
            }
        }

        // Check that there is at least one strategy.
        if (strategies.isEmpty()) {
            throw new ConfigurationException("No strategy found", "You must have, at least, one class with @CassandreStrategy annotation");
        }

        // Check that all strategies extends CassandreStrategyInterface.
        Set<String> strategiesWithoutExtends = strategies.values()
                .stream()
                .filter(strategy -> !(strategy instanceof CassandreStrategyInterface))
                .map(strategy -> strategy.getClass().getSimpleName())
                .collect(Collectors.toSet());
        if (!strategiesWithoutExtends.isEmpty()) {
            final String list = String.join(",", strategiesWithoutExtends);
            throw new ConfigurationException(list + " doesn't extend BasicCassandreStrategy", list + " must extend BasicCassandreStrategy");
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
            throw new ConfigurationException("Your strategies specify a trading account that doesn't exist",
                    "Check your getTradeAccount(Set<AccountDTO> accounts) method as it returns an empty result - Strategies in error: " + strategyList + "\r\n"
                            + "See https://trading-bot.cassandre.tech/ressources/how-tos/how-to-fix-common-problems.html#your-strategies-specifies-a-trading-account-that-doesn-t-exist");
        }

        // Check that there is no duplicated strategy ids.
        final List<String> strategyIds = strategies.values()
                .stream()
                .map(o -> o.getClass().getAnnotation(CassandreStrategy.class).strategyId())
                .toList();
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
            logger.warn("Your exchange doesn't support the following currency pairs you requested: {}", String.join(", ", notAvailableCurrencyPairs));
        }

        return user.get();
    }

    /**
     * Returns cassandre strategy configuration.
     *
     * @param strategyDTO strategy
     * @return cassandre strategy configuration
     */
    private CassandreStrategyConfiguration getCassandreStrategyConfiguration(final StrategyDTO strategyDTO) {
        return CassandreStrategyConfiguration.builder()
                .strategyDTO(strategyDTO)
                .dryMode(exchangeParameters.getModes().getDry())
                .build();
    }

    /**
     * Returns cassandre strategy dependencies.
     *
     * @return cassandre strategy dependencies
     */
    private CassandreStrategyDependencies getCassandreStrategyDependencies() {
        return CassandreStrategyDependencies.builder()
                // Flux.
                .positionFlux(positionFlux)
                // Repositories.
                .orderRepository(orderRepository)
                .tradeRepository(tradeRepository)
                .positionRepository(positionRepository)
                .importedCandleRepository(importedCandleRepository)
                .importedTickerRepository(importedTickerRepository)
                // Services.
                .exchangeService(exchangeService)
                .tradeService(tradeService)
                .positionService(positionService)
                .build();
    }

    /**
     * Load candles in database.
     */
    private void loadCandlesFromFiles() {
        // Deleting everything before import.
        importedCandleRepository.deleteAllInBatch();

        // Getting the list of files to import and insert them in database.
        logger.info("Importing candles...");
        AtomicLong counter = new AtomicLong(0);
        getFilesToLoad("classpath*:candles-to-import*csv")
                .stream()
                .filter(resource -> resource.getFilename() != null)
                .peek(resource -> logger.info("Importing candles from {}", resource.getFilename()))
                .forEach(resource -> {
                    try {
                        // Insert the tickers in database.
                        new CsvToBeanBuilder<ImportedCandle>(Files.newBufferedReader(resource.getFile().toPath()))
                                .withType(ImportedCandle.class)
                                .withIgnoreLeadingWhiteSpace(true)
                                .build()
                                .parse()
                                .forEach(importedCandle -> {
                                    logger.debug("Importing candle {}", importedCandle);
                                    importedCandle.setUid(counter.incrementAndGet());
                                    importedCandleRepository.save(importedCandle);
                                });
                    } catch (IOException e) {
                        logger.error("Impossible to load imported candles: {}", e.getMessage());
                    }
                });
        logger.info("{} candles imported", importedCandleRepository.count());
    }

    /**
     * Load tickers in database.
     */
    private void loadTickersFromFiles() {
        // Deleting everything before import.
        importedTickerRepository.deleteAllInBatch();

        // Getting the list of files to import and insert them in database.
        logger.info("Importing tickers...");
        AtomicLong counter = new AtomicLong(0);
        getFilesToLoad("classpath*:tickers-to-import*csv")
                .stream()
                .filter(resource -> resource.getFilename() != null)
                .peek(resource -> logger.info("Importing tickers from {}", resource.getFilename()))
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
                                    importedTicker.setUid(counter.incrementAndGet());
                                    importedTickerRepository.save(importedTicker);
                                });
                    } catch (IOException e) {
                        logger.error("Impossible to load imported tickers: {}", e.getMessage());
                    }
                });
        logger.info("{} tickers imported", importedTickerRepository.count());
    }

    /**
     * Returns the list of tickers files to import.
     *
     * @param locationPattern the location pattern to resolve.
     * @return files to import.
     */
    public List<Resource> getFilesToLoad(final String locationPattern) {
        try {
            return Arrays.asList(new PathMatchingResourcePatternResolver().getResources(locationPattern));
        } catch (IOException e) {
            logger.error("Impossible to load imported tickers: {}", e.getMessage());
        }
        return Collections.emptyList();
    }
}
