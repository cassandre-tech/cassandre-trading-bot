package tech.cassandre.trading.bot.util.ta4j;

import lombok.extern.log4j.Log4j2;
import org.ta4j.core.Bar;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Implementation of the {@link BarAggregator} based on {@link Duration}.
 */
@Log4j2
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
     * @param close close price
     * @param volume volume
     */
    @Override
    public void update(final ZonedDateTime timestamp, final Number close, final Number volume) {
        if (ctx == null) {
            ctx = new BarContext(duration, timestamp, close, close, volume);
        } else if (ctx.isAfter(timestamp)) {
            // we have new bar starting - emit current ctx
            final Bar newBar = ctx.toBar();
            log.debug("Emitting new bar {}", newBar);
            sink.next(newBar);
            // take the close and start counting new context
            ctx = new BarContext(duration, timestamp, ctx.getClose(), close, volume);
        } else {
            ctx.update(close, volume);
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
}
