package tech.cassandre.trading.bot.test.configuration.parameters.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Database parameters - Valid configuration")
@Configuration({
        @Property(key = "TEST_NAME", value = "Database parameters - Valid configuration")
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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
