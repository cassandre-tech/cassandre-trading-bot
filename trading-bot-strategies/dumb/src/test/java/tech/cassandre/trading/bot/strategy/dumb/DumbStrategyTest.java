package tech.cassandre.trading.bot.strategy.dumb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.cassandre.trading.bot.dto.market.TickerDTO;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.with;
import static org.awaitility.pollinterval.FibonacciPollInterval.fibonacci;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Dumb strategy test.
 */
@SpringBootTest
@DisplayName("Dumb strategy test")
public class DumbStrategyTest {

	/** How much we should wait for tests to last. */
	protected static final long MAXIMUM_RESPONSE_TIME_IN_SECONDS = 60;

	/** Dumb strategy. */
	@Autowired
	DumbStrategy strategy;

	/**
	 * Check that the strategy receive data.
	 */
	@Test
	@DisplayName("Check data retrieved")
	public void receivedData() {
		// Waiting to see if the strategy received the accounts update (we have two accounts).
		with().pollInterval(fibonacci(SECONDS)).await()
				.atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
				.untilAsserted(() -> assertEquals(strategy.getAccounts().size(), 2));

		// Waiting to see if the strategy received a ticker.
		with().pollInterval(fibonacci(SECONDS)).await()
				.atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
				.untilAsserted(() -> assertNotNull(strategy.getLastTickerReceived()));

		// We check that we received more than one ticker.
		TickerDTO ticker = strategy.getLastTickerReceived();
		with().pollInterval(fibonacci(SECONDS)).await()
				.atMost(MAXIMUM_RESPONSE_TIME_IN_SECONDS, SECONDS)
				.untilAsserted(() -> assertNotEquals(strategy.getLastTickerReceived(), ticker));
	}

	@Test
	@DisplayName("Trade service available")
	public void tradeServiceAvailable() {
		assertNotNull(strategy.getTradeService());
	}

}
