package tech.cassandre.trading.bot.test.util.junit;

import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Base for tests.
 */
public class BaseTest {

    /** cp1 for tests. */
    protected final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

    /** cp2 for tests. */
    protected final CurrencyPairDTO cp2 = new CurrencyPairDTO(ETH, USDT);

    /** Ten seconds wait. */
    protected static final long WAITING_TIME_IN_SECONDS = 5L;

    /** How much we should wait for tests until it is declared as failed. */
    protected static final long MAXIMUM_RESPONSE_TIME_IN_SECONDS = 60;

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Constructor.
     */
    public BaseTest() {
        // Default Configuration for Awaitility.
        Awaitility.setDefaultPollInterval(fibonacci(SECONDS));
        Awaitility.setDefaultTimeout(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS);
    }

    /**
     * Getter logger.
     *
     * @return logger
     */
    protected final Logger getLogger() {
        return logger;
    }

    /**
     * Util method to return a fake ticker.
     *
     * @param cp  currency pair
     * @param bid bid price
     * @return ticket
     */
    protected static Optional<TickerDTO> getFakeTicker(final CurrencyPairDTO cp, final BigDecimal bid) {
        return Optional.of(TickerDTO.builder()
                .currencyPair(cp)
                .timestamp(getRandomDate())
                .bid(bid)
                .last(bid)
                .create());
    }

    /**
     * Util method to return a fake ticker with date.
     *
     * @param timestamp timestamp
     * @param cp        currency pair
     * @param bid       bid price
     * @return ticket
     */
    protected static Optional<TickerDTO> getFakeTicker(final Date timestamp, final CurrencyPairDTO cp, final BigDecimal bid) {
        return Optional.of(TickerDTO.builder()
                .currencyPair(cp)
                .timestamp(timestamp)
                .bid(bid)
                .last(bid)
                .create());
    }

    /**
     * Get random date.
     *
     * @return random date
     */
    public static Date getRandomDate() {
        long aDay = TimeUnit.DAYS.toMillis(1);
        long now = new Date().getTime();
        Date hundredYearsAgo = new Date(now - aDay * 365 * 100);
        Date tenDaysAgo = new Date(now - aDay * 10);
        long startMillis = hundredYearsAgo.getTime();
        long endMillis = tenDaysAgo.getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis);
        return new Date(randomMillisSinceEpoch);
    }

    /**
     * Get exception message from parameter exception.
     *
     * @param e exception
     * @return message
     */
    protected String getParametersExceptionMessage(Exception e) {
        return e.getCause().getCause().getCause().getMessage();
    }


    /**
     * Generate a date in 2020 with a day.
     * @param day day
     * @return date
     */
    protected static Date createDate(final int day) {
        return Date.from(ZonedDateTime.of(2020, 1, day, 9, 0, 0, 0, ZoneId.systemDefault()).toInstant());
    }


    /**
     * Generates a ZonedDateTime.
     * @param date date with format dd-MM-yyyy
     * @return ZonedDateTime
     */
    protected ZonedDateTime createZonedDateTime(final String date) {
        LocalDateTime ldt = LocalDateTime.parse(date + " 00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        return ldt.atZone(ZoneId.systemDefault());
    }

}
