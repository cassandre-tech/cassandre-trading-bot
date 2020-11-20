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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_EXCHANGE_DRY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_EXCHANGE_SANDBOX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_KEY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_NAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_PASSPHRASE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_SECRET;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_TICKER;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_TRADE;

@DisplayName("Exchange parameters - No configuration")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_NAME),
        @Property(key = PARAMETER_EXCHANGE_SANDBOX),
        @Property(key = PARAMETER_EXCHANGE_DRY),
        @Property(key = PARAMETER_EXCHANGE_USERNAME),
        @Property(key = PARAMETER_EXCHANGE_PASSPHRASE),
        @Property(key = PARAMETER_EXCHANGE_KEY),
        @Property(key = PARAMETER_EXCHANGE_SECRET),
        @Property(key = PARAMETER_EXCHANGE_RATE_ACCOUNT),
        @Property(key = PARAMETER_EXCHANGE_RATE_TICKER),
        @Property(key = PARAMETER_EXCHANGE_RATE_TRADE)
})
@DirtiesContext(classMode = AFTER_CLASS)
@Disabled
public class NoConfigurationTest extends BaseTest {

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
            e.printStackTrace();
            assertTrue(message.contains("'name'"));
            assertTrue(message.contains("'username'"));
            assertTrue(message.contains("'passphrase'"));
            assertTrue(message.contains("'key'"));
            assertTrue(message.contains("'secret'"));
            assertTrue(message.contains("'modes.sandbox'"));
            assertTrue(message.contains("'modes.dry'"));
            assertTrue(message.contains("rates.account"));
            assertTrue(message.contains("rates.ticker"));
            assertTrue(message.contains("rates.trade"));
        }
    }

}
