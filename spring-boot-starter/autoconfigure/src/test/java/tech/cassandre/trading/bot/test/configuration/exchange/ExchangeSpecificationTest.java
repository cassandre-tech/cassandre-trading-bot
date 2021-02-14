package tech.cassandre.trading.bot.test.configuration.exchange;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.util.parameters.ExchangeParameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_HOST;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_PLAIN_TEXT_URI;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_PORT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_PROXY_HOST;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_PROXY_PORT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_SSL_URI;

@DisplayName("Configuration - Exchange - Specific exchange parameters")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_PROXY_HOST, value = "127.0.0.1"),
        @Property(key = PARAMETER_EXCHANGE_PROXY_PORT, value = "4780"),
        @Property(key = PARAMETER_EXCHANGE_SSL_URI, value = "https://someexchange.com"),
        @Property(key = PARAMETER_EXCHANGE_PLAIN_TEXT_URI, value = "http://someexchange.com"),
        @Property(key = PARAMETER_EXCHANGE_HOST, value = "www.someexchange.com"),
        @Property(key = PARAMETER_EXCHANGE_PORT, value = "443")
})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class ExchangeSpecificationTest extends BaseTest {

    @Test
    @CaseId(12)
    @DisplayName("Check proxy host & port")
    public void checkProxyAndHostParameters() {
        try {
            //TODO do not init directly, delegate it to test framework
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);

            //TODO catch exception like - Assertions.assertThrows(ConnectException.class, ()->application.run());
            //TODO do not use "run" directly, delegate it to test framework
            final ConfigurableApplicationContext run = application.run();

            final ExchangeParameters exchangeParameters = run.getBean(ExchangeParameters.class);
            //TODO this code is never called... as exception is thrown during "application.run()" call
            assertEquals("127.0.0.1", exchangeParameters.getProxyHost());
            assertEquals(4780, exchangeParameters.getProxyPort());
            assertEquals("https://someexchange.com", exchangeParameters.getSslUri());
            assertEquals("http://someexchange.com", exchangeParameters.getPlainTextUri());
            assertEquals("www.someexchange.com", exchangeParameters.getHost());
            assertEquals(443, exchangeParameters.getPort());
            fail("No exception raised");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("java.net.ConnectException"));
        }
    }

}
