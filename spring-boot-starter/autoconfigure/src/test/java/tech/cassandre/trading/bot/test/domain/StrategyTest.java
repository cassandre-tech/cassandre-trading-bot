package tech.cassandre.trading.bot.test.domain;

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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@SpringBootTest
@DisplayName("Domain - Strategy")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("schedule-disabled")
public class StrategyTest {

    @Autowired
    private StrategyRepository strategyRepository;

    @Test
    @DisplayName("Check saved strategy in database")
    public void checkLoadOrderFromDatabase() {
        // Test existing strategy.
        final Optional<Strategy> strategy = strategyRepository.findById("1");
        assertTrue(strategy.isPresent());
        assertEquals("Testable strategy", strategy.get().getName());
        assertNotNull(strategy.get().getCreatedOn());
        assertNull(strategy.get().getUpdatedOn());

        // Test non existing strategy.
        assertFalse(strategyRepository.findById("NON_EXISTING").isPresent());

    }

}
