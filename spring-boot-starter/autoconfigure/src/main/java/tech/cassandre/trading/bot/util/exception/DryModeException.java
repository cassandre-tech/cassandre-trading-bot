package tech.cassandre.trading.bot.util.exception;

/**
 * Dry mode exception.
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
