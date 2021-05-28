package tech.cassandre.trading.bot.test.domain;

import io.qase.api.annotation.CaseId;
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
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_NAME_DEFAULT_VALUE;

@SpringBootTest
@DisplayName("Domain - Strategy - After restart")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("schedule-disabled")
public class StrategyExistingTest {

    @Autowired
    private StrategyRepository strategyRepository;

    @Test
    @CaseId(33)
    @DisplayName("Check saved strategy in database when bot restarted")
    public void checkLoadOrderFromDatabase() {
        // Test existing strategy.
        final Optional<Strategy> strategy = strategyRepository.findByStrategyId("01");
        assertTrue(strategy.isPresent());
        assertEquals(1, strategy.get().getId());
        assertEquals("01", strategy.get().getStrategyId());
        assertEquals("Testable strategy", strategy.get().getName());

        // Test non existing strategy.
        assertFalse(strategyRepository.findByStrategyId("NON_EXISTING").isPresent());
    }

}
