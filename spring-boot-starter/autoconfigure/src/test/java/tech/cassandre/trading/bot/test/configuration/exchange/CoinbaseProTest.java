package tech.cassandre.trading.bot.test.configuration.exchange;

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
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_NAME;

@DisplayName("Configuration - Exchange - Coinbase pro test")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_NAME, value = "coinbasePro")
})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class CoinbaseProTest extends BaseTest {

    @Test
    @DisplayName("Check error messages")
    public void checkErrorMessages() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            //assertTrue(e.getMessage().contains("(HTTP status code: 404)"));
        }
    }

}
