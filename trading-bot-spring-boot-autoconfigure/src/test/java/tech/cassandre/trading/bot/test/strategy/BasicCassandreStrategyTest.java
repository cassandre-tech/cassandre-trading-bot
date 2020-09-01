package tech.cassandre.trading.bot.test.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.test.util.strategy.TestableCassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;

import static org.awaitility.Awaitility.with;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_DRY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_KEY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_NAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_PASSPHRASE_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TICKER_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TRADE_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SANDBOX_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SECRET_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_USERNAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_DRY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_SANDBOX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_KEY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_NAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_PASSPHRASE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SECRET;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ORDER;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_TICKER;

@SetSystemProperty(key = PARAMETER_NAME, value = PARAMETER_NAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SANDBOX, value = PARAMETER_SANDBOX_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_DRY, value = PARAMETER_DRY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_USERNAME, value = PARAMETER_USERNAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_PASSPHRASE, value = PARAMETER_PASSPHRASE_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_KEY, value = PARAMETER_KEY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SECRET, value = PARAMETER_SECRET_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ACCOUNT, value = PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_TICKER, value = PARAMETER_RATE_TICKER_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ORDER, value = PARAMETER_RATE_TRADE_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE)
@SpringBootTest
@Import(BasicCassandreStrategyTestMock.class)
@DisplayName("Basic cassandre strategy")
public class BasicCassandreStrategyTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy testableStrategy;

    @Test
    @DisplayName("strategy test")
    public void strategyTest() {
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

        testableStrategy.getLastTicker().values().forEach(System.out::println);
    }

}
