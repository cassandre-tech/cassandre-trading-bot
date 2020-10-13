package tech.cassandre.trading.bot.test.configuration.parameters.database;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static tech.cassandre.trading.bot.util.parameters.DatabaseParameters.Datasource.PARAMETER_DATABASE_DATASOURCE_DRIVER_CLASS_NAME;

@DisplayName("Database parameters - Invalid database parameters")
@Configuration({
		@Property(key = PARAMETER_DATABASE_DATASOURCE_DRIVER_CLASS_NAME, value = "org.JDBCDriver")
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class InvalidDatabaseParametersTest {

	@Test
	@DisplayName("Check error messages")
	public void checkErrorMessages() {
		try {
			SpringApplication application = new SpringApplication(CassandreTradingBot.class);
			application.run();
			fail("Exception not raised");
		} catch (Exception e) {
			assertTrue(e.getMessage().contains("Impossible to connect to database"));
		}
	}

}
