package tech.cassandre.trading.bot.tmp.configuration.parameters.exchange;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_ACCOUNT;

@DisplayName("Exchange parameters - Invalid account rate")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_RATE_ACCOUNT, value = "A")
})
@DirtiesContext(classMode = AFTER_CLASS)
@Disabled
public class InvalidRateForAccountTest extends BaseTest {

    @Test
    @Tag("notReviewed")
    @DisplayName("Check error messages")
    public void checkErrorMessages() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            final String message = getParametersExceptionMessage(e);
            assertFalse(message.contains("'name'"));
            assertFalse(message.contains("'sandbox'"));
            assertFalse(message.contains("'dry'"));
            assertFalse(message.contains("'username'"));
            assertFalse(message.contains("'passphrase'"));
            assertFalse(message.contains("'key'"));
            assertFalse(message.contains("'secret'"));
            assertTrue(message.contains("Invalid account rate"));
            assertFalse(message.contains("Invalid ticker rate"));
            assertFalse(message.contains("Invalid order rate"));
        }
    }

}
