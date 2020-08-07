package tech.cassandre.trading.bot.util.exception;

/**
 * Exception in cassandre configuration.
 */
public class ConfigurationException extends RuntimeException {

    /** Advised action to fix the error. */
    private String action;

    /**
     * Constructor without action.
     *
     * @param message error message
     */
    public ConfigurationException(final String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param message   error message
     * @param newAction advised action to fix this problem
     */
    public ConfigurationException(final String message, final String newAction) {
        super(message);
        action = newAction;
    }

    /**
     * Getter for action.
     *
     * @return action
     */
    public final String getAction() {
        return action;
    }

}
