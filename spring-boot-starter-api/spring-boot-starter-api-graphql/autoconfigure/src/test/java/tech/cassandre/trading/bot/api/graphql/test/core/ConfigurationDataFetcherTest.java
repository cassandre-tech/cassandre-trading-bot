package tech.cassandre.trading.bot.api.graphql.test.core;

import com.jayway.jsonpath.TypeRef;
import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.api.graphql.client.generated.DgsConstants;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.ConfigurationGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.ConfigurationProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.Configuration;
import tech.cassandre.trading.bot.api.graphql.data.ConfigurationDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

/**
 * Configuration data fetcher test.
 */
@ActiveProfiles("schedule-disabled")
@DisplayName("Configuration data fetcher test")
@SpringBootTest(classes = {DgsAutoConfiguration.class, CassandreTradingBot.class, DgsAutoConfiguration.class, ConfigurationDataFetcher.class})
@TestPropertySource(properties = {"spring.liquibase.change-log = classpath:db/test/core/complete-database.yaml"})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class ConfigurationDataFetcherTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    @DisplayName("configuration: Configuration")
    void configuration() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new ConfigurationGraphQLQuery.Builder().build(),
                new ConfigurationProjectionRoot().apiVersion());
        // Query execution.
        Configuration configuration = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.Configuration,
                new TypeRef<>() {
                });
        // Tests.
        assertNotNull(configuration);
        assertEquals("1.1", configuration.getApiVersion());
    }

}
