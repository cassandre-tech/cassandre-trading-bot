package tech.cassandre.trading.bot.tmp.configuration.parameters.database;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.CassandreTradingBot;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.util.parameters.DatabaseParameters.Datasource.PARAMETER_DATABASE_DATASOURCE_DRIVER_CLASS_NAME;
import static tech.cassandre.trading.bot.util.parameters.DatabaseParameters.Datasource.PARAMETER_DATABASE_DATASOURCE_PASSWORD;
import static tech.cassandre.trading.bot.util.parameters.DatabaseParameters.Datasource.PARAMETER_DATABASE_DATASOURCE_URL;
import static tech.cassandre.trading.bot.util.parameters.DatabaseParameters.Datasource.PARAMETER_DATABASE_DATASOURCE_USERNAME;

@DisplayName("Database parameters - No configuration")
@Configuration({
        @Property(key = PARAMETER_DATABASE_DATASOURCE_DRIVER_CLASS_NAME),
        @Property(key = PARAMETER_DATABASE_DATASOURCE_URL),
        @Property(key = PARAMETER_DATABASE_DATASOURCE_USERNAME),
        @Property(key = PARAMETER_DATABASE_DATASOURCE_PASSWORD)
})
@DirtiesContext(classMode = AFTER_CLASS)
@Disabled
public class NoConfigurationTest extends BaseTest {

    @Test
    @Tag("notReviewed")
    @DisplayName("Check error messages")
    public void checkErrorMessages() {
        try {
            SpringApplication application = new SpringApplication(CassandreTradingBot.class);
            application.run();
            fail("Exception not raised");
        } catch (Exception e) {
            final String message = e.getCause().getCause().getCause().getCause().getCause().getMessage();
            assertTrue(message.contains("'datasource.username'"));
            assertTrue(message.contains("'datasource.url'"));
            assertTrue(message.contains("'datasource.driverClassName'"));
        }
    }

}
