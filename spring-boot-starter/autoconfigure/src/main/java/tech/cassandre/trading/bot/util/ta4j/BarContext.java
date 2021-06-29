package tech.cassandre.trading.bot.util.ta4j;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * BarContext represents a transient state of the bar being built.
 * Please note, that the computations are done in doubles.
 */
@Getter
@EqualsAndHashCode
@Log4j2
public
class BarContext {
    /**
     * The duration.
     */
    private final Duration duration;
    /**
     * The start time.
     */
    private final ZonedDateTime startTime;
    /**
     * The end time.
     */
    private final ZonedDateTime endTime;
    /**
     * Low price.
     */
    private double low;
    /**
     * High price.
     */
    private double high;
    /**
     * Open price.
     */
    private final double open;
    /**
     * Close price.
     */
    private double close;
    /**
     * Volume.
     */
    private double volume;


    /**
     * Bar context. The bar is constructed after the time has finished.
     *
     * @param newDuration  the duration
     * @param newStartTime start time of the bar
     * @param newLow       low price
     * @param newHigh      high price
     * @param newOpen      open price
     * @param newClose     close price
     * @param newVolume    volume
     */
    @SuppressWarnings("checkstyle:AvoidInlineConditionals")
    public BarContext(final Duration newDuration, final ZonedDateTime newStartTime, final Number newLow, final Number newHigh,
                      final Number newOpen, final Number newClose, final Number newVolume) {
        if (newDuration == null || newStartTime == null) {
            throw new IllegalArgumentException("Cannot construct bar context without duration and timestamp specified");
        }
        this.duration = newDuration;
        this.startTime = newStartTime;
        this.endTime = startTime.plus(duration);
        this.close = newClose != null ? newClose.doubleValue() : 0;

        this.low = newLow != null ? newLow.doubleValue() : close;
        this.high = newHigh != null ? newHigh.doubleValue() : close;
        this.open = newOpen != null ? newOpen.doubleValue() : close;
        this.volume = newVolume != null ? newVolume.doubleValue() : 0;
    }

    public final boolean isAfter(final ZonedDateTime timestamp) {
        return timestamp.isAfter(endTime.minus(Duration.ofSeconds(1)));
    }

    @SuppressWarnings("checkstyle:AvoidInlineConditionals")
    public final void update(final Number newLow, final Number newHigh, final Number newClose, final Number newVolume) {
        if (newClose == null) {
            throw new IllegalArgumentException("Cannot update bar context without at least specifying close price");
        }
        close = newClose.doubleValue();
        low = Math.min(low, newLow == null ? close : newLow.doubleValue());
        high = Math.max(high, newHigh == null ? close : newHigh.doubleValue());

        volume = volume + (newVolume == null ? 0 : newVolume.doubleValue());
    }

}
