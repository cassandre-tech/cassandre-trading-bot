package tech.cassandre.trading.bot.test.mock;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.BacktestingCandle;
import tech.cassandre.trading.bot.domain.BacktestingCandleId;
import tech.cassandre.trading.bot.domain.ImportedCandle;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.BacktestingCandleRepository;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.MarketServiceBacktestingImplementation;
import tech.cassandre.trading.bot.util.mapper.BacktestingTickerMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Ticker flux mock - Allows developers to simulate tickers throw candles csv files importation.
 * Will read all files starting with "candles-for-backtesting" and ending with ".csv".
 * <p>
 * The file has the following columns:
 * Field    Description
 * =======================================
 * time     Start time of the candle cycle
 * open     Opening price
 * close    Closing price
 * high     Highest price
 * low      Lowest price
 * volume   Transaction volume
 * turnover Transaction amount
 */
@SuppressWarnings("checkstyle:DesignForExtension")
@TestConfiguration
@RequiredArgsConstructor
public class TickerFluxMock {

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Backtesting tickers repository. */
    private final BacktestingCandleRepository backtestingCandleRepository;

    /** Order flux. */
    private final OrderFlux orderFlux;

    /** Trade flux. */
    private final TradeFlux tradeFlux;

    /** Market service for backtesting. */
    private MarketServiceBacktestingImplementation marketServiceBacktestingImplementation;

    /** Backtesting mapper. */
    private final BacktestingTickerMapper backtestingTickerMapper = Mappers.getMapper(BacktestingTickerMapper.class);

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(applicationContext, marketService());
    }

    @Bean
    @Primary
    public MarketService marketService() {
        // Removes everything from table.
        backtestingCandleRepository.deleteAllInBatch();

        // Creates the mock.
        marketServiceBacktestingImplementation = new MarketServiceBacktestingImplementation(
                orderFlux,
                tradeFlux,
                orderRepository,
                tradeRepository,
                backtestingCandleRepository);

        // Getting the list of files to import and insert them in database.
        logger.info("Importing candles for backtesting...");
        Set<CurrencyPairDTO> currencyPairUsed = new HashSet<>();
        getCandlesFilesToLoad()
                .stream()
                .filter(resource -> resource.getFilename() != null)
                .peek(resource -> logger.info("Importing {}...", resource.getFilename()))
                .forEach(resource -> {
                    try {
                        // Insert the tickers in database.
                        AtomicLong sequence = new AtomicLong(1);
                        new CsvToBeanBuilder<ImportedCandle>(Files.newBufferedReader(resource.getFile().toPath()))
                                .withType(ImportedCandle.class)
                                .withIgnoreLeadingWhiteSpace(true)
                                .build()
                                .parse()
                                .forEach(importedCandle -> {
                                    logger.debug("Importing candle {}", importedCandle);
                                    BacktestingCandle candle = backtestingTickerMapper.mapToBacktestingCandle(importedCandle);
                                    // Specific fields in Backtesting candle.
                                    BacktestingCandleId id = new BacktestingCandleId();
                                    id.setTestSessionId(marketServiceBacktestingImplementation.getTestSessionId());
                                    id.setResponseSequenceId(sequence.getAndIncrement());
                                    id.setCurrencyPair(importedCandle.getCurrencyPair());
                                    candle.setId(id);
                                    // Save in database.
                                    backtestingCandleRepository.save(candle);
                                    // We build a list of currency pairs listed in files.
                                    currencyPairUsed.add(id.getCurrencyPairDTO());
                                });
                    } catch (IOException e) {
                        logger.error("Impossible to load candles for backtesting: {}", e.getMessage());
                    }
                });

        // Setting the flux size of each currency pair.
        currencyPairUsed.forEach(currencyPairDTO -> marketServiceBacktestingImplementation.getFluxSize()
                .put(currencyPairDTO, backtestingCandleRepository.findByIdCurrencyPair(currencyPairDTO.toString()).size()));

        return marketServiceBacktestingImplementation;
    }

    /**
     * Getter marketServiceBacktestingImplementation.
     *
     * @return marketServiceBacktestingImplementation
     */
    public final MarketServiceBacktestingImplementation getMarketServiceBacktestingImplementation() {
        return marketServiceBacktestingImplementation;
    }

    /**
     * Returns the list of candle files to import.
     *
     * @return candles to import.
     */
    public List<Resource> getCandlesFilesToLoad() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            final Resource[] resources = resolver.getResources("classpath*:candles-for-backtesting*csv");
            return Arrays.asList(resources);
        } catch (IOException e) {
            logger.error("TickerFluxMock encountered an error: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Returns true is a specific flux is done.
     *
     * @param currencyPair currency pair
     * @return true if the flux is done
     */
    public boolean isFluxDone(final CurrencyPairDTO currencyPair) {
        return marketServiceBacktestingImplementation.isFluxDone(currencyPair);
    }

    /**
     * Returns true is all flux are done.
     *
     * @return true if all are done
     */
    public boolean isFluxDone() {
        return marketServiceBacktestingImplementation.isFluxDone();
    }

}
