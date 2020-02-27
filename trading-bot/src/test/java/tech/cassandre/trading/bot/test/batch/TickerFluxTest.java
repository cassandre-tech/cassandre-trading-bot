package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.BeforeAll;
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
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.test.util.strategy.TestableStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;

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
 * Ticker flux test.
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
@DisplayName("Ticker flux")
public class TickerFluxTest extends BaseTest {

	/** Cassandre strategy. */
	@Autowired
	private TestableStrategy testableStrategy;

	/** Market service. */
	@Autowired
	private MarketService marketService;

	@Test
	@DisplayName("Received data")
	public void testReceivedData() {
		final int numberOfTickersExpected = 13;
		final int numberOfMarketServiceCalls = 16;

		// Currency pairs supported.
		final CurrencyPairDTO cp1 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);
		final CurrencyPairDTO cp2 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.USDT);

		// Waiting for the market service to have been called with all the test data.
		with().pollInterval(fibonacci(SECONDS)).await()
				.atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
				.untilAsserted(() -> verify(marketService, atLeast(numberOfMarketServiceCalls)).getTicker(any()));

		// Checking that somme tickers have already been treated (to verify we work on single thread).
		assertTrue(testableStrategy.getTickersUpdateReceived().size() < numberOfTickersExpected);
		assertTrue(testableStrategy.getTickersUpdateReceived().size() > 0);

		// Wait for the strategy to have received all the test values.
		with().pollInterval(fibonacci(SECONDS)).await()
				.atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
				.untilAsserted(() -> assertTrue(testableStrategy.getTickersUpdateReceived().size() >= numberOfTickersExpected));

		// Test all values received.
		final Iterator<TickerDTO> iterator = testableStrategy.getTickersUpdateReceived().iterator();

		// First value cp1 - 1.
		TickerDTO t = iterator.next();
		assertEquals(cp1, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("1").compareTo(t.getBid()));

		// Second value cp2 - 10.
		t = iterator.next();
		assertEquals(cp2, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("10").compareTo(t.getBid()));

		// Third value cp1 - 2.
		t = iterator.next();
		assertEquals(cp1, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("2").compareTo(t.getBid()));

		// Fourth value cp2 - 20.
		t = iterator.next();
		assertEquals(cp2, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("20").compareTo(t.getBid()));

		// Fifth value cp1 - 3.
		t = iterator.next();
		assertEquals(cp1, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("3").compareTo(t.getBid()));

		// Sixth value cp2 - 30.
		t = iterator.next();
		assertEquals(cp2, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("30").compareTo(t.getBid()));

		// Seventh value cp2 - 40.
		t = iterator.next();
		assertEquals(cp2, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("40").compareTo(t.getBid()));

		// Eighth value cp1 - 4.
		t = iterator.next();
		assertEquals(cp1, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("4").compareTo(t.getBid()));

		// Ninth value cp2 - 50.
		t = iterator.next();
		assertEquals(cp2, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("50").compareTo(t.getBid()));

		// Tenth value cp1 - 5.
		t = iterator.next();
		assertEquals(cp1, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("5").compareTo(t.getBid()));

		// Eleventh value cp2 - 50.
		t = iterator.next();
		assertEquals(cp2, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("60").compareTo(t.getBid()));

		// Twelfth value cp1 - 6.
		t = iterator.next();
		assertEquals(cp1, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("6").compareTo(t.getBid()));

		// Thirteenth value cp2 - 70.
		t = iterator.next();
		assertEquals(cp2, t.getCurrencyPair());
		assertEquals(0, new BigDecimal("70").compareTo(t.getBid()));
	}


	/** Change configuration to integrate mocks. */
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
		 * Replace service by a mock.
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
		 * Replace marketService with a mock.
		 *
		 * @return mocked market service
		 */
		@SuppressWarnings("unchecked")
		@Bean
		@Primary
		public MarketService marketService() {
			// Creates the mock.
			MarketService marketService = mock(MarketService.class);

			// Replies for ETH / BTC.
			final CurrencyPairDTO cp1 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);
			given(marketService
					.getTicker(cp1))
					.willReturn(getFakeTicker(cp1, new BigDecimal("1")),
							getFakeTicker(cp1, new BigDecimal("2")),
							getFakeTicker(cp1, new BigDecimal("3")),
							Optional.empty(),
							getFakeTicker(cp1, new BigDecimal("4")),
							getFakeTicker(cp1, new BigDecimal("5")),
							getFakeTicker(cp1, new BigDecimal("6")),
							Optional.empty()
					);

			// Replies for ETH / USDT.
			final CurrencyPairDTO cp2 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.USDT);
			given(marketService
					.getTicker(cp2))
					.willReturn(getFakeTicker(cp2, new BigDecimal("10")),
							getFakeTicker(cp2, new BigDecimal("20")),
							getFakeTicker(cp2, new BigDecimal("30")),
							getFakeTicker(cp2, new BigDecimal("40")),
							getFakeTicker(cp2, new BigDecimal("50")),
							getFakeTicker(cp2, new BigDecimal("60")),
							Optional.empty(),
							getFakeTicker(cp2, new BigDecimal("70"))
					);
			return marketService;
		}

		/**
		 * Replace service by a mock.
		 *
		 * @return mocked service
		 */
		@Bean
		@Primary
		public TradeService tradeService() {
			TradeService service = mock(TradeService.class);
			given(service.getOpenOrders()).willReturn(new LinkedHashSet<>());
			return service;
		}

	}

}
