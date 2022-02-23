package tech.cassandre.trading.bot.test.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.repository.StrategyRepository;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Domain - Strategy")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false"),
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class StrategyTest {

    @Autowired
    private StrategyRepository strategyRepository;

    @Test
    @DisplayName("Check saved strategy in database")
    public void checkSavedStrategyFromDatabase() {
        // Test existing strategy.
        final Optional<Strategy> strategy = strategyRepository.findByStrategyId("01");
        assertTrue(strategy.isPresent());
        assertEquals(1, strategy.get().getUid());
        assertEquals("01", strategy.get().getStrategyId());
        assertEquals("Testable strategy", strategy.get().getName());

        // Test equals.
        final Optional<Strategy> sBis = strategyRepository.findByStrategyId("01");
        assertTrue(sBis.isPresent());
        assertEquals(strategy.get(), sBis.get());

        // Test non-existing strategy.
        assertFalse(strategyRepository.findByStrategyId("NON_EXISTING").isPresent());
    }

}
