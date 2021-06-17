package tech.cassandre.trading.bot.issues.v4_x.v4_2_0;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 543")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue543Test extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check getter for exchange service in strategies")
    public void checkExchangeServiceGetter() {
        final ExchangeService exchangeService = strategy.getExchangeService();
        assertNotNull(exchangeService);
        assertEquals(1, exchangeService.getAvailableCurrencyPairs().size());
    }

}
