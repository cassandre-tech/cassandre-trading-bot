package tech.cassandre.trading.bot.util.base;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.util.Set;

import static reactor.core.publisher.FluxSink.OverflowStrategy.LATEST;

/**
 * Base external flux.
 *
 * @param <T> flux type
 */
public abstract class BaseExternalFlux<T> extends Base {

    /** Flux. */
    private final Flux<T> flux;

    /** Flux sink. */
    private FluxSink<T> fluxSink;

    /**
     * Constructor.
     */
    public BaseExternalFlux() {
        Flux<T> fluxTemp = Flux.create(newFluxSink -> this.fluxSink = newFluxSink, getOverflowStrategy());
        flux = fluxTemp.publishOn(Schedulers.elastic());
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
     * Implements this method to return all the new values. Those values will be sent to the strategy.
     *
     * @return list of new values
     */
    protected abstract Set<T> getNewValues();

    /**
     * Method executed when values must be updated (usually called by the Scheduler).
     */
    public final void update() {
        final Set<T> newValues = getNewValues();
        newValues.forEach(this::emitValue);
    }

    /**
     * Emit a new value.
     *
     * @param newValue new value
     */
    public void emitValue(final T newValue) {
        logger.debug("{} flux emits a new value : {}", this.getClass().getName(), newValue);
        saveValue(newValue);
        fluxSink.next(newValue);
    }

    /**
     * Implements this method to backup each update.
     *
     * @param newValue new value
     */
    public void saveValue(final T newValue) {

    }

    /**
     * Getter for flux.
     *
     * @return flux
     */
    public final Flux<T> getFlux() {
        return flux;
    }

}
