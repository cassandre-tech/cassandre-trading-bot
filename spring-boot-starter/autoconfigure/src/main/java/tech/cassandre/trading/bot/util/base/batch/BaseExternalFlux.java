package tech.cassandre.trading.bot.util.base.batch;

import java.util.Set;

/**
 * Base external flux.
 *
 * @param <T> flux type
 */
public abstract class BaseExternalFlux<T> extends BaseFlux<T> {

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
        getNewValues().forEach(this::emitValue);
    }

}
