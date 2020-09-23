package tech.cassandre.trading.bot.test.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.test.strategy.mocks.BasicCassandreStrategyTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;

import static org.awaitility.Awaitility.with;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.USDT;

@SpringBootTest
@DisplayName("Strategy - Basic cassandre strategy")
@Configuration({
        @Property(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = "true"),
        @Property(key = PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, value = "false")
})
@Import(BasicCassandreStrategyTestMock.class)
public class BasicCassandreStrategyTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy testableStrategy;

    @Test
    @DisplayName("check strategy behavior")
    public void checkStrategyBehavior() {
        final int numberOfValuesExpected = 7;

        // Wait for the strategy to have received all the account test values.
        with().await().untilAsserted(() -> assertEquals(numberOfValuesExpected, testableStrategy.getTickersUpdateReceived().size()));

        // Checking that all other data have been received.
        assertFalse(testableStrategy.getOrdersUpdateReceived().isEmpty());
        assertFalse(testableStrategy.getAccountsUpdatesReceived().isEmpty());
        assertFalse(testableStrategy.getTickersUpdateReceived().isEmpty());
        assertFalse(testableStrategy.getTradesUpdateReceived().isEmpty());
        assertFalse(testableStrategy.getPositionsUpdateReceived().isEmpty());
        assertEquals(2, testableStrategy.getLastTicker().size());
        assertEquals(0, new BigDecimal("6").compareTo(testableStrategy.getLastTicker().get(new CurrencyPairDTO(ETH, BTC)).getBid()));

        // Checking that services are available.
        assertNotNull(testableStrategy.getTradeService());
        assertNotNull(testableStrategy.getPositionService());

        // Check getEstimatedBuyingCost()
        assertEquals(0, new BigDecimal("12").compareTo(testableStrategy.getEstimatedBuyingCost(new CurrencyPairDTO(ETH, BTC), new BigDecimal(2)).get().getValue()));

        // Test canBuyMethod().
        final AccountDTO account = testableStrategy.getAccounts().get("03");
        assertNotNull(account);
        assertEquals(3, account.getBalances().size());
        final CurrencyPairDTO cp1 = new CurrencyPairDTO(BTC, ETH);
        final CurrencyPairDTO cp2 = new CurrencyPairDTO(BTC, USDT);

        // Buying something for a ticker we don't have.
        assertFalse(testableStrategy.canBuy(account, cp1, new BigDecimal("0.00001")));
        // Trying to buy a full bitcoin but we only have 2 000 USDT.
        assertFalse(testableStrategy.canBuy(account, cp2, new BigDecimal("1")));
        // Trying to buy a 0.1 bitcoin that costs 1 000 USDT and we have 2 000 USDT.
        assertTrue(testableStrategy.canBuy(account, cp2, new BigDecimal("0.1")));
        // Trying to buy a 0.1 bitcoin that costs 1 000 USDT (we have 2 000 USDT). But we want to have 1 000 USDT left.
        assertFalse(testableStrategy.canBuy(account, cp2, new BigDecimal("0.1"), new BigDecimal("1000")));
    }

}
