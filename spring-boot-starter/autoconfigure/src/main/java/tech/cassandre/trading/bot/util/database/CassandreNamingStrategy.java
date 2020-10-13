package tech.cassandre.trading.bot.util.database;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Cassandre naming strategy.
 */
public class CassandreNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    /** Table prefix (default none). */
    private String tablePrefix = "";

    /**
     * Constructor.
     *
     * @param newTablePrefix table prefix
     */
    public CassandreNamingStrategy(final String newTablePrefix) {
        if (newTablePrefix != null) {
            this.tablePrefix = newTablePrefix;
        }
    }

    @Override
    public final Identifier toPhysicalTableName(final Identifier name, final JdbcEnvironment context) {
        return new Identifier(tablePrefix.concat(name.getText()), name.isQuoted());
    }

}
