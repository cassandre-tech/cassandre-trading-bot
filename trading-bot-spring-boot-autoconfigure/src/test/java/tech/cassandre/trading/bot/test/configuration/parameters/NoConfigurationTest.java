package tech.cassandre.trading.bot.test.configuration.parameters;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.springframework.boot.SpringApplication;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.BaseTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_DRY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_SANDBOX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_KEY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_NAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_PASSPHRASE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SECRET;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ORDER;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_TICKER;

@ClearSystemProperty(key = PARAMETER_NAME)
@ClearSystemProperty(key = PARAMETER_SANDBOX)
@ClearSystemProperty(key = PARAMETER_DRY)
@ClearSystemProperty(key = PARAMETER_USERNAME)
@ClearSystemProperty(key = PARAMETER_PASSPHRASE)
@ClearSystemProperty(key = PARAMETER_KEY)
@ClearSystemProperty(key = PARAMETER_SECRET)
@ClearSystemProperty(key = PARAMETER_RATE_ACCOUNT)
@ClearSystemProperty(key = PARAMETER_RATE_TICKER)
@ClearSystemProperty(key = PARAMETER_RATE_ORDER)
@ClearSystemProperty(key = PARAMETER_TESTABLE_STRATEGY_ENABLED)
@ClearSystemProperty(key = PARAMETER_INVALID_STRATEGY_ENABLED)
@DisplayName("No configuration")
@Disabled("Strange error")
// TODO Fix this strange error
public class NoConfigurationTest extends BaseTest {

    @Test
    @DisplayName("Check error messages")
    public void checkErrorMessages() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            final String message = getParametersExceptionMessage(e);
            assertTrue(message.contains("'name'"));
            assertTrue(message.contains("'sandbox'"));
            assertTrue(message.contains("'dry'"));
            assertTrue(message.contains("'username'"));
            assertTrue(message.contains("'passphrase'"));
            assertTrue(message.contains("'key'"));
            assertTrue(message.contains("'secret'"));
            assertTrue(message.contains("Invalid account rate"));
            assertTrue(message.contains("Invalid ticker rate"));
            assertTrue(message.contains("Invalid order rate"));
        }
    }

}
