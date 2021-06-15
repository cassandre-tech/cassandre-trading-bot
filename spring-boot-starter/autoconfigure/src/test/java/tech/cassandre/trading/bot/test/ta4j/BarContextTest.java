package tech.cassandre.trading.bot.test.ta4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.util.ta4j.BarContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BarContextTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss",
            Locale.ENGLISH);

    @DisplayName("Check bar context is created correctly with all values set")
    @Test
    public void testContextCreation() {
        BarContext ctx = new BarContext(Duration.ofMinutes(1), getTime("2021-10-11 10:00:00"),
                1, 3, 1, 2, 100);

        assertEquals(1d, ctx.getLow());
        assertEquals(3d, ctx.getHigh());
        assertEquals(2d, ctx.getClose());
        assertEquals(100d, ctx.getVolume());
        assertEquals(getTime("2021-10-11 10:00:00").plusMinutes(1), ctx.getEndTime());
    }

    @DisplayName("Check bar context is created correctly with some nulls")
    @Test
    public void testContextCreationWithNullsAndDefaultCloseValue() {
        BarContext ctx = new BarContext(Duration.ofMinutes(1), getTime("2021-10-11 10:00:00"),
                null, null, null, 2, null);

        assertEquals(2d, ctx.getLow());
        assertEquals(2d, ctx.getHigh());
        assertEquals(2d, ctx.getClose());
        assertEquals(0d, ctx.getVolume());
        assertEquals(getTime("2021-10-11 10:00:00").plusMinutes(1), ctx.getEndTime());
    }

    @DisplayName("Check bar context is created correctly with all nulls")
    @Test
    public void testContextCreationWithNulls() {
        assertThrows(IllegalArgumentException.class, ()-> new BarContext(null, null,
                null, null, null, null, null));
    }

    @DisplayName("Check bar context is updated correctly")
    @Test
    public void testUpdate() {
        BarContext ctx = new BarContext(Duration.ofMinutes(1), getTime("2021-10-11 10:00:00"),
                1, 3, 1, 2, 100);

        ctx.update(2, 5, 4, 200);

        assertEquals(1d, ctx.getLow());
        assertEquals(5d, ctx.getHigh());
        assertEquals(4d, ctx.getClose());
        assertEquals(300d, ctx.getVolume());

    }

    @DisplayName("Check bar context is updated correctly with only close and volume provided")
    @Test
    public void testUpdateWithCloseAndVolumeOnly() {
        BarContext ctx = new BarContext(Duration.ofMinutes(1), getTime("2021-10-11 10:00:00"),
                1, 3, 1, 2, 100);

        ctx.update(null, null, 10, 200);

        assertEquals(1d, ctx.getLow());
        assertEquals(10d, ctx.getHigh());
        assertEquals(10d, ctx.getClose());
        assertEquals(300d, ctx.getVolume());
    }

    @DisplayName("Check bar context is not updated when close price is missing")
    @Test
    public void testUpdateWithoutClosePrice() {
        BarContext ctx = new BarContext(Duration.ofMinutes(1), getTime("2021-10-11 10:00:00"),
                1, 3, 1, 2, 100);

        assertThrows(IllegalArgumentException.class, ()-> ctx.update(null, null, null, 200));
    }

    ZonedDateTime getTime(String value) {
        return LocalDateTime.parse(value, dateTimeFormatter)
                .atZone(ZoneId.systemDefault());
    }

}