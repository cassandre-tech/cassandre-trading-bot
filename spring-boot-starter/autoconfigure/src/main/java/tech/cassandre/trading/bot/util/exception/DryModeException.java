package tech.cassandre.trading.bot.util.exception;

/**
 * Exception in cassandre dry mode.
 */
public class DryModeException extends RuntimeException {

    /**
     * Dry mode exception.
     *
     * @param message exception message
     */
    public DryModeException(final String message) {
        super(message);
    }

}
