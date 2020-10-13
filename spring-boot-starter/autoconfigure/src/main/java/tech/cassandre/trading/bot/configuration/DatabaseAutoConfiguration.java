package tech.cassandre.trading.bot.configuration;

import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.cassandre.trading.bot.util.base.BaseConfiguration;
import tech.cassandre.trading.bot.util.database.CassandreNamingStrategy;
import tech.cassandre.trading.bot.util.exception.ConfigurationException;
import tech.cassandre.trading.bot.util.parameters.DatabaseParameters;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Database autoconfiguration.
 */
@Configuration
@EnableConfigurationProperties({DatabaseParameters.class,
        DatabaseParameters.Datasource.class})
public class DatabaseAutoConfiguration extends BaseConfiguration {

    /** Database parameters. */
    private DatabaseParameters databaseParameters;

    /**
     * Constructor.
     *
     * @param newDatabaseParameters database parameters.
     */
    public DatabaseAutoConfiguration(final DatabaseParameters newDatabaseParameters) {
        this.databaseParameters = newDatabaseParameters;
    }

    /**
     * Cassandre datasource.
     *
     * @return datasource
     */
    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName(databaseParameters.getDatasource().getDriverClassName());
        dataSourceBuilder.url(databaseParameters.getDatasource().getUrl());
        dataSourceBuilder.username(databaseParameters.getDatasource().getUsername());
        dataSourceBuilder.password(databaseParameters.getDatasource().getPassword());
        try {
            // Trying to connect to see it if works.
            final Connection connection = dataSourceBuilder.build().getConnection();
            connection.close();
        } catch (Exception exception) {
            throw new ConfigurationException("Impossible to connect to database : " + exception.getMessage(),
                    "Check your database configuration : " + databaseParameters.getDatasource().getDriverClassName()
                            + " / " + databaseParameters.getDatasource().getUrl()
                            + " / " + databaseParameters.getDatasource().getUsername());
        }
        return dataSourceBuilder.build();
    }

    @Bean
    @SuppressWarnings("checkstyle:DesignForExtension")
    public PhysicalNamingStrategy physical() {
        return new CassandreNamingStrategy(databaseParameters.getTablePrefix());
    }

}
