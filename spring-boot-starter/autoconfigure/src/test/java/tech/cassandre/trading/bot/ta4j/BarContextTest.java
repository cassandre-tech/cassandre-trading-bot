package tech.cassandre.trading.bot.ta4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.ta4j.core.Bar;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertEquals(0d, ctx.getVolume());
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

    @DisplayName("Check bar object is creatd")
    @Test
    public void testToBar() {
        BarContext ctx = new BarContext(Duration.ofMinutes(1), getTime("2021-10-11 10:00:00"),
                null, null, null, 2, 100);

        Bar bar = ctx.toBar();

        assertEquals(2d, bar.getLowPrice().doubleValue());
        assertEquals(2d, bar.getHighPrice().doubleValue());
        assertEquals(2d, bar.getClosePrice().doubleValue());
        assertEquals(100d, bar.getVolume().doubleValue());
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
        assertEquals(100, ctx.getVolume());

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
        assertEquals(100, ctx.getVolume());
    }

    @DisplayName("Check volume aggregation when duration is gt than 24 hours")
    @Test
    public void testVolumeAggregation24HDuration() {
        BarContext ctx = new BarContext(Duration.ofHours(24), getTime("2021-10-11 10:00:00"),
                1, 3, 1, 2, 100);

        ctx.update(null, null, 10, 200);
        ctx.update(null, null, 10, 210);
        ctx.update(null, null, 10, 250);

        assertEquals(1d, ctx.getLow());
        assertEquals(10d, ctx.getHigh());
        assertEquals(10d, ctx.getClose());
        assertEquals(150, ctx.getVolume());
    }

    @DisplayName("Check volume aggregation when duration is lt than 24 hours")
    @Test
    public void testVolumeAggregationIntraDayDuration() {
        BarContext ctx = new BarContext(Duration.ofHours(1), getTime("2021-10-11 10:00:00"),
                1, 3, 1, 2, 100);

        ctx.update(null, null, 10, 110);
        ctx.update(null, null, 11, 115);

        assertEquals(1d, ctx.getLow());
        assertEquals(11d, ctx.getHigh());
        assertEquals(11d, ctx.getClose());
        assertEquals(15, ctx.getVolume());
    }

    @DisplayName("Check duration is lt than 24 hours")
    @Test
    public void testDurationLt1Day() {
        BarContext ctx = new BarContext(Duration.ofHours(1), getTime("2021-10-11 10:00:00"),
                1, 3, 1, 2, 100);

        assertFalse(ctx.isDurationMoreThanDay());
    }

    @DisplayName("Check duration is gt than 24 hours")
    @Test
    public void testDurationGt1Day() {
        BarContext ctx = new BarContext(Duration.ofDays(7), getTime("2021-10-11 10:00:00"),
                1, 3, 1, 2, 100);

        assertTrue(ctx.isDurationMoreThanDay());
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