package tech.cassandre.trading.bot.test.services.xchange;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.services.xchange.mocks.RatesTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_RATE_ACCOUNT;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_RATE_TICKER;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_RATE_TRADE;

@SpringBootTest
@DisplayName("Service - XChange - Rates")
@ActiveProfiles("schedule-disabled")
@Configuration({
		@Property(key = PARAMETER_EXCHANGE_RATE_ACCOUNT, value = "PT10S"),	// 10 seconds.
		@Property(key = PARAMETER_EXCHANGE_RATE_TICKER, value = "PT15S"),	// 15 seconds.
		@Property(key = PARAMETER_EXCHANGE_RATE_TRADE, value = "PT20S")		// 20 seconds.
})
@Import(RatesTestMock.class)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class RatesTest extends BaseTest {

	@Autowired
	private UserService userService;

	@Autowired
	private MarketService marketService;

	@Autowired
	private TradeService tradeService;

	@Test
	@DisplayName("Check account service rate")
	public void checkAccountServiceRate() throws InterruptedException {
		TimeUnit.SECONDS.sleep(20);

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

		// One call must have been made.
		TimeUnit.SECONDS.sleep(9);
		assertEquals(1, numberOfCalls.intValue());

		// Second call should have been made.
		TimeUnit.SECONDS.sleep(21);
		assertEquals(2, numberOfCalls.intValue());
	}

	@Test
	@DisplayName("Check market service rate")
	public void checkMarketServiceRate() throws InterruptedException {
		AtomicInteger numberOfCalls = new AtomicInteger(0);

		// Executing service calls in parallel.
		ExecutorService executor = Executors.newFixedThreadPool(2);
		// Call number 1.
		executor.submit(() -> {
			marketService.getTicker(ETH_BTC);
			numberOfCalls.incrementAndGet();
		});
		// Call number 2.
		executor.submit(() -> {
			marketService.getTicker(ETH_BTC);
			numberOfCalls.incrementAndGet();
		});

		// One call must have been made.
		TimeUnit.SECONDS.sleep(14);
		assertEquals(1, numberOfCalls.intValue());

		// Second call should have been made.
		TimeUnit.SECONDS.sleep(31);
		assertEquals(2, numberOfCalls.intValue());
	}

	@Test
	@DisplayName("Check trade service rate")
	public void checkTradeServiceRate() throws InterruptedException {
		TimeUnit.SECONDS.sleep(25);
		AtomicInteger numberOfCalls = new AtomicInteger(0);

		// Executing service calls in parallel.
		ExecutorService executor = Executors.newFixedThreadPool(2);
		// Call number 1.
		executor.submit(() -> {
			tradeService.getOrders();
			numberOfCalls.incrementAndGet();
		});
		// Call number 2.
		executor.submit(() -> {
			tradeService.getOrders();
			numberOfCalls.incrementAndGet();
		});

		// One call must have been made.
		TimeUnit.SECONDS.sleep(19);
		assertEquals(1, numberOfCalls.intValue());

		// Second call should have been made.
		TimeUnit.SECONDS.sleep(41);
		assertEquals(2, numberOfCalls.intValue());
	}

}
