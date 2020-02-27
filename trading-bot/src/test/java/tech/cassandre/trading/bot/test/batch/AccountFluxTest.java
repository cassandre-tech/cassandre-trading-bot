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
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.user.BalanceDTO;
import tech.cassandre.trading.bot.dto.user.UserDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.test.util.strategy.TestableStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.ETH;
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
 * Account flux test.
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
@DisplayName("Account flux")
public class AccountFluxTest extends BaseTest {

	/** Cassandre strategy. */
	@Autowired
	private TestableStrategy testableStrategy;

	/** User service. */
	@Autowired
	private UserService userService;

	@Test
	@DisplayName("Received data")
	public void testReceivedData() {
		final int numberOfAccountsUpdateExpected = 6;
		final int numberOfUserServiceCallsExpected = 6;

		// Waiting for the user service to have been called with all the test data.
		with().pollInterval(fibonacci(SECONDS)).await()
				.atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
				.untilAsserted(() -> verify(userService, atLeast(numberOfUserServiceCallsExpected)).getUser());

		// Checking that somme accounts update have already been treated (to verify we work on single thread).
		assertTrue(testableStrategy.getAccountsUpdatesReceived().size() < numberOfAccountsUpdateExpected);
		assertTrue(testableStrategy.getAccountsUpdatesReceived().size() > 0);

		// Wait for the strategy to have received all the test values.
		with().pollInterval(fibonacci(SECONDS)).await()
				.atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
				.untilAsserted(() -> assertEquals(numberOfAccountsUpdateExpected, testableStrategy.getAccountsUpdatesReceived().size()));

		// Checking values.
		final Iterator<AccountDTO> iterator = testableStrategy.getAccountsUpdatesReceived().iterator();

		// Check update 1.
		AccountDTO accountUpdate = iterator.next();
		assertEquals("01", accountUpdate.getId());
		assertEquals(2, accountUpdate.getBalances().size());

		// Check update 2.
		accountUpdate = iterator.next();
		assertEquals("02", accountUpdate.getId());
		assertEquals(1, accountUpdate.getBalances().size());

		// Check update 3.
		accountUpdate = iterator.next();
		assertEquals("01", accountUpdate.getId());
		assertEquals(3, accountUpdate.getBalances().size());

		// Check update 4.
		accountUpdate = iterator.next();
		assertEquals("01", accountUpdate.getId());
		assertTrue(accountUpdate.getBalance(ETH).isPresent());
		assertEquals(new BigDecimal("5"), accountUpdate.getBalance(ETH).get().getBorrowed());

		// Check update 5.
		accountUpdate = iterator.next();
		assertEquals("02", accountUpdate.getId());
		assertTrue(accountUpdate.getBalance(CurrencyDTO.BTC).isPresent());
		assertEquals(new BigDecimal("2"), accountUpdate.getBalance(CurrencyDTO.BTC).get().getFrozen());

		// Check update 6.
		accountUpdate = iterator.next();
		assertEquals("01", accountUpdate.getId());
		assertEquals(2, accountUpdate.getBalances().size());
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
		 * Replace service by a mock.
		 *
		 * @return mocked service
		 */
		@SuppressWarnings("unchecked")
		@Bean
		@Primary
		public UserService userService() {
			// Creates the mock.
			Map<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();
			final Map<String, AccountDTO> accounts = new LinkedHashMap<>();
			UserService userService = mock(UserService.class);

			// =========================================================================================================
			// Account 1 with 2 balances.
			// Account 2 with 1 balance.
			BalanceDTO account01Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("1"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			BalanceDTO account01Balance2 = BalanceDTO.builder()
					.available(new BigDecimal("2"))
					.borrowed(new BigDecimal("2"))
					.currency(ETH)
					.depositing(new BigDecimal("2"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("2"))
					.total(new BigDecimal("2"))
					.withdrawing(new BigDecimal("2"))
					.create();
			balances.put(CurrencyDTO.BTC, account01Balance1);
			balances.put(ETH, account01Balance2);
			AccountDTO account01 = AccountDTO.builder().id("01").name("01").balances(balances).create();
			BalanceDTO account02Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("1"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			balances.clear();
			balances.put(CurrencyDTO.BTC, account02Balance1);
			AccountDTO account02 = AccountDTO.builder().id("02").name("02").balances(balances).create();
			accounts.put("01", account01);
			accounts.put("02", account02);
			UserDTO user01 = UserDTO.builder().setAccounts(accounts).create();

			// =========================================================================================================
			// Account 1 with 3 balances.
			// Account 2 with 1 balance.
			// Change : Account 1 has now 3 balances.
			BalanceDTO account03Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("1"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			BalanceDTO account03Balance2 = BalanceDTO.builder()
					.available(new BigDecimal("2"))
					.borrowed(new BigDecimal("2"))
					.currency(ETH)
					.depositing(new BigDecimal("2"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("2"))
					.total(new BigDecimal("2"))
					.withdrawing(new BigDecimal("2"))
					.create();
			BalanceDTO account03Balance3 = BalanceDTO.builder()
					.available(new BigDecimal("2"))
					.borrowed(new BigDecimal("2"))
					.currency(CurrencyDTO.USDT)
					.depositing(new BigDecimal("2"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("2"))
					.total(new BigDecimal("2"))
					.withdrawing(new BigDecimal("2"))
					.create();
			balances.clear();
			balances.put(CurrencyDTO.BTC, account03Balance1);
			balances.put(ETH, account03Balance2);
			balances.put(CurrencyDTO.USDT, account03Balance3);
			AccountDTO account03 = AccountDTO.builder().id("01").name("01").balances(balances).create();
			BalanceDTO account04Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("1"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			balances.clear();
			balances.put(CurrencyDTO.BTC, account04Balance1);
			AccountDTO account04 = AccountDTO.builder().id("02").name("02").balances(balances).create();
			accounts.clear();
			accounts.put("01", account03);
			accounts.put("02", account04);
			UserDTO user02 = UserDTO.builder().setAccounts(accounts).create();

			// =========================================================================================================
			// Account 1 with 3 balances.
			// Account 2 with 1 balance.
			// Change : No change.
			BalanceDTO account05Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("1"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			BalanceDTO account05Balance2 = BalanceDTO.builder()
					.available(new BigDecimal("2"))
					.borrowed(new BigDecimal("2"))
					.currency(ETH)
					.depositing(new BigDecimal("2"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("2"))
					.total(new BigDecimal("2"))
					.withdrawing(new BigDecimal("2"))
					.create();
			BalanceDTO account05Balance3 = BalanceDTO.builder()
					.available(new BigDecimal("2"))
					.borrowed(new BigDecimal("2"))
					.currency(CurrencyDTO.USDT)
					.depositing(new BigDecimal("2"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("2"))
					.total(new BigDecimal("2"))
					.withdrawing(new BigDecimal("2"))
					.create();
			balances.clear();
			balances.put(CurrencyDTO.BTC, account05Balance1);
			balances.put(ETH, account05Balance2);
			balances.put(CurrencyDTO.USDT, account05Balance3);
			AccountDTO account05 = AccountDTO.builder().id("01").name("01").balances(balances).create();
			BalanceDTO account06Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("1"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			balances.clear();
			balances.put(CurrencyDTO.BTC, account06Balance1);
			AccountDTO account06 = AccountDTO.builder().id("02").name("02").balances(balances).create();
			accounts.clear();
			accounts.put("01", account05);
			accounts.put("02", account06);
			UserDTO user03 = UserDTO.builder().setAccounts(accounts).create();

			// =========================================================================================================
			// Account 1 with 3 balances.
			// Account 2 with 1 balance.
			// Change : ETH balance of account 1 changed (borrowed value) & balance of account 2 (frozen).
			BalanceDTO account07Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("1"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			BalanceDTO account07Balance2 = BalanceDTO.builder()
					.available(new BigDecimal("2"))
					.borrowed(new BigDecimal("5"))
					.currency(ETH)
					.depositing(new BigDecimal("2"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("2"))
					.total(new BigDecimal("2"))
					.withdrawing(new BigDecimal("2"))
					.create();
			BalanceDTO account07Balance3 = BalanceDTO.builder()
					.available(new BigDecimal("2"))
					.borrowed(new BigDecimal("2"))
					.currency(CurrencyDTO.USDT)
					.depositing(new BigDecimal("2"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("2"))
					.total(new BigDecimal("2"))
					.withdrawing(new BigDecimal("2"))
					.create();
			balances.clear();
			balances.put(CurrencyDTO.BTC, account07Balance1);
			balances.put(ETH, account07Balance2);
			balances.put(CurrencyDTO.USDT, account07Balance3);
			AccountDTO account07 = AccountDTO.builder().id("01").name("01").balances(balances).create();
			BalanceDTO account08Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			balances.clear();
			balances.put(CurrencyDTO.BTC, account08Balance1);
			AccountDTO account08 = AccountDTO.builder().id("02").name("02").balances(balances).create();
			accounts.clear();
			accounts.put("01", account07);
			accounts.put("02", account08);
			UserDTO user04 = UserDTO.builder().setAccounts(accounts).create();

			// =========================================================================================================
			// Account 1 with 3 balances.
			// Account 2 with 1 balance.
			// Change : no change.
			BalanceDTO account09Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("1"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			BalanceDTO account09Balance2 = BalanceDTO.builder()
					.available(new BigDecimal("2"))
					.borrowed(new BigDecimal("5"))
					.currency(ETH)
					.depositing(new BigDecimal("2"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("2"))
					.total(new BigDecimal("2"))
					.withdrawing(new BigDecimal("2"))
					.create();
			BalanceDTO account09Balance3 = BalanceDTO.builder()
					.available(new BigDecimal("2"))
					.borrowed(new BigDecimal("2"))
					.currency(CurrencyDTO.USDT)
					.depositing(new BigDecimal("2"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("2"))
					.total(new BigDecimal("2"))
					.withdrawing(new BigDecimal("2"))
					.create();
			balances.clear();
			balances.put(CurrencyDTO.BTC, account09Balance1);
			balances.put(ETH, account09Balance2);
			balances.put(CurrencyDTO.USDT, account09Balance3);
			AccountDTO account09 = AccountDTO.builder().id("01").name("01").balances(balances).create();
			BalanceDTO account10Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			balances.clear();
			balances.put(CurrencyDTO.BTC, account10Balance1);
			AccountDTO account10 = AccountDTO.builder().id("02").name("02").balances(balances).create();
			accounts.clear();
			accounts.put("01", account09);
			accounts.put("02", account10);
			UserDTO user05 = UserDTO.builder().setAccounts(accounts).create();

			// =========================================================================================================
			// Account 1 with 2 balances.
			// Account 2 with 1 balance.
			// Change : one balance removed on account 1.
			BalanceDTO account11Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("1"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			BalanceDTO account11Balance2 = BalanceDTO.builder()
					.available(new BigDecimal("2"))
					.borrowed(new BigDecimal("2"))
					.currency(CurrencyDTO.USDT)
					.depositing(new BigDecimal("2"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("2"))
					.total(new BigDecimal("2"))
					.withdrawing(new BigDecimal("2"))
					.create();
			balances.clear();
			balances.put(CurrencyDTO.BTC, account11Balance1);
			balances.put(CurrencyDTO.USDT, account11Balance2);
			AccountDTO account11 = AccountDTO.builder().id("01").name("01").balances(balances).create();
			BalanceDTO account12Balance1 = BalanceDTO.builder()
					.available(new BigDecimal("1"))
					.borrowed(new BigDecimal("1"))
					.currency(CurrencyDTO.BTC)
					.depositing(new BigDecimal("1"))
					.frozen(new BigDecimal("2"))
					.loaned(new BigDecimal("1"))
					.total(new BigDecimal("1"))
					.withdrawing(new BigDecimal("1"))
					.create();
			balances.clear();
			balances.put(CurrencyDTO.BTC, account12Balance1);
			AccountDTO account12 = AccountDTO.builder().id("02").name("02").balances(balances).create();
			accounts.clear();
			accounts.put("01", account11);
			accounts.put("02", account12);
			UserDTO user06 = UserDTO.builder().setAccounts(accounts).create();

			// Mock.
			given(userService.getUser())
					.willReturn(Optional.of(user01),
							Optional.empty(),
							Optional.of(user02),
							Optional.of(user03),
							Optional.of(user04),
							Optional.empty(),
							Optional.of(user05),
							Optional.of(user06)
					);
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
			given(service.getTicker(any())).willReturn(Optional.empty());
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
			given(service.getOpenOrders()).willReturn(new LinkedHashSet<>());
			return service;
		}

	}

}
