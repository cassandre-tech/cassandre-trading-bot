package tech.cassandre.trading.bot.issues;

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

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DisplayName("Github issue 421")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/issue470.sql")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(Issue470Mock.class)
public class Issue470 extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TickerFlux tickerFlux;

    @Test
    @DisplayName("Check onPositionStatusUpdate not called after restart")
    public void checkGainsCalculation() throws InterruptedException {
        // Position 5 on ETH/BTC is OPENED and is present in database.
        // We call the ticker flux that should update the latestPrice of position.
        // That should trigger a position update but NOT a position status update.
        tickerFlux.update();
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);

        assertEquals(1, strategy.getPositionsUpdateReceived().size());
        assertEquals(0, strategy.getPositionsStatusUpdateReceived().size());
    }

}
