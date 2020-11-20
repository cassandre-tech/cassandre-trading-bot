package tech.cassandre.trading.bot.tmp.configuration.parameters.exchange;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.PARAMETER_EXCHANGE_SECRET;

@DisplayName("Exchange parameters - Invalid credentials")
@Configuration({
		@Property(key = PARAMETER_EXCHANGE_SECRET, value = "none")
})
@DirtiesContext(classMode = AFTER_CLASS)
@Disabled
public class InvalidCredentialsTest {

	@Test
	@Tag("notReviewed")
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
