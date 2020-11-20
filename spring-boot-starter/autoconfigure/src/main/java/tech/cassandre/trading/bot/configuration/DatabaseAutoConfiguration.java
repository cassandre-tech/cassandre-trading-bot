package tech.cassandre.trading.bot.configuration;

import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tech.cassandre.trading.bot.util.base.BaseConfiguration;
import tech.cassandre.trading.bot.util.database.CassandreNamingStrategy;
import tech.cassandre.trading.bot.util.parameters.DatabaseParameters;

/**
 * Database autoconfiguration.
 */
@Configuration
@EntityScan(basePackages = "tech.cassandre.trading.bot.domain")
@EnableJpaRepositories(basePackages = "tech.cassandre.trading.bot.repository")
@EnableConfigurationProperties(DatabaseParameters.class)
public class DatabaseAutoConfiguration extends BaseConfiguration {

    /** Precision. */
    public static final int PRECISION = 16;

    /** Scale. */
    public static final int SCALE = 8;

    /** Database parameters. */
    private final DatabaseParameters databaseParameters;

    /**
     * Constructor.
     *
     * @param newDatabaseParameters database parameters.
     */
    public DatabaseAutoConfiguration(final DatabaseParameters newDatabaseParameters) {
        this.databaseParameters = newDatabaseParameters;
    }

    /**
     * Gives to Hiraki the configuration of the default datasource.
     *
     * @return datasource configuration
     */
    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties p = new DataSourceProperties();
        p.setDriverClassName(databaseParameters.getDatasource().getDriverClassName());
        p.setUrl(databaseParameters.getDatasource().getUrl());
        p.setUsername(databaseParameters.getDatasource().getUsername());
        p.setPassword(databaseParameters.getDatasource().getPassword());
        return p;
    }

    /**
     * Set physical naming strategy.
     * Adds a prefix to table name via cassandre.trading.bot.database.table-prefix parameter.
     * @return physical naming strategy
     */
    @Bean
    @SuppressWarnings("checkstyle:DesignForExtension")
    public PhysicalNamingStrategy physical() {
        return new CassandreNamingStrategy(databaseParameters.getTablePrefix());
    }

}
