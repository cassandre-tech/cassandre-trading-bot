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

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

@DisplayName("Configuration parameters - Valid configuration")
@Configuration({
        @Property(key = "TEST_NAME", value = "Exchange parameters - Valid configuration")
})
@DirtiesContext(classMode = AFTER_CLASS)
@Disabled
public class ValidConfigurationTest extends BaseTest {

    @Test
    @Tag("notReviewed")
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
