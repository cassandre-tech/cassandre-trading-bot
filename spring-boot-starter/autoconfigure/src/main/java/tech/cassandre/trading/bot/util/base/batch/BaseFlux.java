package tech.cassandre.trading.bot.util.base.batch;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
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
        Flux<Set<T>> fluxTemp = Flux.create(newFluxSink -> this.fluxSink = newFluxSink, getOverflowStrategy());
        flux = fluxTemp.publishOn(Schedulers.boundedElastic());
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
            logger.debug("{} flux emits {} values", this.getClass().getName(), newValues.size());
            fluxSink.next(saveValues(newValues));
        }
    }

    /**
     * Method executed when values must be updated (usually called by schedulers).
     */
    public final void update() {
        try {
            emitValues(getNewValues());
        } catch (RuntimeException e) {
            logger.error(getClass().getSimpleName() + " - Error getting new values : " + e.getMessage());
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
     * Implements this method to backup each update.
     *
     * @param newValue new value
     * @return the value saved
     */
    protected abstract Set<T> saveValues(Set<T> newValue);

}
