package tech.cassandre.trading.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Simple strategy test.
 */
@SpringBootTest
@DisplayName("Simple strategy test")
public class SimpleCassandreStrategyTest {

	/** How much we should wait for tests to last. */
	protected static final long MAXIMUM_RESPONSE_TIME_IN_SECONDS = 60;

	/**
	 * Check data reception
	 */
	@Test
	@DisplayName("Check data reception")
	public void receivedData() {

	}

}
