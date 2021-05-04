package tech.cassandre.trading.bot.ta4j;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * BarContext represents a transient state of the bar being built.
 * Please note, that the computations are done in doubles.
 */
@Getter
@EqualsAndHashCode
@Log4j2
class BarContext {

    /**
     * The number of updates received in the context.
     */
    private int updatesReceived = 0;
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
    private double open;
    /**
     * Close price.
     */
    private double close;
    /**
     * Running Volume.
     */
    private double volume = 0;

    /**
     * Initial volume received at bar open. Only used, when bar duration is < 24h
     */
    private double initialDayVolume;


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
    BarContext(final Duration newDuration, final ZonedDateTime newStartTime, final Number newLow, final Number newHigh,
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
        if (isDurationMoreThanDay()) {
            this.volume = newVolume.doubleValue();
        } else {
            this.initialDayVolume = newVolume != null ? newVolume.doubleValue() : 0;
        }
    }

    public boolean isAfter(final ZonedDateTime timestamp) {
        return timestamp.isAfter(endTime.minus(Duration.ofSeconds(1)));
    }

    /**
     * The contract of the update call is that it is called without timestamp
     * and is always within bounds of one bar duration.
     *
     * @param newLow    new low to be updated (might be null)
     * @param newHigh   new high to be updated (might be null)
     * @param newClose  new close - mandatory
     * @param newVolume new volume to be updated (might be null)
     */
    @SuppressWarnings("checkstyle:AvoidInlineConditionals")
    public void update(final Number newLow, final Number newHigh, final Number newClose, final Number newVolume) {
        if (newClose == null) {
            throw new IllegalArgumentException("Cannot update bar context without at least specifying close price");
        }
        close = newClose.doubleValue();
        low = Math.min(low, newLow == null ? close : newLow.doubleValue());
        high = Math.max(high, newHigh == null ? close : newHigh.doubleValue());

        if (newVolume != null) {
            if (!isDurationMoreThanDay() && initialDayVolume > 0) {
                volume = newVolume.doubleValue() - initialDayVolume;
            } else {
                volume = volume + newVolume.doubleValue();
            }
        }
        updatesReceived++;
    }

    /**
     * Gets the {@link Bar} object from the context.
     * @return the {@link Bar}
     */
    @SuppressWarnings("checkstyle:AvoidInlineConditionals")
    public Bar toBar() {
        return new BaseBar(duration, endTime, open, high, low, close,
                volume == 0 && updatesReceived == 0 ? initialDayVolume : volume);
    }

    boolean isDurationMoreThanDay() {
        return duration.compareTo(Duration.ofDays(1)) > 0;
    }

}
