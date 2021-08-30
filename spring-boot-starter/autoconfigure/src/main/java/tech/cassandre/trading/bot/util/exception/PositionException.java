package tech.cassandre.trading.bot.util.exception;

/**
 * Exception in cassandre position.
 */
public class PositionException extends RuntimeException {

    /**
     * Position exception.
     *
     * @param message exception message
     */
    public PositionException(final String message) {
        super(message);
    }

}
