package tech.cassandre.trading.bot.test.configuration.strategy;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Strategy configuration tests.
 */
@DisplayName("Strategy configuration tests")
public class StrategyConfigurationTest {

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
	}

	@Test
	@DisplayName("Valid")
	public void validStrategy() {
		try {
			System.setProperty("testableStrategy.enabled", "true");
			System.setProperty("invalidStrategy.enabled", "false");
			SpringApplication application = new SpringApplication(CassandreTradingBot.class);
			application.run();
		} catch (Exception e) {
			fail("Exception was raised for valid strategy" + e);
		}
	}

	@Test
	@DisplayName("No strategy found")
	public void noStrategyFound() {
		try {
			System.setProperty("testableStrategy.enabled", "false");
			System.setProperty("invalidStrategy.enabled", "false");
			SpringApplication application = new SpringApplication(CassandreTradingBot.class);
			application.run();
			fail("Exception was not raised");
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof ConfigurationException);
			assertTrue(e.getCause().getMessage().contains("No strategy found"));
		}
	}

	@Test
	@DisplayName("Two strategies found")
	public void twoStrategyFound() {
		try {
			System.setProperty("testableStrategy.enabled", "true");
			System.setProperty("invalidStrategy.enabled", "true");
			SpringApplication application = new SpringApplication(CassandreTradingBot.class);
			application.run();
			fail("Exception was not raised");
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof ConfigurationException);
			assertTrue(e.getCause().getMessage().contains("Several strategies were found"));
		}
	}

	@Test
	@DisplayName("Invalid strategy found")
	public void invalidStrategyFound() {
		try {
			System.setProperty("testableStrategy.enabled", "false");
			System.setProperty("invalidStrategy.enabled", "true");
			SpringApplication application = new SpringApplication(CassandreTradingBot.class);
			application.run();
			fail("Exception was not raised");
		} catch (Exception e) {
			assertTrue(e.getCause() instanceof ConfigurationException);
			assertTrue(e.getCause().getMessage().contains("Your strategy doesn't extends CassandreStrategy"));
		}
	}

}
