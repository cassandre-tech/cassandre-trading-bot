package tech.cassandre.trading.bot.util.ta4j;

import org.ta4j.core.Bar;
import reactor.core.publisher.Flux;

import java.time.ZonedDateTime;

/**
 * Component to aggregate bars and provide a Flux of {@link Bar}.
 */
public interface BarAggregator {

    /**
     * Updates the dar data.
     * @param timestamp time of the tick
     * @param latestPrice latest price
     */
    void update(ZonedDateTime timestamp, Number latestPrice);

    /**
     * Gets the {@link Flux}.
     * @return flux of Bars
     */
    Flux<Bar> getBarFlux();
}
