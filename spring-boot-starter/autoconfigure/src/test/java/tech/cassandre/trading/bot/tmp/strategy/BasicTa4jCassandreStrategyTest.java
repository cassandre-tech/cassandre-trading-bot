package tech.cassandre.trading.bot.tmp.strategy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.tmp.strategy.mocks.BasicTa4jCassandreStrategyTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableTa4jCassandreStrategy;

import java.math.BigDecimal;

import static org.awaitility.Awaitility.await;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.test.util.strategies.InvalidStrategy.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.NoTradingAccountStrategy.PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableTa4jCassandreStrategy.PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED;

@SpringBootTest
@DisplayName("Strategy - Basic ta4j cassandre strategy")
@Configuration({
        @Property(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, value = "true"),
        @Property(key = PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, value = "false")
})
@DirtiesContext(classMode = AFTER_CLASS)
@Import(BasicTa4jCassandreStrategyTestMock.class)
@Disabled
public class BasicTa4jCassandreStrategyTest extends BaseTest {

    private final CurrencyPairDTO cp1 = new CurrencyPairDTO(BTC, USDT);

    @Autowired
    private TestableTa4jCassandreStrategy strategy;

    @Test
    @Tag("notReviewed")
    @DisplayName("check strategy behavior")
    public void checkStrategyBehavior() {
        // Checking received data.
        await().untilAsserted(() -> assertEquals(2, strategy.getAccounts().size()));
        await().untilAsserted(() -> assertEquals(4, strategy.getOrders().size()));
        await().untilAsserted(() -> assertEquals(3, strategy.getTrades().size()));
        await().untilAsserted(() -> assertEquals(15, strategy.getTickersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(1, strategy.getLastTicker().size()));
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("130").compareTo(strategy.getLastTicker().get(cp1).getLast())));

        // Checking ta4j results.
        await().untilAsserted(() -> assertEquals(5, strategy.getEnterCount()));
        await().untilAsserted(() -> assertEquals(2, strategy.getExitCount()));
        await().untilAsserted(() -> assertEquals(8, strategy.getSeries().getBarCount()));

        // Checking that services are available.
        assertNotNull(strategy.getTradeService());
        assertNotNull(strategy.getPositionService());

        // Check getEstimatedBuyingCost()
        assertTrue(strategy.getEstimatedBuyingCost(cp1, new BigDecimal(3)).isPresent());
        assertEquals(0, new BigDecimal("390").compareTo(strategy.getEstimatedBuyingCost(cp1, new BigDecimal(3)).get().getValue()));

        // Test for simplified canBuy() & canSell().
        // 1 BTC / 150 in my account.
        // 1 BTC = 390 UST.
        final AccountDTO account = strategy.getAccounts().get("03");
        assertNotNull(account);

        // canBuy().
        // Trying to buy 1 bitcoin for 390 USDT per bitcoin - should work.
        assertTrue(strategy.canBuy(new BigDecimal("1")));
        assertTrue(strategy.canBuy(account, new BigDecimal("1")));
        // Trying to buy 2 bitcoin for 390 USDT per bitcoin - should not work.
        assertFalse(strategy.canBuy(new BigDecimal("2")));
        assertFalse(strategy.canBuy(account, new BigDecimal("2")));
        // Trying to buy 1 bitcoin for 390 USDT per bitcoin but I want 400 USDT left - should not work.
        assertFalse(strategy.canBuy(new BigDecimal("2"), new BigDecimal("400")));
        assertFalse(strategy.canBuy(account, new BigDecimal("2"), new BigDecimal("400")));

        // canSell().
        // 1 BTC / 500 in my account.
        // Wanting to sell 1 bitcoin - I have them.
        assertTrue(strategy.canSell(new BigDecimal("1")));
        assertTrue(strategy.canSell(account, new BigDecimal("1")));
        // Wanting to sell 2 bitcoin - I don't have them.
        assertFalse(strategy.canSell(new BigDecimal("2")));
        assertFalse(strategy.canSell(account, new BigDecimal("2")));
        // Wanting to sell 1 bitcoin but have one left after - Not possible.
        assertFalse(strategy.canSell(new BigDecimal("1"), new BigDecimal('1')));
        assertFalse(strategy.canSell(account, new BigDecimal("1"), new BigDecimal('1')));
    }

}
