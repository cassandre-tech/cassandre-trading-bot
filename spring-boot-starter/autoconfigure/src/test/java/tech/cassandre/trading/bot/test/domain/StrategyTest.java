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
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_NAME_DEFAULT_VALUE;

@SpringBootTest
@DisplayName("Domain - Strategy - Creation")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("schedule-disabled")
public class StrategyTest {

    @Autowired
    private StrategyRepository strategyRepository;

    @Test
    @CaseId(34)
    @DisplayName("Check saved strategy in database")
    public void checkLoadOrderFromDatabase() {
        // Test existing strategy.
        final Optional<Strategy> s = strategyRepository.findByStrategyId("01");
        assertTrue(s.isPresent());
        assertEquals(1, s.get().getId());
        assertEquals("01", s.get().getStrategyId());
        assertEquals("Testable strategy", s.get().getName());
        assertEquals(PARAMETER_NAME_DEFAULT_VALUE, s.get().getExchangeAccount().getExchange());
        assertEquals("cassandre.crypto.bot@gmail.com", s.get().getExchangeAccount().getAccount());

        // Test equals.
        final Optional<Strategy> sBis = strategyRepository.findByStrategyId("01");
        assertTrue(sBis.isPresent());
        assertEquals(s.get(), sBis.get());

        // Test non existing strategy.
        assertFalse(strategyRepository.findByStrategyId("NON_EXISTING").isPresent());
    }

}
