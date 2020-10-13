package tech.cassandre.trading.bot.util.parameters;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

/**
 * Database parameters from application.properties.
 */
@Validated
@ConfigurationProperties(prefix = "cassandre.trading.bot.database")
public class DatabaseParameters {

    /** Datasource configuration. */
    @Valid
    private static Datasource datasource = new Datasource();

    /** Datasource configuration. */
    @Validated
    @ConfigurationProperties(prefix = "cassandre.trading.bot.database.datasource")
    public static class Datasource {

        /** Backup enabled parameter. */
        public static final String PARAMETER_DATABASE_DATASOURCE_DRIVER_CLASS_NAME = "cassandre.trading.bot.database.datasource.driver-class-name";

        /** Backup enabled parameter. */
        public static final String PARAMETER_DATABASE_DATASOURCE_URL = "cassandre.trading.bot.database.datasource.url";

        /** Backup enabled parameter. */
        public static final String PARAMETER_DATABASE_DATASOURCE_USERNAME = "cassandre.trading.bot.database.datasource.username";

        /** Backup enabled parameter. */
        public static final String PARAMETER_DATABASE_DATASOURCE_PASSWORD = "cassandre.trading.bot.database.datasource.password";

        /** Driver class name. */
        @NotEmpty(message = "Database driver class name must be set")
        private String driverClassName;

        /** URL. */
        @NotEmpty(message = "Database url must be set")
        private String url;

        /** Username. */
        @NotEmpty(message = "Database username must be set")
        private String username;

        /** Password. */
        private String password;

        /**
         * Getter driverClassName.
         *
         * @return driverClassName
         */
        public String getDriverClassName() {
            return driverClassName;
        }

        /**
         * Setter driverClassName.
         *
         * @param newDriverClassName the driverClassName to set
         */
        public void setDriverClassName(final String newDriverClassName) {
            driverClassName = newDriverClassName;
        }

        /**
         * Getter url.
         *
         * @return url
         */
        public String getUrl() {
            return url;
        }

        /**
         * Setter url.
         *
         * @param newUrl the url to set
         */
        public void setUrl(final String newUrl) {
            url = newUrl;
        }

        /**
         * Getter username.
         *
         * @return username
         */
        public String getUsername() {
            return username;
        }

        /**
         * Setter username.
         *
         * @param newUsername the username to set
         */
        public void setUsername(final String newUsername) {
            username = newUsername;
        }

        /**
         * Getter password.
         *
         * @return password
         */
        public String getPassword() {
            return password;
        }

        /**
         * Setter password.
         *
         * @param newPassword the password to set
         */
        public void setPassword(final String newPassword) {
            password = newPassword;
        }

        @Override
        public final String toString() {
            return "Datasource{"
                    + " driverClassName='" + driverClassName + '\''
                    + ", url='" + url + '\''
                    + ", username='" + username + '\''
                    + ", password='" + password + '\''
                    + '}';
        }

    }

    /**
     * Getter datasource.
     *
     * @return datasource
     */
    public Datasource getDatasource() {
        return datasource;
    }

    /**
     * Setter datasource.
     *
     * @param newDatasource the datasource to set
     */
    public void setDatasource(final Datasource newDatasource) {
        datasource = newDatasource;
    }

}
