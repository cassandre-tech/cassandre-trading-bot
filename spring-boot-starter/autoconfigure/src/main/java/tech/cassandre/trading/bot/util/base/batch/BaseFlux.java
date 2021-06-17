package tech.cassandre.trading.bot.util.base.batch;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import tech.cassandre.trading.bot.util.base.Base;

import java.util.Set;

import static reactor.core.publisher.FluxSink.OverflowStrategy.LATEST;

/**
 * Base flux.
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
     * Method executed when values has to be retrieved (usually called by schedulers).
     */
    public final void update() {
        try {
            emitValues(getNewValues());
        } catch (RuntimeException e) {
            logger.error("{} encountered and error {}", getClass().getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Implements this method to return all the new values. Those values will be sent to the strategy.
     *
     * @return list of new values
     */
    protected abstract Set<T> getNewValues();

    /**
     * Implements this method to backup each new value.
     *
     * @param newValue new value
     * @return the value saved
     */
    protected abstract Set<T> saveValues(Set<T> newValue);

}
