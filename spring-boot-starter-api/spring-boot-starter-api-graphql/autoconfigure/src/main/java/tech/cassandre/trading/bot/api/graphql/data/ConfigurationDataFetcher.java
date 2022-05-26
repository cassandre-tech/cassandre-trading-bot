package tech.cassandre.trading.bot.api.graphql.data;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import tech.cassandre.trading.bot.api.graphql.util.base.BaseDataFetcher;
import tech.cassandre.trading.bot.api.graphql.util.graphql.Configuration;

/**
 * Configuration data fetcher.
 */
@DgsComponent
public class ConfigurationDataFetcher extends BaseDataFetcher {

    /** API Version. */
    private static final String API_VERSION = "1.1";

    /**
     * Returns configuration.
     *
     * @return configuration
     */
    @DgsQuery
    public final Configuration configuration() {
        return Configuration.builder()
                .apiVersion(API_VERSION)    // GraphQL API version.
                .build();
    }

}
