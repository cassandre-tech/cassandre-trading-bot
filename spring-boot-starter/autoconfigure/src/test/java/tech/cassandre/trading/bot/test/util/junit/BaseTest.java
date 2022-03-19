package tech.cassandre.trading.bot.test.util.junit;

import org.awaitility.Awaitility;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.base.Base;

import java.math.BigDecimal;
import java.time.Instant;
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
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.KCS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Base for tests.
 */
public class BaseTest extends Base {

    /** Default strategy. */
    protected final StrategyDTO strategyDTO = StrategyDTO.builder()
            .uid(1L)
            .strategyId("01")
            .build();

    /** ETH/BTC. */
    public static final CurrencyPairDTO ETH_BTC = new CurrencyPairDTO(ETH, BTC);

    /** XChange ETH/BTC. */
    public static final Instrument XCHANGE_ETH_BTC = CURRENCY_MAPPER.mapToInstrument(new CurrencyPair(Currency.ETH, Currency.BTC));

    /** ETH/USDT. */
    public static final CurrencyPairDTO ETH_USDT = new CurrencyPairDTO(ETH, USDT);

    /** XChange ETH/USDT. */
    public static final Instrument XCHANGE_ETH_USDT = CURRENCY_MAPPER.mapToInstrument(new CurrencyPair(Currency.ETH, Currency.USDT));

    /** BTC/USDT. */
    public static final CurrencyPairDTO BTC_USDT = new CurrencyPairDTO(BTC, USDT);

    /** XChange BTC/USDT. */
    public static final Instrument XCHANGE_BTC_USDT = CURRENCY_MAPPER.mapToInstrument(new CurrencyPair(Currency.BTC, Currency.USDT));

    /** KCS/USDT. */
    public static final CurrencyPairDTO KCS_USDT = new CurrencyPairDTO(KCS, USDT);

    /** BTC/ETH. */
    public static final CurrencyPairDTO BTC_ETH = new CurrencyPairDTO(BTC, ETH);

    /** XChange BTC/ETH. */
    public static final Instrument XCHANGE_BTC_ETH = CURRENCY_MAPPER.mapToInstrument(new CurrencyPair(Currency.BTC, Currency.ETH));

    /**
     * Constructor.
     */
    public BaseTest() {
        // Default Configuration for Awaitility.
        Awaitility.setDefaultPollInterval(fibonacci(SECONDS));
        Awaitility.setDefaultTimeout(60, SECONDS);
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
                .last(bid)
                .build());
    }

    /**
     * Get random date.
     *
     * @return random date
     */
    public static ZonedDateTime getRandomDate() {
        long aDay = TimeUnit.DAYS.toMillis(1);
        long now = new Date().getTime();
        Date hundredYearsAgo = new Date(now - aDay * 365 * 100);
        Date tenDaysAgo = new Date(now - aDay * 10);
        long startMillis = hundredYearsAgo.getTime();
        long endMillis = tenDaysAgo.getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis);
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(randomMillisSinceEpoch), ZoneId.systemDefault());
    }

    /**
     * Generate a date in 2020 with a day.
     *
     * @param day day
     * @return date
     */
    protected static ZonedDateTime createZonedDateTime(final int day) {
        return ZonedDateTime.of(2020, 1, day, 0, 0, 0, 0, ZoneId.systemDefault());
    }

    /**
     * Generates a ZonedDateTime.
     *
     * @param date date with format dd-MM-yyyy
     * @return ZonedDateTime
     */
    protected static ZonedDateTime createZonedDateTime(final String date) {
        return ZonedDateTime.parse(date + " 00:00:00 UTC", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss VV"));
    }

}
