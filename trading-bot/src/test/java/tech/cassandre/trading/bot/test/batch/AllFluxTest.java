package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.with;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Flux configuration test.
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayName("All flux tests")
public class AllFluxTest extends BaseTest {

	@BeforeAll
	static void beforeAll() {
		System.setProperty("cassandre.trading.bot.exchange.name", "kucoin");
		System.setProperty("cassandre.trading.bot.exchange.sandbox", "true");
		System.setProperty("cassandre.trading.bot.exchange.username", "cassandre.crypto.bot@gmail.com");
		System.setProperty("cassandre.trading.bot.exchange.passphrase", "cassandre");
		System.setProperty("cassandre.trading.bot.exchange.key", "5df8eea30092f40009cb3c6a");
		System.setProperty("cassandre.trading.bot.exchange.secret", "5f6e91e0-796b-4947-b75e-eaa5c06b6bed");
		System.setProperty("cassandre.trading.bot.exchange.rates.account", "100");
		System.setProperty("cassandre.trading.bot.exchange.rates.ticker", "101");
		System.setProperty("cassandre.trading.bot.exchange.rates.order", "102");
		System.setProperty("testableStrategy.enabled", "true");
	}

	/** Cassandre strategy. */
	@Autowired
	private TestableStrategy testableStrategy;

	@Test
	@DisplayName("Multithreaded test")
	public void multithreadedTest() {
		final int numberOfAccountsUpdateExpected = 3;

		// Wait for the strategy to have received all the account test values.
		with().pollInterval(fibonacci(SECONDS)).await()
				.atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
				.untilAsserted(() -> assertEquals(numberOfAccountsUpdateExpected, testableStrategy.getAccountsUpdatesReceived().size()));

		System.out.println(testableStrategy.getAccountsUpdatesReceived().size());
		System.out.println(testableStrategy.getTickersUpdateReceived().size());
		System.out.println(testableStrategy.getTickersUpdateReceived().size());
		// Checking that all other data have been received.
		assertFalse(testableStrategy.getTickersUpdateReceived().isEmpty());
		assertFalse(testableStrategy.getOrdersUpdateReceived().isEmpty());
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
		 * Replace service by a mock.
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
		 * Replace service by a mock.
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
		 * Replace service by a mock.
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
