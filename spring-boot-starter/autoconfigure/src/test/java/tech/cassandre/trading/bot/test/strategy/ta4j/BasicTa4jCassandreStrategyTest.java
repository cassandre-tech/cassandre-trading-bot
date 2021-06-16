package tech.cassandre.trading.bot.test.strategy.ta4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.repository.StrategyRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableTa4jCassandreStrategy;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.dto.strategy.StrategyTypeDTO.BASIC_TA4J_STRATEGY;
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
public class BasicTa4jCassandreStrategyTest extends BaseTest {

    @Autowired
    private TestableTa4jCassandreStrategy strategy;

    @Autowired
    private StrategyRepository strategyRepository;

    @Test
    @DisplayName("Check strategy behavior")
    public void checkStrategyBehavior() {
        // Check type.
        Optional<Strategy> strategyInDatabase = strategyRepository.findByStrategyId("01");
        assertTrue(strategyInDatabase.isPresent());
        assertEquals(BASIC_TA4J_STRATEGY, strategyInDatabase.get().getType());

        await().untilAsserted(() -> assertEquals(3, strategy.getAccounts().size()));
        await().untilAsserted(() -> assertEquals(4, strategy.getOrders().size()));
        await().untilAsserted(() -> assertEquals(3, strategy.getTrades().size()));
        await().untilAsserted(() -> assertEquals(15, strategy.getTickersUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(1, strategy.getLastTickers().size()));
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("130").compareTo(strategy.getLastTickers().get(BTC_USDT).getLast())));

        // Check ta4j results.
//        await().untilAsserted(() -> assertEquals(5, strategy.getEnterCount()));
//        await().untilAsserted(() -> assertEquals(2, strategy.getExitCount()));
        //TODO: discuss the above - is it really correct? The bars have same value, there should be no entry

        await().untilAsserted(() -> assertEquals(7, strategy.getSeries().getBarCount()));

        // Check getEstimatedBuyingCost()
        assertTrue(strategy.getEstimatedBuyingCost(BTC_USDT, new BigDecimal(3)).isPresent());
        assertEquals(0, new BigDecimal("390").compareTo(strategy.getEstimatedBuyingCost(BTC_USDT, new BigDecimal(3)).get().getValue()));

        // Test for simplified canBuy() & canSell().
        // 1 BTC / 150 in my account.
        // 1 BTC = 390 UST.
        final AccountDTO account = strategy.getAccounts().get("03");
        assertNotNull(account);
        strategy.getAccounts().remove("01");

        // Check canBuy().
        // Trying to buy 1 bitcoin for 390 USDT per bitcoin - should work.
        assertTrue(strategy.canBuy(new BigDecimal("1")));
        assertTrue(strategy.canBuy(account, new BigDecimal("1")));
        // Trying to buy 2 bitcoin for 390 USDT per bitcoin - should not work.
        assertFalse(strategy.canBuy(new BigDecimal("2")));
        assertFalse(strategy.canBuy(account, new BigDecimal("2")));
        // Trying to buy 1 bitcoin for 390 USDT per bitcoin but I want 400 USDT left - should not work.
        assertFalse(strategy.canBuy(new BigDecimal("2"), new BigDecimal("400")));
        assertFalse(strategy.canBuy(account, new BigDecimal("2"), new BigDecimal("400")));

        // Check canSell().
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
