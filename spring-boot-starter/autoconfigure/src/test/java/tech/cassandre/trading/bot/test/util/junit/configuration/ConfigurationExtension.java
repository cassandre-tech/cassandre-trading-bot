package tech.cassandre.trading.bot.test.util.junit.configuration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_DRY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_SANDBOX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_KEY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_NAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_PASSPHRASE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SECRET;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_TRADE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_TICKER;

/**
 * Configuration extension - set and clear system properties.
 */
@NotThreadSafe // system properties are JVM-global, so don't run tests using this rule in parallel
public class ConfigurationExtension implements BeforeAllCallback, AfterAllCallback {

    /** Invalid strategy enabled parameter. */
    public static final String PARAMETER_INVALID_STRATEGY_ENABLED = "invalidStrategy.enabled";

    /** Testable strategy enabled parameter. */
    public static final String PARAMETER_TESTABLE_STRATEGY_ENABLED = "testableStrategy.enabled";

    /** Invalid strategy enabled parameter. */
    public static final String PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE = "false";

    /** Testable strategy enabled parameter. */
    public static final String PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE = "true";

    /** Testable ta4j strategy enabled parameter. */
    public static final String PARAMETER_TESTABLE_TA4J_STRATEGY_DEFAULT_VALUE = "false";

    /** Exchange name parameter. */
    public static final String PARAMETER_NAME_DEFAULT_VALUE = "kucoin";

    /** Sandbox parameter. */
    public static final String PARAMETER_SANDBOX_DEFAULT_VALUE = "true";

    /** Dry parameter. */
    public static final String PARAMETER_DRY_DEFAULT_VALUE = "false";

    /** Username parameter. */
    public static final String PARAMETER_USERNAME_DEFAULT_VALUE = "cassandre.crypto.bot@gmail.com";

    /** Passphrase parameter. */
    public static final String PARAMETER_PASSPHRASE_DEFAULT_VALUE = "cassandre";

    /** Key parameter. */
    public static final String PARAMETER_KEY_DEFAULT_VALUE = "5df8eea30092f40009cb3c6a";

    /** Secret parameter. */
    public static final String PARAMETER_SECRET_DEFAULT_VALUE = "5f6e91e0-796b-4947-b75e-eaa5c06b6bed";

    /** Rate for account parameter. */
    public static final String PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE = "100";

    /** Rate for account parameter (long value). */
    public static final String PARAMETER_RATE_ACCOUNT_LONG_VALUE = "PT5S";

    /** Rate for ticker parameter. */
    public static final String PARAMETER_RATE_TICKER_DEFAULT_VALUE = "101";

    /** Rate for ticker parameter (long value). */
    public static final String PARAMETER_RATE_TICKER_LONG_VALUE = "PT5S";

    /** Rate for trade parameter. */
    public static final String PARAMETER_RATE_TRADE_DEFAULT_VALUE = "102";

    /** Rate for trade parameter (long value). */
    public static final String PARAMETER_RATE_TRADE_LONG_VALUE = "PT5S";

    @Override
    public void beforeAll(ExtensionContext context) {
        // Default values.
        System.setProperty(PARAMETER_NAME, PARAMETER_NAME_DEFAULT_VALUE);                                                   // Kucoin
        System.setProperty(PARAMETER_SANDBOX, PARAMETER_SANDBOX_DEFAULT_VALUE);                                             // true
        System.setProperty(PARAMETER_DRY, PARAMETER_DRY_DEFAULT_VALUE);                                                     // false
        System.setProperty(PARAMETER_USERNAME, PARAMETER_USERNAME_DEFAULT_VALUE);                                           // cassandre.crypto.bot@gmail.com
        System.setProperty(PARAMETER_PASSPHRASE, PARAMETER_PASSPHRASE_DEFAULT_VALUE);                                       // cassandre
        System.setProperty(PARAMETER_KEY, PARAMETER_KEY_DEFAULT_VALUE);                                                     // 5df8eea30092f40009cb3c6a
        System.setProperty(PARAMETER_SECRET, PARAMETER_SECRET_DEFAULT_VALUE);                                               // 5f6e91e0-796b-4947-b75e-eaa5c06b6bed
        System.setProperty(PARAMETER_RATE_ACCOUNT, PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE);                                   // 100
        System.setProperty(PARAMETER_RATE_TICKER, PARAMETER_RATE_TICKER_DEFAULT_VALUE);                                     // 101
        System.setProperty(PARAMETER_RATE_TRADE, PARAMETER_RATE_TRADE_DEFAULT_VALUE);                                       // 102
        System.setProperty(PARAMETER_INVALID_STRATEGY_ENABLED, PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE);                   // false
        System.setProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED, PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE);                 // true
        System.setProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_DEFAULT_VALUE, PARAMETER_TESTABLE_TA4J_STRATEGY_DEFAULT_VALUE); // false

        // Retrieve all the properties set by the annotation.
        final Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isPresent()) {
            final Configuration configuration = testClass.get().getAnnotation(Configuration.class);
            final Iterator<Property> systemPropertyIterator = Arrays.stream(configuration.value()).iterator();
            systemPropertyIterator.forEachRemaining(s -> {
                if (s.value().equals("")) {
                    System.clearProperty(s.key());
                } else {
                    System.setProperty(s.key(), s.value());
                }
            });
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        // Reset values.
        System.clearProperty(PARAMETER_NAME);
        System.clearProperty(PARAMETER_SANDBOX);
        System.clearProperty(PARAMETER_DRY);
        System.clearProperty(PARAMETER_USERNAME);
        System.clearProperty(PARAMETER_PASSPHRASE);
        System.clearProperty(PARAMETER_KEY);
        System.clearProperty(PARAMETER_SECRET);
        System.clearProperty(PARAMETER_RATE_ACCOUNT);
        System.clearProperty(PARAMETER_RATE_TICKER);
        System.clearProperty(PARAMETER_RATE_TRADE);
        System.clearProperty(PARAMETER_INVALID_STRATEGY_ENABLED);
        System.clearProperty(PARAMETER_TESTABLE_STRATEGY_ENABLED);
        System.clearProperty(PARAMETER_TESTABLE_TA4J_STRATEGY_DEFAULT_VALUE);

        // Remove all the properties set for this method.
        final Optional<Class<?>> testClass = context.getTestClass();
        if (testClass.isPresent()) {
            final Configuration configuration = testClass.get().getAnnotation(Configuration.class);
            final Iterator<Property> systemPropertyIterator = Arrays.stream(configuration.value()).iterator();
            systemPropertyIterator.forEachRemaining(s -> System.clearProperty(s.key()));
        }
    }

}
