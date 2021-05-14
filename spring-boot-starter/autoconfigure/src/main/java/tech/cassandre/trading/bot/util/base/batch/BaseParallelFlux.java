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
public abstract class BaseParallelFlux<T> extends Base {

    /** Flux. */
    protected final Flux<Set<T>> flux;

    /** Flux sink. */
    protected FluxSink<Set<T>> fluxSink;

    /**
     * Constructor.
     */
    public BaseParallelFlux() {
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
     * Implements this method to backup each update.
     *
     * @param newValue new value
     * @return the value saved
     */
    protected abstract Set<T> saveValue(Set<T> newValue);

    /**
     * Emit new values.
     *
     * @param newValue new value
     */
    public void emitValue(final T newValue) {
        logger.debug("{} flux emits {}", this.getClass().getName(), newValue);
        fluxSink.next(Set.of(newValue));
    }

    /**
     * Emit new values.
     *
     * @param newValue new value
     */
    public void emitValue(final Set<T> newValue) {
        logger.debug("{} flux emits {}", this.getClass().getName(), newValue.size());
        if (!newValue.isEmpty()) {
            fluxSink.next(saveValue(newValue));
        }
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
     * Implements this method to return all the new values. Those values will be sent to the strategy.
     *
     * @return list of new values
     */
    protected abstract Set<T> getNewValues();

    /**
     * Method executed when values must be updated (usually called by schedulers).
     */
    public final void update() {
        try {
            emitValue(getNewValues());
        } catch (RuntimeException e) {
            logger.error("BaseExternalFlux - Error getting new values : " + e.getMessage());
        }
    }

}
