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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_NAME;

@DisplayName("Configuration - Exchange - Coinbase pro")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_NAME, value = "org.knowm.xchange.coinbasepro.CoinbaseProExchange")
})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Testcontainers
public class CoinbaseProDbTest extends BaseDbTest {

    @Test
    @CaseId(9)
    @DisplayName("Check error messages")
    public void checkErrorMessages() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            final String message = ExceptionUtils.getRootCause(e).getMessage();
            assertTrue(message.contains("Illegal base64 character 2d"));
        }
    }

}
