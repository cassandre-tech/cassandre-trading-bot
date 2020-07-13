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
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.test.util.strategy.TestableCassandreStrategy;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.with;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
 * Trade flux test.
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
@DisplayName("Trade flux")
public class TradeFluxTest extends BaseTest {

    /** Cassandre strategy. */
    @Autowired
    private TestableCassandreStrategy testableStrategy;

    /** Trade service. */
    @Autowired
    private TradeService tradeService;

    @Test
    @DisplayName("Received data")
    public void testReceivedData() {
        final int numberOfTradeExpected = 7;
        final int numberOfTradeServiceCalls = 4;

        // Waiting for the trade service to have been called with all the test data.
        with().pollInterval(fibonacci(SECONDS)).await()
                .atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
                .untilAsserted(() -> verify(tradeService, atLeast(numberOfTradeServiceCalls)).getTrades());

        // Checking that somme tickers have already been treated (to verify we work on a single thread).
        assertTrue(testableStrategy.getTradesUpdateReceived().size() < numberOfTradeExpected);
        assertTrue(testableStrategy.getTradesUpdateReceived().size() > 0);

        // Wait for the strategy to have received all the test values.
        with().pollInterval(fibonacci(SECONDS)).await()
                .atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
                .untilAsserted(() -> assertTrue(testableStrategy.getTradesUpdateReceived().size() >= numberOfTradeExpected));

        // Test all values received.
        final Iterator<TradeDTO> iterator = testableStrategy.getTradesUpdateReceived().iterator();

        assertEquals("0000001", iterator.next().getId());
        assertEquals("0000002", iterator.next().getId());
        assertEquals("0000003", iterator.next().getId());
        assertEquals("0000004", iterator.next().getId());
        assertEquals("0000005", iterator.next().getId());
        assertEquals("0000006", iterator.next().getId());
        assertEquals("0000008", iterator.next().getId());
    }

    /**
     * Change configuration to integrate mocks.
     */
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
         * Replace trade flux by mock.
         *
         * @return mock
         */
        @Bean
        @Primary
        public TradeFlux tradeFlux() {
            return new TradeFlux(tradeService());
        }

        /**
         * UserService mock.
         *
         * @return mocked service
         */
        @Bean
        @Primary
        public UserService userService() {
            UserService service = mock(UserService.class);
            given(service.getUser()).willReturn(Optional.empty());
            return service;
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
            given(service.getTicker(any())).willReturn(Optional.empty());
            return service;
        }

        /**
         * TradeService mock.
         *
         * @return mocked service
         */
        @SuppressWarnings("unchecked")
        @Bean
        @Primary
        public TradeService tradeService() {
            // Creates the mock.
            TradeService tradeService = mock(TradeService.class);

            // =========================================================================================================
            // First reply : 2 trades.
            TradeDTO trade01 = TradeDTO.builder().id("0000001").create();
            TradeDTO trade02 = TradeDTO.builder().id("0000002").create();

            Set<TradeDTO> reply01 = new LinkedHashSet<>();
            reply01.add(trade01);
            reply01.add(trade02);

            // =========================================================================================================
            // First reply : 3 trades.
            TradeDTO trade03 = TradeDTO.builder().id("0000003").create();
            TradeDTO trade04 = TradeDTO.builder().id("0000004").create();
            TradeDTO trade05 = TradeDTO.builder().id("0000005").create();

            Set<TradeDTO> reply02 = new LinkedHashSet<>();
            reply02.add(trade03);
            reply02.add(trade04);
            reply02.add(trade05);

            // =========================================================================================================
            // First reply : 3 trades - Trade07 is again trade 0000003.
            TradeDTO trade06 = TradeDTO.builder().id("0000006").create();
            TradeDTO trade07 = TradeDTO.builder().id("0000003").create();
            TradeDTO trade08 = TradeDTO.builder().id("0000008").create();

            Set<TradeDTO> reply03 = new LinkedHashSet<>();
            reply02.add(trade06);
            reply02.add(trade07);
            reply02.add(trade08);

            // =========================================================================================================
            // Creating the mock.
            given(tradeService.getTrades())
                    .willReturn(reply01,
                            new LinkedHashSet<>(),
                            reply02,
                            reply03);
            return tradeService;
        }
    }

}
