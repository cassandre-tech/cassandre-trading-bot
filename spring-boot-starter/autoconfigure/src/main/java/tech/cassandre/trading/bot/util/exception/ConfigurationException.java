package tech.cassandre.trading.bot.util.exception;

/**
 * Exception in cassandre configuration.
 */
public class ConfigurationException extends RuntimeException {

    /** Advised action to fix the error. */
    private String advisedAction;

    /**
     * Configuration exception without action.
     *
     * @param message error message
     */
    public ConfigurationException(final String message) {
        super(message);
    }

    /**
     * Configuration exception.
     *
     * @param message          error message
     * @param newAdvisedAction advised action to fix this problem
     */
    public ConfigurationException(final String message, final String newAdvisedAction) {
        super(message);
        advisedAction = newAdvisedAction;
    }

    /**
     * Getter for advised action.
     *
     * @return advised action
     */
    public final String getAdvisedAction() {
        return advisedAction;
    }

}
