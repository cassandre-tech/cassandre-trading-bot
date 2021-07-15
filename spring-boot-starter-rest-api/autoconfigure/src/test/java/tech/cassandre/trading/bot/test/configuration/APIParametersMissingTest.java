package tech.cassandre.trading.bot.test.configuration;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import tech.cassandre.trading.bot.test.CassandreTradingBot;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Configuration - Api parameters are missing")
public class APIParametersMissingTest {

    @BeforeAll
    static void beforeAll() {
        System.setProperty("cassandre.trading.bot.api.key", "");
        System.setProperty("cassandre.trading.bot.api.secret", "");
    }

    @AfterAll
    static void afterAll() {
        System.setProperty("cassandre.trading.bot.api.key", "myapikey");
        System.setProperty("cassandre.trading.bot.api.secret", "myapisecret");
    }

    @Test
    @DisplayName("Check error messages")
    public void checkErrorMessages() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            final String message = ExceptionUtils.getRootCause(e).getMessage();
            assertTrue(message.contains("API key required"));
            assertTrue(message.contains("API secret required"));
        }
    }

}
