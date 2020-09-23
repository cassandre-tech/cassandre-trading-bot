package tech.cassandre.trading.bot.test.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.test.strategy.mocks.BasicTa4jCassandreStrategyTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableTa4jCassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;

import static org.awaitility.Awaitility.await;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.USDT;

@SpringBootTest
@DisplayName("Strategy - Basic ta4j cassandre strategy")
@Configuration({
        @Property(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, value = "true")
})
@Import(BasicTa4jCassandreStrategyTestMock.class)
public class BasicTa4jCassandreStrategyTest extends BaseTest {

    @Autowired
    private TestableTa4jCassandreStrategy strategy;

    @Test
    @DisplayName("check strategy behavior")
    public void checkStrategyBehavior() {
        // Checking received data.
        await().untilAsserted(() -> assertEquals(3, strategy.getAccounts().size()));
        await().untilAsserted(() -> assertEquals(4, strategy.getOrders().size()));
        await().untilAsserted(() -> assertEquals(3, strategy.getTrades().size()));
        await().untilAsserted(() -> assertEquals(3, strategy.getPositions().size()));
        await().untilAsserted(() -> assertEquals(15, strategy.getTickersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(1, strategy.getLastTicker().size()));
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("130").compareTo(strategy.getLastTicker().get(new CurrencyPairDTO(BTC, USDT)).getLast())));

        // Checking ta4j results.
        await().untilAsserted(() -> assertEquals(5, strategy.getEnterCount()));
        await().untilAsserted(() -> assertEquals(2, strategy.getExitCount()));
        await().untilAsserted(() -> assertEquals(8, strategy.getSeries().getBarCount()));

        // Checking that services are available.
        assertNotNull(strategy.getTradeService());
        assertNotNull(strategy.getPositionService());

        // Check getEstimatedBuyingCost()
        assertEquals(0, new BigDecimal("390").compareTo(strategy.getEstimatedBuyingCost(new CurrencyPairDTO(BTC, USDT), new BigDecimal(3)).get().getValue()));
    }

}
