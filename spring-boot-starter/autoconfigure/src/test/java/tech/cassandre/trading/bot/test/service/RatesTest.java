package tech.cassandre.trading.bot.test.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.service.UserService;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_DRY;

@SpringBootTest
@DisplayName("Services - Rates")
@ActiveProfiles("schedule-disabled")
@Configuration({
		@Property(key = PARAMETER_DRY, value = "true")
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Disabled
public class RatesTest {

	private static final long WAITING_TIME = 2900;

	@Autowired
	private UserService userService;

	@Autowired
	private MarketService marketService;

	@Autowired
	private TradeService tradeService;

	@Test
	@DisplayName("Check account service rate")
	public void checkAccountServiceRateTest() throws InterruptedException {
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
	@DisplayName("Check market service rate")
	public void checkMarketServiceRateTest() throws InterruptedException {
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
	@DisplayName("Check trade service rate")
	public void checkTradeServiceRateTest() throws InterruptedException {
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
