package tech.cassandre.trading.bot.integration.gemini;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.CassandreTradingBot;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@DisplayName("Kucoin - Invalid credentials")
@TestPropertySource(properties = {
		"cassandre.trading.bot.exchange.name=${GEMINI_NAME}",
		"cassandre.trading.bot.exchange.modes.sandbox=true",
		"cassandre.trading.bot.exchange.modes.dry=false",
		"cassandre.trading.bot.exchange.username=${GEMINI_USERNAME}",
		"cassandre.trading.bot.exchange.passphrase=${GEMINI_PASSPHRASE}",
		"cassandre.trading.bot.exchange.key=${GEMINI_KEY}",
		"cassandre.trading.bot.exchange.secret=WRONG_SECRET",
		"cassandre.trading.bot.exchange.rates.account=100",
		"cassandre.trading.bot.exchange.rates.ticker=101",
		"cassandre.trading.bot.exchange.rates.trade=102",
		"cassandre.trading.bot.database.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver",
		"cassandre.trading.bot.database.datasource.url=jdbc:hsqldb:mem:cassandre-database;shutdown=true",
		"cassandre.trading.bot.database.datasource.username=sa",
		"cassandre.trading.bot.database.datasource.password=",
		"testableStrategy.enabled=true",
		"invalidStrategy.enabled=false"
})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class InvalidCredentialsTest {

	@Test
	@CaseId(13)
	@DisplayName("Check error messages")
	public void checkErrorMessages() {
		try {
			SpringApplication application = new SpringApplication(CassandreTradingBot.class);
			application.run();
			fail("Exception not raised");
		} catch (Exception e) {
			assertTrue(e.getMessage().contains("Invalid credentials for kucoin"));
		}
	}

}
