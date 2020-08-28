package tech.cassandre.trading.bot.test.util;

import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;

/**
 * Base for tests.
 */
public class BaseTest {

    /** Ten seconds wait. */
    protected static final long TEN_SECONDS = 10000L;

    /** Invalid strategy enabled parameter. */
    public static final String PARAMETER_INVALID_STRATEGY_ENABLED = "invalidStrategy.enabled";

    /** Testable strategy enabled parameter. */
    public static final String PARAMETER_TESTABLE_STRATEGY_ENABLED = "testableStrategy.enabled";

    /** Testable ta4j strategy enabled parameter. */
    public static final String PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED = "testableTa4jStrategy.enabled";

    /** Testable strategy enabled parameter. */
    public static final String PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE = "true";

    /** Invalid strategy enabled parameter. */
    public static final String PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE = "false";

    /** Exchange name parameter. */
    public static final String PARAMETER_NAME_DEFAULT_VALUE = "kucoin";

    /** Sandbox parameter. */
    public static final String PARAMETER_SANDBOX_DEFAULT_VALUE = "true";

    /** Dry parameter. */
    public static final String PARAMETER_DRY_DEFAULT_VALUE = "false";

    /** Username parameter. */
    public static final String PARAMETER_USERNAME_DEFAULT_VALUE = "cassandre.crypto.bot@gmail.com";

    /** Passphrase parameter. */
    public static final String PARAMETER_PASSPHRASE_DEFAULT_VALUE = "cassandre";

    /** Key parameter. */
    public static final String PARAMETER_KEY_DEFAULT_VALUE = "5df8eea30092f40009cb3c6a";

    /** Secret parameter. */
    public static final String PARAMETER_SECRET_DEFAULT_VALUE = "5f6e91e0-796b-4947-b75e-eaa5c06b6bed";

    /** Rate for account parameter. */
    public static final String PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE = "100";

    /** Rate for account parameter (long value). */
    public static final String PARAMETER_RATE_ACCOUNT_LONG_VALUE = "PT5S";

    /** Rate for ticker parameter. */
    public static final String PARAMETER_RATE_TICKER_DEFAULT_VALUE = "101";

    /** Rate for ticker parameter (long value). */
    public static final String PARAMETER_RATE_TICKER_LONG_VALUE = "PT5S";

    /** Rate for trade parameter. */
    public static final String PARAMETER_RATE_TRADE_DEFAULT_VALUE = "102";

    /** Rate for trade parameter (long value). */
    public static final String PARAMETER_RATE_TRADE_LONG_VALUE = "PT5S";

    /** How much we should wait for tests until it ends. */
    protected static final long MAXIMUM_RESPONSE_TIME_IN_SECONDS = 60;

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Constructor.
     */
    public BaseTest() {
        // Configure Awaitility.
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
    protected static Date createDay(final int day) {
        return Date.from(ZonedDateTime.of(2020, 1, day, 9, 0, 0, 0, ZoneId.systemDefault()).toInstant());
    }

}
