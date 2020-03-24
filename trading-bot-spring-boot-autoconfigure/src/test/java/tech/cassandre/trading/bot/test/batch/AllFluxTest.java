package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.test.util.strategy.TestableStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_KEY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_NAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_PASSPHRASE_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_ORDER_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TICKER_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SANDBOX_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SECRET_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_USERNAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_KEY;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_NAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_PASSPHRASE;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SANDBOX;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_SECRET;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_USERNAME;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_ORDER;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_RATE_TICKER;

/**
 * All configuration test.
 */
@SetSystemProperty(key = PARAMETER_NAME, value = PARAMETER_NAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SANDBOX, value = PARAMETER_SANDBOX_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_USERNAME, value = PARAMETER_USERNAME_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_PASSPHRASE, value = PARAMETER_PASSPHRASE_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_KEY, value = PARAMETER_KEY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_SECRET, value = PARAMETER_SECRET_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ACCOUNT, value = PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_TICKER, value = PARAMETER_RATE_TICKER_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ORDER, value = PARAMETER_RATE_ORDER_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE)
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayName("All flux tests")
public class AllFluxTest extends BaseTest {

    /** Cassandre strategy. */
    @Autowired
    private TestableStrategy testableStrategy;

    @Test
    @DisplayName("multi thread test")
    public void multiThreadTest() {
        final int numberOfValuesExpected = 3;

        // Wait for the strategy to have received all the account test values.
        with().await().untilAsserted(() -> assertEquals(numberOfValuesExpected, testableStrategy.getOrdersUpdateReceived().size()));

        // Checking that all other data have been received.
        assertEquals(numberOfValuesExpected, testableStrategy.getTickersUpdateReceived().size());
        assertEquals(numberOfValuesExpected, testableStrategy.getAccountsUpdatesReceived().size());
    }

    /**
     * Change configuration to integrate mocks.
     */
    @SuppressWarnings("unchecked")
    @TestConfiguration
    public static class TestConfig {

        /**
         * Replace ticker flux by mock.
         *
         * @return mock
         */
        @Bean
        @Primary
        public TickerFlux tickerFlux() {
            return new TickerFlux(marketService());
        }

        /**
         * Replace account flux by mock.
         *
         * @return mock
         */
        @Bean
        @Primary
        public AccountFlux accountFlux() {
            return new AccountFlux(userService());
        }

        /**
         * Replace order flux by mock.
         *
         * @return mock
         */
        @Bean
        @Primary
        public OrderFlux orderFlux() {
            return new OrderFlux(tradeService());
        }

        /**
         * UserService mock.
         *
         * @return mocked service
         */
        @SuppressWarnings("unchecked")
        @Bean
        @Primary
        public UserService userService() {
            Map<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();
            final Map<String, AccountDTO> accounts = new LinkedHashMap<>();
            UserService userService = mock(UserService.class);
            // Returns three updates.

            // Account 01.
            BalanceDTO account01Balance1 = BalanceDTO.builder().available(new BigDecimal("1")).create();
            balances.put(CurrencyDTO.BTC, account01Balance1);
            AccountDTO account01 = AccountDTO.builder().id("01").balances(balances).create();
            accounts.put("01", account01);
            UserDTO user01 = UserDTO.builder().setAccounts(accounts).create();
            balances.clear();
            accounts.clear();

            // Account 02.
            BalanceDTO account02Balance1 = BalanceDTO.builder().available(new BigDecimal("1")).create();
            balances.put(CurrencyDTO.BTC, account02Balance1);
            AccountDTO account02 = AccountDTO.builder().id("02").balances(balances).create();
            accounts.put("02", account02);
            UserDTO user02 = UserDTO.builder().setAccounts(accounts).create();
            balances.clear();
            accounts.clear();

            // Account 03.
            BalanceDTO account03Balance1 = BalanceDTO.builder().available(new BigDecimal("1")).create();
            balances.put(CurrencyDTO.BTC, account03Balance1);
            AccountDTO account03 = AccountDTO.builder().id("03").balances(balances).create();
            accounts.put("03", account03);
            UserDTO user03 = UserDTO.builder().setAccounts(accounts).create();
            balances.clear();
            accounts.clear();

            // Mock replies.
            given(userService.getUser()).willReturn(Optional.of(user01), Optional.of(user02), Optional.of(user03));
            return userService;
        }

        /**
         * MarketService mock.
         *
         * @return mocked service
         */
        @Bean
        @Primary
        public MarketService marketService() {
            MarketService service = mock(MarketService.class);
            // Returns three values.
            final CurrencyPairDTO cp1 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);
            given(service.getTicker(cp1)).willReturn(
                    getFakeTicker(cp1, new BigDecimal("1")),    // Ticker 01.
                    getFakeTicker(cp1, new BigDecimal("2")),    // Ticker 02.
                    getFakeTicker(cp1, new BigDecimal("3"))     // Ticker 03.
            );
            return service;
        }

        /**
         * TradeService mock.
         *
         * @return mocked service
         */
        @Bean
        @Primary
        public TradeService tradeService() {
            TradeService service = mock(TradeService.class);

            // Returns three values.
            Set<OrderDTO> reply = new LinkedHashSet<>();
            reply.add(OrderDTO.builder().id("000001").create());    // Order 01.
            reply.add(OrderDTO.builder().id("000002").create());    // Order 02.
            reply.add(OrderDTO.builder().id("000003").create());    // Order 03.
            given(service.getOpenOrders()).willReturn(reply);
            return service;
        }

    }

}
