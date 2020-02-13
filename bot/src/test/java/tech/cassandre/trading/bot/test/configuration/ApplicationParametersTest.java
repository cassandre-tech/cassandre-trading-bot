package tech.cassandre.trading.bot.test.configuration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.util.parameters.ExchangeParameters;
import tech.cassandre.trading.bot.util.parameters.RateParameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Application parameters test.
 */
@SpringBootTest
@DisplayName("Application parameters test")
public class ApplicationParametersTest extends BaseTest {

	@Test
	@DisplayName("Exchange parameters")
	void exchangeParameters() {
		ExchangeParameters exchange = getContext().getBean(ExchangeParameters.class);
		assertEquals("kucoin", exchange.getName());
		assertTrue(exchange.isSandbox());
		assertEquals("cassandre.crypto.bot@gmail.com", exchange.getUsername());
		assertEquals("cassandre", exchange.getPassphrase());
		assertEquals("5df8eea30092f40009cb3c6a", exchange.getKey());
		assertEquals("WRONG_SECRET", exchange.getSecret());
	}

	@Test
	@DisplayName("Rate parameters")
	void rateParameters() {
		final int expectedAccountRate = 100;
		final int expectedTickerRate = 101;
		final int expectedOrderRate = 102;
		// Testing values.
		RateParameters rateParameters = getContext().getBean(RateParameters.class);
		assertEquals(expectedAccountRate, rateParameters.getAccount());
		assertEquals(expectedTickerRate, rateParameters.getTicker());
		assertEquals(expectedOrderRate, rateParameters.getOrder());
	}

}
