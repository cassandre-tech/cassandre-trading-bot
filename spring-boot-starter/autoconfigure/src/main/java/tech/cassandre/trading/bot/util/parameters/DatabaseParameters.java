package tech.cassandre.trading.bot.util.parameters;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Database parameters from application.properties.
 */
@Validated
@ConfigurationProperties(prefix = "cassandre.trading.bot.database")
public class DatabaseParameters {

    /** Table prefix parameter. */
    public static final String PARAMETER_DATABASE_TABLE_PREFIX = "cassandre.trading.bot.database.table-prefix";

    /** Table prefix. */
    private String tablePrefix;

    /** Datasource configuration. */
    @Valid
    private Datasource datasource = new Datasource();

    /** Datasource configuration. */
    @Validated
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
     * Getter tablePrefix.
     *
     * @return tablePrefix
     */
    public String getTablePrefix() {
        return tablePrefix;
    }

    /**
     * Setter tablePrefix.
     *
     * @param newTablePrefix the tablePrefix to set
     */
    public void setTablePrefix(final String newTablePrefix) {
        tablePrefix = newTablePrefix;
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

    @Override
    public final String toString() {
        return "DatabaseParameters{"
                + " tablePrefix='" + tablePrefix + '\''
                + ", datasource=" + datasource
                + '}';
    }

}
