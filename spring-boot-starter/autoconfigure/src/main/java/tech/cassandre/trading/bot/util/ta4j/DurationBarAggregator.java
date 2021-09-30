package tech.cassandre.trading.bot.util.ta4j;

import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Implementation of the {@link BarAggregator} based on {@link Duration}.
 */
public class DurationBarAggregator implements BarAggregator {

    /**
     * Duration.
     */
    private final Duration duration;
    /**
     * The bar context.
     */
    private BarContext ctx;
    /**
     * The processor.
     */
    private final DirectProcessor<Bar> processor;
    /**
     * The sink.
     */
    private final FluxSink<Bar> sink;
    /**
     * Highest price
     */
    private Number highest = 0;
    /**
     * Lowest price
     */
    private Number lowest = 0;
    /**
     * Creates the Aggregator with the given {@link Duration}.
     * @param barDuration the duration
     */
    public DurationBarAggregator(final Duration barDuration) {
        this.duration = barDuration;
        this.processor = DirectProcessor.create();
        this.sink = processor.sink();
    }


    /**
     * Updates the bar data.
     * @param timestamp time of the tick
     * @param latestPrice latest price
     */

    @Override
    public void update(ZonedDateTime timestamp, Number latestPrice) {
        calculateHighestLowest(latestPrice);
        if (ctx == null) {
            ctx = new BarContext(duration, timestamp, latestPrice, latestPrice, latestPrice, latestPrice, 0);
        } else if (ctx.isAfter(timestamp)) {
            // we have new bar starting - emit current ctx
            sink.next(new BaseBar(duration, ctx.getEndTime(), ctx.getOpen(), ctx.getHigh(),
                    ctx.getLow(), ctx.getClose(), ctx.getVolume()));
            // take the close and start counting new context
            ctx = new BarContext(duration, timestamp, latestPrice, latestPrice, latestPrice, latestPrice, 0);
            highest = latestPrice;
            lowest = latestPrice;
        } else {
            ctx.update(lowest,highest,latestPrice,0);
        }
    }

    /**
     * Gets the {@link Flux}.
     * @return flux of Bars
     */
    @Override
    public Flux<Bar> getBarFlux() {
        return processor;
    }

    private void calculateHighestLowest(Number latestPrice) {
        if (lowest.doubleValue() == 0D) {
            lowest = latestPrice;
            highest = latestPrice;
        }
        else {
            lowest = Math.min(lowest.doubleValue(),latestPrice.doubleValue());
            highest = Math.max(highest.doubleValue(),latestPrice.doubleValue());
        }
    }
}
