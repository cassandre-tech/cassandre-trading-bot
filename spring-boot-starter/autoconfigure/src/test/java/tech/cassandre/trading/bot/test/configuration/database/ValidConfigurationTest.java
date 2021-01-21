package tech.cassandre.trading.bot.test.configuration.database;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.test.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DisplayName("Configuration - Database - Valid configuration")
@Configuration({
        @Property(key = "TEST_NAME", value = "Database parameters - Valid configuration")
})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class ValidConfigurationTest extends BaseTest {

    @Test
    @CaseId(8)
    @DisplayName("Database connection test")
    public void checkConnection() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
        } catch (Exception e) {
            fail("Exception raised during application startup : " + e);
        }
    }

}
