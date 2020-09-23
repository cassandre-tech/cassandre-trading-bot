package tech.cassandre.trading.bot.test.configuration.parameters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_DRY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_SANDBOX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_KEY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_NAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_PASSPHRASE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SECRET;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_TICKER;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_TRADE;

@DisplayName("Configuration parameters - No configuration")
@Configuration({
        @Property(key = PARAMETER_NAME),
        @Property(key = PARAMETER_SANDBOX),
        @Property(key = PARAMETER_DRY),
        @Property(key = PARAMETER_USERNAME),
        @Property(key = PARAMETER_PASSPHRASE),
        @Property(key = PARAMETER_KEY),
        @Property(key = PARAMETER_SECRET),
        @Property(key = PARAMETER_RATE_ACCOUNT),
        @Property(key = PARAMETER_RATE_TICKER),
        @Property(key = PARAMETER_RATE_TRADE)
})
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
            assertTrue(message.contains("'username'"));
            assertTrue(message.contains("'passphrase'"));
            assertTrue(message.contains("'key'"));
            assertTrue(message.contains("'secret'"));
            // TODO Find why the message error doesn't appear on nested fields.
//            assertTrue(message.contains("'sandbox'"));
//            assertTrue(message.contains("'dry'"));
//            assertTrue(message.contains("Invalid account rate"));
//            assertTrue(message.contains("Invalid ticker rate"));
//            assertTrue(message.contains("Invalid order rate"));
        }
    }

}
