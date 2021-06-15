package tech.cassandre.trading.bot.test.configuration.exchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRIVER_CLASS_NAME;

@DisplayName("Configuration - Exchange - Coinbase")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRIVER_CLASS_NAME, value = "coinbase")
})
public class CoinbaseTest extends BaseTest {

    @Test
    @DisplayName("Check error messages")
    public void checkErrorMessages() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("(HTTP status code: 404)"));
        }
    }

}
