package tech.cassandre.trading.bot.test.util.junit;

import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Base for mocks.
 */
public class BaseMock {

    /** cp1 for tests. */
    protected final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

    /** cp2 for tests. */
    protected final CurrencyPairDTO cp2 = new CurrencyPairDTO(ETH, USDT);

    /** cp3 for tests. */
    protected final CurrencyPairDTO cp3 = new CurrencyPairDTO(BTC, USDT);

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
    protected static ZonedDateTime createDate(final int day) {
        return ZonedDateTime.of(2020, 1, day, 0, 0, 0, 0, ZoneId.systemDefault());
    }


    /**
     * Generates a ZonedDateTime.
     *
     * @param date date with format dd-MM-yyyy
     * @return ZonedDateTime
     */
    protected static ZonedDateTime createZonedDateTime(final String date) {
        LocalDateTime ldt = LocalDateTime.parse(date + " 00:00", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
        return ldt.atZone(ZoneId.systemDefault());
    }

}
