package tech.cassandre.trading.bot.test.core.strategy.basic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
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
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.EUR;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy.PARAMETER_TESTABLE_STRATEGY_ENABLED;

@SpringBootTest
@DisplayName("Strategy - Basic cassandre strategy")
@Configuration({
        @Property(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = "true"),
})
@DirtiesContext(classMode = BEFORE_CLASS)
@Import(BasicCassandreStrategyTestMock.class)
public class BasicCassandreStrategyTest extends BaseTest {

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check strategy behavior")
    public void checkStrategyBehavior() {
        // =============================================================================================================
        // We check that the strategy is correctly registered in database.
        Optional<Strategy> strategyInDatabase = strategyRepository.findByStrategyId("01");
        assertTrue(strategyInDatabase.isPresent());
        assertEquals(1, strategyInDatabase.get().getUid());
        assertEquals("01", strategyInDatabase.get().getStrategyId());
        assertEquals("Testable strategy", strategyInDatabase.get().getName());

        // =============================================================================================================
        // We check accounts updates (4 replies : account 01, account 02, account 03 and again account 03).
        // But the 4 replies only concerns 3 account.
        // We have 1 BTC, 10 ETH and 100 USDT.
        with().await().untilAsserted(() -> assertEquals(4, strategy.getAccountsUpdatesReceived().size()));
        assertEquals(3, strategy.getAccounts().size());

        // Testing trade account.
        final Optional<AccountDTO> tradeAccount = strategy.getTradeAccount();
        assertTrue(tradeAccount.isPresent());
        assertEquals("03", tradeAccount.get().getAccountId());
        assertEquals("trade", tradeAccount.get().getName());

        // Testing trade account balances.
        assertEquals(3, strategy.getTradeAccountBalances().size());
        assertEquals(0, new BigDecimal("1").compareTo(strategy.getTradeAccountBalances().get(BTC).getAvailable()));
        assertEquals(0, new BigDecimal("10").compareTo(strategy.getTradeAccountBalances().get(ETH).getAvailable()));
        assertEquals(0, new BigDecimal("100").compareTo(strategy.getTradeAccountBalances().get(USDT).getAvailable()));

        // =============================================================================================================
        // We check that all tickers arrived (6 ETH/BTC & 1 ETH/USDT).
        // 1 ETH = 6 BTC.
        // 1 ETH = 10 000 USDT.
        with().await().untilAsserted(() -> assertEquals(7, strategy.getTickersUpdatesReceived().size()));
        assertEquals(2, strategy.getLastTickers().size());
        assertNotNull(strategy.getLastTickers().get(ETH_BTC));
        assertNotNull(strategy.getLastTickers().get(ETH_USDT));
        assertEquals(0, new BigDecimal("6").compareTo(strategy.getLastTickers().get(ETH_BTC).getLast()));
        assertEquals(0, new BigDecimal("10000").compareTo(strategy.getLastTickers().get(ETH_USDT).getLast()));

        // =============================================================================================================
        // Check getEstimatedBuyingCost().
        // As 1 ETH cost 6 BTC, 2 ETH would cost 12 BTC.
        final Optional<CurrencyAmountDTO> estimatedBuyingCost = strategy.getEstimatedBuyingCost(ETH_BTC, new BigDecimal(2));
        assertTrue(estimatedBuyingCost.isPresent());
        assertEquals(0, new BigDecimal("12").compareTo(estimatedBuyingCost.get().getValue()));

        // =============================================================================================================
        // Check getEstimatedBuyableAmount()
        // As 1 ETH cost 10 000 USDT, with 5 000 USDT, I should be able to buy 0.5 ETH.
        // And I check I can't 260 EUR with ETH as I don't have EURO price.
        // And I check I can't buy USDT with EURO as I don't have EURO.
        final Optional<BigDecimal> estimatedBuyableAmount = strategy.getEstimatedBuyableAmount(new CurrencyAmountDTO(new BigDecimal(5000), USDT), ETH);
        assertTrue(estimatedBuyableAmount.isPresent());
        assertEquals(0, new BigDecimal("0.5").compareTo(estimatedBuyableAmount.get()));
        assertFalse(strategy.getEstimatedBuyableAmount(new CurrencyAmountDTO(new BigDecimal(260), EUR), ETH).isPresent());
        assertFalse(strategy.getEstimatedBuyableAmount(new CurrencyAmountDTO(new BigDecimal(260), USDT), EUR).isPresent());

        // =============================================================================================================
        // Check canBuy() & canSell().
        // Our assets: we have 1 BTC, 10 ETH and 100 USDT.
        // 1 ETH costs 6 BTC.
        // 1 ETH costs 10 000 USDT.
        final AccountDTO account = strategy.getAccounts().get("03");
        assertNotNull(account);

        // canBuy().
        // Buying something for an asset we don't have.
        assertFalse(strategy.canBuy(BTC_ETH, new BigDecimal("0.00001")));
        assertFalse(strategy.canBuy(account, BTC_ETH, new BigDecimal("0.00001")));
        // Trying to buy a full bitcoin, but we only have 2 000 USDT.
        assertFalse(strategy.canBuy(ETH_USDT, new BigDecimal("1")));
        assertFalse(strategy.canBuy(account, ETH_USDT, new BigDecimal("1")));
        // Trying to buy a 0.01 ETH that will cost 100 USDT, and we have 100 USDT, it should work but not for 0.011.
        assertTrue(strategy.canBuy(ETH_USDT, new BigDecimal("0.01")));
        assertTrue(strategy.canBuy(account, ETH_USDT, new BigDecimal("0.01")));
        assertFalse(strategy.canBuy(account, ETH_USDT, new BigDecimal("0.011")));
        // Trying to buy a 0.01 bitcoin that costs 100 USDT (we have 100 USDT). But we want to have 1 USDT left.
        assertFalse(strategy.canBuy(ETH_USDT, new BigDecimal("0.01"), new BigDecimal("1")));
        assertFalse(strategy.canBuy(ETH_USDT, new BigDecimal("0.01"), new BigDecimal("1")));
        assertFalse(strategy.canBuy(account, ETH_USDT, new BigDecimal("0.01"), new BigDecimal("1")));
        assertFalse(strategy.canBuy(account, ETH_USDT, new BigDecimal("0.01"), new BigDecimal("1")));

        // canSell().
        // Selling an asset we don't have (EUR).
        assertFalse(strategy.canSell(EUR, new BigDecimal("0.00001")));
        assertFalse(strategy.canSell(account, EUR, new BigDecimal("0.00001")));
        // Trying to sell 1 BTC (we have them).
        assertTrue(strategy.canSell(BTC, new BigDecimal("1")));
        assertTrue(strategy.canSell(account, BTC, new BigDecimal("1")));
        // Trying to sell 3 BTC (we don't have them).
        assertFalse(strategy.canSell(BTC, new BigDecimal("3")));
        assertFalse(strategy.canSell(account, BTC, new BigDecimal("3")));
        // Trying to sell 1 BTC and still have 1 (not possible).
        assertFalse(strategy.canSell(BTC, new BigDecimal("1"), new BigDecimal("1")));
        assertFalse(strategy.canSell(account, BTC, new BigDecimal("1"), new BigDecimal("1")));
    }

}
