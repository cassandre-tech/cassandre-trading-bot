package tech.cassandre.trading.bot.test.util;

import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.KCS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Base for tests.
 */
@SuppressWarnings("unused")
public class BaseTest {

    /** How much we should wait for tests until it ends. */
    protected static final long MAXIMUM_RESPONSE_TIME_IN_SECONDS = 120;

    /** Parameter that enable the "only tickers" strategy. */
    public static final String PARAMETER_ONLY_TICKERS_STRATEGY_ENABLED = "ONLY_TICKERS_STRATEGY_ENABLED";

    /** Parameter that enable the "only orders" strategy. */
    public static final String PARAMETER_ONLY_ORDERS_STRATEGY_ENABLED = "ONLY_ORDERS_STRATEGY_ENABLED";

    /** Parameter that enable the "only positions" strategy. */
    public static final String PARAMETER_ONLY_POSITIONS_STRATEGY_ENABLED = "ONLY_POSITIONS_STRATEGY_ENABLED";

    /** BTC/USDT. */
    public static final CurrencyPairDTO BTC_USDT = new CurrencyPairDTO(BTC, USDT);

    /** ETH/BTC. */
    public static final CurrencyPairDTO ETH_BTC = new CurrencyPairDTO(ETH, BTC);

    /** ETH/USDT. */
    public static final CurrencyPairDTO ETH_USDT = new CurrencyPairDTO(ETH, USDT);

    /** KCS/USDT. */
    public static final CurrencyPairDTO KCS_USDT = new CurrencyPairDTO(KCS, USDT);

    /** KCS/BTC. */
    public static final CurrencyPairDTO KCS_BTC = new CurrencyPairDTO(KCS, BTC);

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
     * Generate a date in 2020 with a day.
     * @param day day
     * @return date
     */
    protected static Date createDay(final int day) {
        return Date.from(ZonedDateTime.of(2020, 1, day, 9, 0, 0, 0, ZoneId.systemDefault()).toInstant());
    }

}
