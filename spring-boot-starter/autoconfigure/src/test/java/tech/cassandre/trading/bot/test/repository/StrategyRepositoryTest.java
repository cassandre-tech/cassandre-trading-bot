package tech.cassandre.trading.bot.test.repository;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.repository.StrategyRepository;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("Repository - Strategy")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
public class StrategyRepositoryTest {

    @Autowired
    private StrategyRepository strategyRepository;

    @Test
    @CaseId(62)
    @DisplayName("Check imported data")
    public void checkImportedOrders() {
        // Testing an existing strategy.
        Optional<Strategy> strategy = strategyRepository.findByStrategyId("01");
        assertTrue(strategy.isPresent());

        // Testing a non existing strategy.
        strategy = strategyRepository.findByStrategyId("NON_EXISTING");
        assertFalse(strategy.isPresent());
    }

}
