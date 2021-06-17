package tech.cassandre.trading.bot.issues.v4_x.v4_2_1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.LargeTestableCassandreStrategy;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;
import static tech.cassandre.trading.bot.test.util.strategies.LargeTestableCassandreStrategy.PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;

@SpringBootTest
@DisplayName("Github issue 558")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true"),
        @Property(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_LARGE_TESTABLE_STRATEGY_ENABLED, value = "true"),
})
@Import(Issue558TestMock.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue558Test extends BaseTest {

    @Autowired
    private LargeTestableCassandreStrategy strategy;

    @Autowired
    private TickerFlux tickerFlux;

    @Test
    @DisplayName("Check getTickers() on marketService")
    public void checkGetTickers() {
        tickerFlux.update();

        // We should received three tickers with juste one call to getFlux.
        await().untilAsserted(() -> assertEquals(3, strategy.getTickersUpdatesReceived().size()));
    }

}
