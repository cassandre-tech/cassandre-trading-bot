package tech.cassandre.trading.bot.test.configuration.parameters.exchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.util.parameters.ExchangeParameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_PROXY_HOST;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_PROXY_PORT;

@DisplayName("Exchange parameters - Specific exchange parameters")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_PROXY_HOST, value = "127.0.0.1"),
        @Property(key = PARAMETER_EXCHANGE_PROXY_PORT, value = "4780")
})
public class ExchangeSpecificationTest extends BaseTest {

    @Test
    @DisplayName("Check proxy host & port")
    public void checkProxyAndHostParameters() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            final ConfigurableApplicationContext run = application.run();
            final ExchangeParameters exchangeParameters = run.getBean(ExchangeParameters.class);
            assertEquals("127.0.0.1", exchangeParameters.getProxyHost());
            assertEquals(4780, exchangeParameters.getProxyPort());
            fail("No exception raised");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("java.net.ConnectException"));
        }
    }

}
