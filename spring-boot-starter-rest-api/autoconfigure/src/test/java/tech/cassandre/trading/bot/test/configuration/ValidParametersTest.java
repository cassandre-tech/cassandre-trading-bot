package tech.cassandre.trading.bot.test.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.test.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.BaseMock;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@DisplayName("Configuration - Api parameters are valid")
@Import(BaseMock.class)
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
