package tech.cassandre.trading.bot.test.issues.v4_x.v4_2_0;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 539")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@ActiveProfiles("schedule-disabled")
@Import(Issue539TestMock.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue539Test extends BaseTest {

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check scheduled tasks continue to work after exception")
    public void checkExceptionInScheduledTasks() {
        tickerFlux.update();
        tickerFlux.update();
        tickerFlux.update();

        // We should receive two tickers.
        await().untilAsserted(() -> assertEquals(2, strategy.getTickersUpdatesReceived().size()));
    }

}
