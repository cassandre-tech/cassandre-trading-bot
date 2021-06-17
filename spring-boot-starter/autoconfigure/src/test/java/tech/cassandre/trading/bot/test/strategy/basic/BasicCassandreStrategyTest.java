package tech.cassandre.trading.bot.test.strategy.basic;

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
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.with;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static tech.cassandre.trading.bot.dto.strategy.StrategyTypeDTO.BASIC_STRATEGY;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.EUR;
import static tech.cassandre.trading.bot.test.util.strategies.InvalidStrategy.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.NoTradingAccountStrategy.PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.strategies.TestableTa4jCassandreStrategy.PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED;

@SpringBootTest
@DisplayName("Strategy - Basic cassandre strategy")
@Configuration({
        @Property(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = "true"),
        @Property(key = PARAMETER_TESTABLE_TA4J_STRATEGY_ENABLED, value = "false"),
        @Property(key = PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED, value = "false")
})
@DirtiesContext(classMode = BEFORE_CLASS)
@Import(BasicCassandreStrategyTestMock.class)
public class BasicCassandreStrategyTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private StrategyRepository strategyRepository;

    @Test
    @DisplayName("Check strategy behavior")
    public void checkStrategyBehavior() {
        final int numberOfValuesExpected = 7;
        strategy.getAccounts().remove("trade");

        // Check type.
        Optional<Strategy> strategyInDatabase = strategyRepository.findByStrategyId("01");
        assertTrue(strategyInDatabase.isPresent());
        assertEquals(BASIC_STRATEGY, strategyInDatabase.get().getType());

        // Wait for the strategy to have received all the account test values.
        with().await().untilAsserted(() -> assertTrue(strategy.getTickersUpdatesReceived().size() >= numberOfValuesExpected));

        // Checking that all other data have been received.
        assertFalse(strategy.getOrdersUpdatesReceived().isEmpty());
        assertFalse(strategy.getAccountsUpdatesReceived().isEmpty());
        assertFalse(strategy.getTickersUpdatesReceived().isEmpty());
        assertFalse(strategy.getTradesUpdatesReceived().isEmpty());
        assertEquals(2, strategy.getLastTickers().size());
        assertEquals(0, new BigDecimal("6").compareTo(strategy.getLastTickers().get(ETH_BTC).getLast()));

        // Trading account test.
        with().await().untilAsserted(() -> assertEquals(3, strategy.getAccountsUpdatesReceived().size()));
        final Optional<AccountDTO> tradeAccount = strategy.getTradeAccount();
        assertTrue(tradeAccount.isPresent());
        assertEquals("03", tradeAccount.get().getAccountId());

        // Check getEstimatedBuyingCost().
        assertTrue(strategy.getEstimatedBuyingCost(ETH_BTC, new BigDecimal(2)).isPresent());
        assertEquals(0, new BigDecimal("12").compareTo(strategy.getEstimatedBuyingCost(ETH_BTC, new BigDecimal(2)).get().getValue()));

        // Check canBuy() & canSell().
        final AccountDTO account = strategy.getAccounts().get("03");
        assertNotNull(account);
        assertEquals(3, account.getBalances().size());

        // canBuy().
        // Buying something for an asset we don't have.
        assertFalse(strategy.canBuy(BTC_ETH, new BigDecimal("0.00001")));
        assertFalse(strategy.canBuy(account, BTC_ETH, new BigDecimal("0.00001")));
        // Trying to buy a full bitcoin but we only have 2 000 USDT.
        assertFalse(strategy.canBuy(ETH_USDT, new BigDecimal("1")));
        assertFalse(strategy.canBuy(account, ETH_USDT, new BigDecimal("1")));
        // Trying to buy a 0.1 bitcoin that costs 1 000 USDT and we have 2 000 USDT.
        assertTrue(strategy.canBuy(ETH_USDT, new BigDecimal("0.1")));
        assertTrue(strategy.canBuy(account, ETH_USDT, new BigDecimal("0.1")));
        // Trying to buy a 0.1 bitcoin that costs 1 001 USDT (we have 2 000 USDT). But we want to have 1 000 USDT left.
        assertFalse(strategy.canBuy(ETH_USDT, new BigDecimal("0.1"), new BigDecimal("1001")));
        assertFalse(strategy.canBuy(ETH_USDT, new BigDecimal("0.1"), new BigDecimal("1001")));
        assertFalse(strategy.canBuy(account, ETH_USDT, new BigDecimal("0.1"), new BigDecimal("1001")));
        assertFalse(strategy.canBuy(account, ETH_USDT, new BigDecimal("0.1"), new BigDecimal("1001")));

        // canSell().
        // Selling  an asset we don't have.
        assertFalse(strategy.canSell(EUR, new BigDecimal("0.00001")));
        assertFalse(strategy.canSell(account, EUR, new BigDecimal("0.00001")));
        // Trying to sell 1 BTC (we have them).
        assertTrue(strategy.canSell(BTC, new BigDecimal("1")));
        assertTrue(strategy.canSell(account, BTC, new BigDecimal("1")));
        // Trying to sell 3 BTC (we don't have them).
        assertFalse(strategy.canSell(BTC, new BigDecimal("3")));
        assertFalse(strategy.canSell(account, BTC, new BigDecimal("3")));
        // Trying to sell 1 BTC and still have 1 (not possible).
        assertFalse(strategy.canSell(BTC, new BigDecimal("1"), new BigDecimal("2")));
        assertFalse(strategy.canSell(account, BTC, new BigDecimal("1"), new BigDecimal("2")));
    }

}
