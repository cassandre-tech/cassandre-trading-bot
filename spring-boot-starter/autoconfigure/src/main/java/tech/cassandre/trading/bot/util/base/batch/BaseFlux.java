package tech.cassandre.trading.bot.util.base.batch;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import tech.cassandre.trading.bot.util.base.Base;

import java.util.Collections;
import java.util.Set;

import static reactor.core.publisher.FluxSink.OverflowStrategy.LATEST;

/**
 * Base flux.
 * <p>
 * update() method is called by schedulers, and it does two things:
 * - Calls the getNewValues() method you implemented to retrieve new values from "outside" (for example: call the service to retrieve new tickers).
 * - For each value retrieved previously, we call the saveValues() method you implemented to save all the data in the database.
 * - Each value saved in database is then push to the flux to be consumed by strategies.
 * note: you are not forced to implement getNewValues() or saveValues().
 *
 * @param <T> flux
 */
public abstract class BaseFlux<T> extends Base {

    /** Flux. */
    protected final Flux<Set<T>> flux;

    /** Flux sink. */
    protected FluxSink<Set<T>> fluxSink;

    /**
     * Constructor.
     */
    public BaseFlux() {
        flux = Flux.create(newFluxSink -> this.fluxSink = newFluxSink, getOverflowStrategy());
    }

    /**
     * Set the default overflow strategy - override to change it.
     *
     * @return overflow strategy
     */
    @SuppressWarnings("SameReturnValue")
    protected FluxSink.OverflowStrategy getOverflowStrategy() {
        return LATEST;
    }

    /**
     * Getter for flux.
     *
     * @return flux
     */
    public Flux<Set<T>> getFlux() {
        return flux;
    }

    /**
     * Method executed when values have to be retrieved (usually called by schedulers).
     */
    public final void update() {
        try {
            emitValues(getNewValues());
        } catch (RuntimeException e) {
            logger.error("{} encountered an error {}", getClass().getSimpleName(), e.getMessage());
        }
    }

    /**
     * Emit new value.
     *
     * @param newValue new value
     */
    public void emitValue(final T newValue) {
        emitValues(Set.of(newValue));
    }

    /**
     * Emit new values.
     *
     * @param newValues new values
     */
    public void emitValues(final Set<T> newValues) {
        if (!newValues.isEmpty()) {
            logger.debug("{} flux emits {} values", getClass().getSimpleName(), newValues.size());
            fluxSink.next(saveValues(newValues));
        }
    }

    /**
     * Implements this method to return all the new values. Those values will be sent to the strategy.
     *
     * @return list of new values
     */
    protected Set<T> getNewValues() {
        return Collections.emptySet();
    }

    /**
     * Implements this method to save values coming from flux.
     *
     * @param newValue new value
     * @return the value saved
     */
    protected Set<T> saveValues(final Set<T> newValue) {
        return newValue;
    }

}
