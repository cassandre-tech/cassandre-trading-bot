package tech.cassandre.trading.bot.test.mock;

import lombok.RequiredArgsConstructor;
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
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.BacktestingTickerRepository;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.MarketServiceBacktestingImplementation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Ticker flux mock - Allows developers to simulate tickers via tsv files.
 * Will read all files starting by "tickers-" and ending with ".tsv".
 * <p>
 * The file has the following format :
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

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Backtesting tickers repository. */
    private final BacktestingTickerRepository backtestingTickerRepository;

    /** Order flux. */
    private final OrderFlux orderFlux;

    /** Trade flux. */
    private final TradeFlux tradeFlux;

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** To milliseconds. */
    public static final int MILLISECONDS = 1_000;

    /** Tickers file prefix. */
    private static final String TICKERS_FILE_PREFIX = "tickers-";

    /** Tickers file suffix. */
    private static final String TICKERS_FILE_SUFFIX = ".*sv";

    /** Market service for backtesting. */
    private MarketServiceBacktestingImplementation marketServiceBacktestingImplementation;

    @Bean
    @Primary
    public TickerFlux tickerFlux() {
        return new TickerFlux(applicationContext, marketService());
    }

    @Bean
    @Primary
    public MarketService marketService() {
        // Creates the mock.
        marketServiceBacktestingImplementation = new MarketServiceBacktestingImplementation(
                orderFlux,
                tradeFlux,
                orderRepository,
                tradeRepository,
                backtestingTickerRepository);

        // For every file.
        getFilesToLoad()
                .stream()
                .filter(resource -> resource.getFilename() != null)
                .forEach(resource -> {
                    // We add all the tickers of the currency pair.
                    AtomicLong sequence = new AtomicLong(1);
                    logger.info("Adding tests data from {}", resource.getFilename().substring(resource.getFilename().indexOf(TICKERS_FILE_PREFIX)));
                    getTickersFromFile(resource).forEach(tickerDTO -> {
                        // Adding each ticker in database.
                        marketServiceBacktestingImplementation.addTickerToDatabase(sequence.getAndIncrement(), tickerDTO);
                    });
                });

        return marketServiceBacktestingImplementation;
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
            logger.error("TickerFluxMock encountered an error: {}", e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Returns the currency pair from a filename.
     *
     * @param file file
     * @return currency pair
     */
    public CurrencyPairDTO getCurrencyPairFromFileName(final Resource file) {
        // Getting the string value of currency pair.
        if (file.getFilename() != null) {
            final int currencyPairIndexStart = file.getFilename().indexOf(TICKERS_FILE_PREFIX) + TICKERS_FILE_PREFIX.length();
            final int currencyPairIndexStop = file.getFilename().indexOf("sv") - 2;
            final String currencyPairAsString = file.getFilename().substring(currencyPairIndexStart, currencyPairIndexStop);
            final String[] currencyPairAsSplit = currencyPairAsString.split("-");
            return new CurrencyPairDTO(new CurrencyDTO(currencyPairAsSplit[0].toUpperCase()), new CurrencyDTO(currencyPairAsSplit[1].toUpperCase()));
        } else {
            return null;
        }
    }

    /**
     * Returns tickers loaded from a file.
     *
     * @param file file
     * @return tickers
     */
    private List<TickerDTO> getTickersFromFile(final Resource file) {
        final CurrencyPairDTO currencyPair = getCurrencyPairFromFileName(file);
        final List<TickerDTO> tickers = new LinkedList<>();
        // Replies from TSV files.
        try (Scanner scanner = new Scanner(file.getFile())) {
            while (scanner.hasNextLine()) {
                try (Scanner rowScanner = new Scanner(scanner.nextLine())) {
                    if (file.getFilename() != null && file.getFilename().endsWith("tsv")) {
                        rowScanner.useDelimiter("\t");
                    } else {
                        rowScanner.useDelimiter(",");
                    }
                    while (rowScanner.hasNext()) {
                        // Data retrieved from file.
                        final String time = rowScanner.next().replaceAll("\"", "");
                        final String open = rowScanner.next().replaceAll("\"", "");
                        final String close = rowScanner.next().replaceAll("\"", "");
                        final String high = rowScanner.next().replaceAll("\"", "");
                        final String low = rowScanner.next().replaceAll("\"", "");
                        final String volume = rowScanner.next().replaceAll("\"", "");
                        String turnover;
                        if (rowScanner.hasNext()) {
                            turnover = rowScanner.next().replaceAll("\"", "");
                        } else {
                            turnover = "0";
                        }

                        // Creating the ticker.
                        TickerDTO t = TickerDTO.builder()
                                .currencyPair(currencyPair)
                                .timestamp(ZonedDateTime.ofInstant(new Date(Long.parseLong(time) * MILLISECONDS).toInstant(), ZoneId.systemDefault()))
                                .open(new BigDecimal(open))
                                .last(new BigDecimal(close))
                                .high(new BigDecimal(high))
                                .low(new BigDecimal(low))
                                .volume(new BigDecimal(volume))
                                .quoteVolume(new BigDecimal(turnover))
                                .build();

                        // Add the ticker.
                        tickers.add(t);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            logger.error("{} not found !", file.getFilename());
        } catch (IOException e) {
            logger.error("IOException : {}", e.getMessage());
        }
        return tickers;
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
