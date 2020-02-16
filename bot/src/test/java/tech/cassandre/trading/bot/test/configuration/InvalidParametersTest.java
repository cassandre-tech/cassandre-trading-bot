package tech.cassandre.trading.bot.test.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.SpringApplication;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.BaseTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Invalid application parameters tests.
 */
@DisplayName("Invalid application parameters tests")
public class InvalidParametersTest extends BaseTest {

	@BeforeEach
	void setUp() {
		System.setProperty("cassandre.trading.bot.exchange.name", "kucoin");
		System.setProperty("cassandre.trading.bot.exchange.sandbox", "true");
		System.setProperty("cassandre.trading.bot.exchange.username", "cassandre.crypto.bot@gmail.com");
		System.setProperty("cassandre.trading.bot.exchange.passphrase", "cassandre");
		System.setProperty("cassandre.trading.bot.exchange.key", "5df8eea30092f40009cb3c6a");
		System.setProperty("cassandre.trading.bot.exchange.secret", "WRONG_SECRET");
		System.setProperty("cassandre.trading.bot.exchange.rates.account", "100");
		System.setProperty("cassandre.trading.bot.exchange.rates.ticker", "101");
		System.setProperty("cassandre.trading.bot.exchange.rates.order", "102");
	}

	@Test
	@DisplayName("All parameters missing")
	public void allParametersMissing() {
		try {
			System.setProperty("cassandre.trading.bot.exchange.name", "");
			System.setProperty("cassandre.trading.bot.exchange.sandbox", "");
			System.setProperty("cassandre.trading.bot.exchange.username", "");
			System.setProperty("cassandre.trading.bot.exchange.passphrase", "");
			System.setProperty("cassandre.trading.bot.exchange.key", "");
			System.setProperty("cassandre.trading.bot.exchange.secret", "");
			System.setProperty("cassandre.trading.bot.exchange.rates.account", "");
			System.setProperty("cassandre.trading.bot.exchange.rates.ticker", "");
			System.setProperty("cassandre.trading.bot.exchange.rates.order", "");
			SpringApplication application = new SpringApplication(CassandreTradingBot.class);
			application.run();
		} catch (Exception e) {
			assertTrue(e instanceof UnsatisfiedDependencyException);
			final String message = getExceptionMessage(e);
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'name'"));
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'sandbox'"));
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'username'"));
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'passphrase'"));
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'key'"));
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'secret'"));
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'rates.account'"));
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'rates.ticker'"));
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'rates.order'"));
		}
	}

	@Test
	@DisplayName("One parameter missing (cassandre.trading.bot.exchange.name)")
	public void invalidRates() {
		try {
			System.setProperty("cassandre.trading.bot.exchange.rates.account", "-1");
			System.setProperty("cassandre.trading.bot.exchange.rates.ticker", "-1");
			System.setProperty("cassandre.trading.bot.exchange.rates.order", "-1");
			SpringApplication application = new SpringApplication(CassandreTradingBot.class);
			application.run();
		} catch (Exception e) {
			assertTrue(e instanceof UnsatisfiedDependencyException);
			final String message = getExceptionMessage(e);
			System.out.println(message);
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'name'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'sandbox'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'username'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'passphrase'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'key'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'secret'"));
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange.rates' on field 'account'"));
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange.rates' on field 'ticker'"));
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange.rates' on field 'order'"));
		}
	}

	@Test
	@DisplayName("One parameter missing (account / ticker / order)")
	public void parameterNameMissing() {
		try {
			System.setProperty("cassandre.trading.bot.exchange.name", "");
			SpringApplication application = new SpringApplication(CassandreTradingBot.class);
			application.run();
		} catch (Exception e) {
			assertTrue(e instanceof UnsatisfiedDependencyException);
			final String message = getExceptionMessage(e);
			assertTrue(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'name'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'sandbox'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'username'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'passphrase'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'key'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'secret'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'rates.account'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'rates.ticker'"));
			assertFalse(message.contains("Field error in object 'cassandre.trading.bot.exchange' on field 'rates.order'"));
		}
	}

	/**
	 * Returns exception message.
	 *
	 * @param e exception
	 * @return exception message
	 */
	private String getExceptionMessage(final Exception e) {
		return e.getCause().getCause().getCause().getMessage();
	}

}
