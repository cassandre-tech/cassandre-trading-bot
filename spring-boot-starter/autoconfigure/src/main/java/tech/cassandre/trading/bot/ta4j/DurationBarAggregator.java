package tech.cassandre.trading.bot.ta4j;

import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.ZonedDateTime;

public class DurationBarAggregator implements BarAggregator {

    private final Duration duration;
    private BarContext ctx;
    private final DirectProcessor<Bar> processor;
    private final FluxSink<Bar> sink;


    public DurationBarAggregator(final Duration duration) {
        this.duration = duration;
        processor = DirectProcessor.create();
        this.sink = processor.sink();
    }


    @Override
    public void update(final ZonedDateTime timestamp, Number close, Number high, Number low, Number volume) {
        if (this.ctx == null) {
            ctx = new BarContext(duration, timestamp, low, high, 0, close, volume);
        } else if (ctx.isAfter(timestamp)) {
            // we have new bar starting - emit current ctx
            sink.next(new BaseBar(duration, ctx.endTime, ctx.open.doubleValue(),
                    ctx.high.doubleValue(), ctx.low.doubleValue(), ctx.close.doubleValue(), ctx.volume.doubleValue()));
            // take the close and start counting new context
            ctx = new BarContext(duration, timestamp, low, high, ctx.close, close, volume);
        } else {
            ctx.update(low, high, close, volume);
        }
    }

    @Override
    public Flux<Bar> getBarFlux() {
        return processor;
    }


    private static class BarContext {
        Duration duration;
        ZonedDateTime startTime;
        ZonedDateTime endTime;
        Number low;
        Number high;
        Number open;
        Number close;
        Number volume;


        public BarContext(Duration duration, ZonedDateTime startTime, Number low, Number high, Number open, Number close, Number volume) {
            this.duration = duration;
            this.startTime = startTime;
            this.endTime = startTime.plus(duration);
            this.low = low;
            this.high = high;
            this.open = open;
            this.close = close;
            this.volume = volume;
        }

        boolean isAfter(final ZonedDateTime timestamp) {
            return timestamp.isAfter(endTime.minus(Duration.ofSeconds(1)));
        }

        void update(Number newLow, Number newHigh, Number newClose, Number newVolume){
            low = Math.min(low.doubleValue(), newLow.doubleValue());
            high = Math.max(high.doubleValue(), newHigh.doubleValue());
            close = newClose;
            volume = volume.doubleValue() + newVolume.doubleValue();
        }

    }
}
