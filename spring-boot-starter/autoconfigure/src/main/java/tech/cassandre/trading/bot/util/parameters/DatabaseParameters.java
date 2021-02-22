package tech.cassandre.trading.bot.util.parameters;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Database parameters from application.properties.
 */
@Validated
@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "cassandre.trading.bot.database")
public class DatabaseParameters {

    /** Datasource configuration. */
    @Valid
    private Datasource datasource = new Datasource();

    /** Datasource configuration. */
    @Validated
    @Getter
    @Setter
    @ToString
    @ConfigurationProperties(prefix = "cassandre.trading.bot.database.datasource")
    public class Datasource {

        /** Backup enabled parameter. */
        public static final String PARAMETER_DATABASE_DATASOURCE_DRIVER_CLASS_NAME = "cassandre.trading.bot.database.datasource.driver-class-name";

        /** Backup enabled parameter. */
        public static final String PARAMETER_DATABASE_DATASOURCE_URL = "cassandre.trading.bot.database.datasource.url";

        /** Backup enabled parameter. */
        public static final String PARAMETER_DATABASE_DATASOURCE_USERNAME = "cassandre.trading.bot.database.datasource.username";

        /** Backup enabled parameter. */
        public static final String PARAMETER_DATABASE_DATASOURCE_PASSWORD = "cassandre.trading.bot.database.datasource.password";

        /** Driver class name. */
        @NotNull(message = "Database driver class name must be set")
        private String driverClassName;

        /** URL. */
        @NotNull(message = "Database url must be set")
        private String url;

        /** Username. */
        @NotNull(message = "Database username must be set")
        private String username;

        /** Password. */
        private String password;

    }

}
