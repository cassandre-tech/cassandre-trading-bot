package tech.cassandre.trading.bot.issues.v4_x.v4_1_0;

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
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 482")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(Issue482TestMock.class)
public class Issue482Test extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TickerFlux tickerFlux;

    @Test
    @DisplayName("Cannot retrieve binance tickers due to missing timestamp")
    public void checkTickersWithMissingTimestamp() {
        // Send three tickers with null dates.
        tickerFlux.update();
        tickerFlux.update();
        tickerFlux.update();

        // We should received three tickers.
        await().untilAsserted(() -> assertTrue(strategy.getTickersUpdatesReceived().size() > 3));
    }

}
