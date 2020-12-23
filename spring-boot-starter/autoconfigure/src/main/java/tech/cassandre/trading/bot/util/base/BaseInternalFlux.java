package tech.cassandre.trading.bot.util.base;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import static reactor.core.publisher.FluxSink.OverflowStrategy.LATEST;

/**
 * Base external flux.
 *
 * @param <T> flux type
 */
public abstract class BaseInternalFlux<T> extends Base {

    /** Flux. */
    private final Flux<T> flux;

    /** Flux sink. */
    private FluxSink<T> fluxSink;

    /**
     * Constructor.
     */
    public BaseInternalFlux() {
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
     * Emit a new value.
     *
     * @param newValue new value
     */
    public void emitValue(final T newValue) {
        logger.debug("{} flux emits a new value : {}", this.getClass().getName(), newValue);
        if (newValue != null) {
            backupValue(newValue);
            fluxSink.next(newValue);
        }
    }

    /**
     * Implements this method to backup each update.
     *
     * @param newValue new value
     */
    public void backupValue(final T newValue) {

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
