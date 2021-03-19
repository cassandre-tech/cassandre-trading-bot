package tech.cassandre.trading.bot.test.configuration.exchange;

import io.qase.api.annotation.CaseId;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_NAME;

@DisplayName("Configuration - Exchange - Unknown exchange name")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_NAME, value = "foo")
})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class UnknownExchangeTest extends BaseTest {

    @Test
    @CaseId(18)
    @DisplayName("Check error messages")
    public void checkErrorMessages() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            final String message = ExceptionUtils.getRootCause(e).getMessage();
            assertTrue(message.contains("Impossible to find the exchange you requested : foo"));
        }
    }

}
