package tech.cassandre.trading.bot.test.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_DRY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_INVALID_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_KEY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_NAME_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_PASSPHRASE_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_ACCOUNT_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TICKER_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_RATE_TRADE_LONG_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SANDBOX_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_SECRET_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_TESTABLE_STRATEGY_ENABLED;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_USERNAME_DEFAULT_VALUE;
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
@SetSystemProperty(key = PARAMETER_RATE_ACCOUNT, value = PARAMETER_RATE_ACCOUNT_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_TICKER, value = PARAMETER_RATE_TICKER_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_RATE_ORDER, value = PARAMETER_RATE_TRADE_LONG_VALUE)
@SetSystemProperty(key = PARAMETER_TESTABLE_STRATEGY_ENABLED, value = PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE)
@SetSystemProperty(key = PARAMETER_INVALID_STRATEGY_ENABLED, value = PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE)
@SpringBootTest
@ActiveProfiles("schedule-disabled")
@DisplayName("Rates")
public class RatesTest {

	/** Waiting time. */
	private static final long WAITING_TIME = 2900;

	/** User service. */
	@Autowired
	private UserService userService;

	/** Market service. */
	@Autowired
	private MarketService marketService;

	/** Trade service. */
	@Autowired
	private TradeService tradeService;

	@Test
	@DisplayName("Account service rate")
	public void accountServiceRateTest() throws InterruptedException {
		Thread.sleep(3 * WAITING_TIME);
		AtomicInteger numberOfCalls = new AtomicInteger(0);

		// Executing service calls in parallel.
		ExecutorService executor = Executors.newFixedThreadPool(2);
		// Call number 1.
		executor.submit(() -> {
			userService.getUser();
			numberOfCalls.incrementAndGet();
		});
		// Call number 2.
		executor.submit(() -> {
			userService.getUser();
			numberOfCalls.incrementAndGet();
		});

		// Right after, one call must have been made.
		Thread.sleep(WAITING_TIME);
		assertEquals(1, numberOfCalls.intValue());

		// After waiting the rate time, we should have two calls.
		Thread.sleep(WAITING_TIME);
		assertEquals(2, numberOfCalls.intValue());
	}

	@Test
	@DisplayName("Market service rate")
	public void marketServiceRateTest() throws InterruptedException {
		Thread.sleep(3 * WAITING_TIME);
		AtomicInteger numberOfCalls = new AtomicInteger(0);
		final CurrencyPairDTO cp = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);

		// Executing service calls in parallel.
		ExecutorService executor = Executors.newFixedThreadPool(2);
		// Call number 1.
		executor.submit(() -> {
			marketService.getTicker(cp);
			numberOfCalls.incrementAndGet();
		});
		// Call number 2.
		executor.submit(() -> {
			marketService.getTicker(cp);
			numberOfCalls.incrementAndGet();
		});

		// Right after, one call must have been made.
		Thread.sleep(WAITING_TIME);
		assertEquals(1, numberOfCalls.intValue());

		// After waiting the rate time, we should have two calls.
		Thread.sleep(WAITING_TIME);
		assertEquals(2, numberOfCalls.intValue());
	}

	@Test
	@DisplayName("Trade service rate")
	public void tradeServiceRateTest() throws InterruptedException {
		Thread.sleep(3 * WAITING_TIME);
		AtomicInteger numberOfCalls = new AtomicInteger(0);

		// Executing service calls in parallel.
		ExecutorService executor = Executors.newFixedThreadPool(2);
		// Call number 1.
		executor.submit(() -> {
			tradeService.getOpenOrders();
			numberOfCalls.incrementAndGet();
		});
		// Call number 2.
		executor.submit(() -> {
			tradeService.getOpenOrderByOrderId("DUMMY");
			numberOfCalls.incrementAndGet();
		});

		// Right after, one call must have been made.
		Thread.sleep(WAITING_TIME);
		assertEquals(1, numberOfCalls.intValue());

		// After waiting the rate time, we should have two calls.
		Thread.sleep(WAITING_TIME);
		assertEquals(2, numberOfCalls.intValue());
	}

}
