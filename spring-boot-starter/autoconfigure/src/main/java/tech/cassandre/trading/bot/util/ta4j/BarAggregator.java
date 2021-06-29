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
     * @param close close price
     * @param high high price
     * @param low low price
     * @param volume volume
     */
    void update(ZonedDateTime timestamp, Number close, Number high, Number low, Number volume);

    /**
     * Gets the {@link Flux}.
     * @return flux of Bars
     */
    Flux<Bar> getBarFlux();
}
