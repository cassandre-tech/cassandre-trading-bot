package tech.cassandre.trading.bot.test.configuration.exchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.fail;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@DisplayName("Configuration - Exchange - Valid configuration")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
public class ValidConfigurationTest extends BaseTest {

    @Test
    @DisplayName("Connection test")
    public void checkConnection() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
        } catch (Exception e) {
            fail("Exception raised during application startup : " + e);
        }
    }

}
