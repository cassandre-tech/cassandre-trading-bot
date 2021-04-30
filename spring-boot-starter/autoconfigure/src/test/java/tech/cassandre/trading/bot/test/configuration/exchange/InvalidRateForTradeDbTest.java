package tech.cassandre.trading.bot.test.configuration.exchange;

import io.qase.api.annotation.CaseId;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.BaseDbTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_RATE_TRADE;

@DisplayName("Configuration - Exchange - Invalid trade rate")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_RATE_TRADE, value = "AT20S")
})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Testcontainers
public class InvalidRateForTradeDbTest extends BaseDbTest {

    @Test
    @CaseId(16)
    @DisplayName("Check error messages")
    public void checkErrorMessages() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            final String message = ExceptionUtils.getRootCause(e).getMessage();
            assertFalse(message.contains("'name'"));
            assertFalse(message.contains("'sandbox'"));
            assertFalse(message.contains("'sandbox'"));
            assertFalse(message.contains("'username'"));
            assertFalse(message.contains("'passphrase'"));
            assertFalse(message.contains("'key'"));
            assertFalse(message.contains("'secret'"));
            assertFalse(message.contains("Invalid account rate"));
            assertFalse(message.contains("Invalid ticker rate"));
            assertTrue(message.contains("Invalid trade rate"));
        }
    }

}
