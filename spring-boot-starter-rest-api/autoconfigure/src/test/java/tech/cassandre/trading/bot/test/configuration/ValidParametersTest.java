package tech.cassandre.trading.bot.test.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import tech.cassandre.trading.bot.test.CassandreTradingBot;

import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Configuration - Api parameters are valid")
public class ValidParametersTest {

    @Test
    @DisplayName("Check error messages")
    public void checkErrorMessages() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
        } catch (Exception e) {
            fail("Exception was raised: " + e.getMessage());
        }
    }

}
