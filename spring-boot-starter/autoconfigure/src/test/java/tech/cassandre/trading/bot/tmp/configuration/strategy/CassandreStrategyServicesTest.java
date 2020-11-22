package tech.cassandre.trading.bot.tmp.configuration.strategy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest
@DisplayName("Strategy configuration - Trade & position services")
@Configuration({
        @Property(key = "TEST_NAME", value = "Strategy configuration - Trade & position services")
})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Disabled
public class CassandreStrategyServicesTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @Tag("notReviewed")
    @DisplayName("Check that trade service is present")
    public void checkTradeService() {
        assertNotNull(strategy.getTradeService());
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check that position service is present")
    public void checkPositionService() {
        assertNotNull(strategy.getPositionService());
    }

    // TODO Test repositories presence.

}
