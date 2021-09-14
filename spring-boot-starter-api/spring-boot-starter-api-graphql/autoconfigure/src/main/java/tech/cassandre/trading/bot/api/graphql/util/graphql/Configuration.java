package tech.cassandre.trading.bot.api.graphql.util.graphql;

import lombok.Builder;

/**
 * Configuration returned throw API to clients.
 */
@Builder
public class Configuration {

    /** Cassandre graphql api version. */
    private String apiVersion;

}
