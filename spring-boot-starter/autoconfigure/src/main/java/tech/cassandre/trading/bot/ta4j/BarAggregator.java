package tech.cassandre.trading.bot.ta4j;

import org.ta4j.core.Bar;
import reactor.core.publisher.Flux;

import java.time.ZonedDateTime;

public interface BarAggregator {

    void update(final ZonedDateTime timestamp, final Number close, final Number high, final Number low, final Number volume);

    Flux<Bar> getBarFlux();
}
