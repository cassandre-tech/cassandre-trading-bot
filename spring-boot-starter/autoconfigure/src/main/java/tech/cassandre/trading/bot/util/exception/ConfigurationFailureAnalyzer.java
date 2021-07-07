package tech.cassandre.trading.bot.util.exception;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Failure analyzer for Cassandre configuration error.
 */
public class ConfigurationFailureAnalyzer extends AbstractFailureAnalyzer<ConfigurationException> {

    @Override
    protected final FailureAnalysis analyze(final Throwable rootFailure, final ConfigurationException cause) {
        return new FailureAnalysis(cause.getMessage(), cause.getAdvisedAction(), rootFailure);
    }

}
