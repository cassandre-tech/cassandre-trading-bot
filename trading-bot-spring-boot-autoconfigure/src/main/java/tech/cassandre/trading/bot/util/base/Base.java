package tech.cassandre.trading.bot.util.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base.
 */
public abstract class Base {

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Getter for logger.
     *
     * @return logger
     */
    protected final Logger getLogger() {
        return logger;
    }

}
