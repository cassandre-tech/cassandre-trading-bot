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
import tech.cassandre.trading.bot.api.graphql.client.generated.client.StrategiesGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.StrategiesProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.StrategyByStrategyIdGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.StrategyByStrategyIdProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.StrategyGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.StrategyProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.Strategy;
import tech.cassandre.trading.bot.api.graphql.data.StrategyDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

/**
 * Strategy data fetcher test.
 */
@ActiveProfiles("schedule-disabled")
@DisplayName("Strategy data fetcher test")
@SpringBootTest(classes = {DgsAutoConfiguration.class, CassandreTradingBot.class, DgsAutoConfiguration.class, StrategyDataFetcher.class})
@TestPropertySource(properties = {"spring.liquibase.change-log = classpath:db/test/core/complete-database.yaml"})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class StrategyDataFetcherTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    @DisplayName("strategies: [Strategy]")
    void strategies() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new StrategiesGraphQLQuery.Builder().build(),
                new StrategiesProjectionRoot().uid());
        // Query execution.
        List<Strategy> strategies = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.Strategies + "[*]",
                new TypeRef<>() {
                });
        // Tests.
        assertEquals(2, strategies.size());
    }

    @Test
    @DisplayName("strategy(uid: Int): Strategy")
    void strategy() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new StrategyGraphQLQuery.Builder().uid(2).build(),
                new StrategyProjectionRoot().uid()
                        .strategyId()
                        .name());
        // Query execution.
        Strategy strategy = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.Strategy,
                new TypeRef<>() {
                });
        // Tests.
        assertNotNull(strategy);
        assertEquals(2, strategy.getUid());
        assertEquals("002", strategy.getStrategyId());
        assertEquals("Uniswap", strategy.getName());
    }

    @Test
    @DisplayName("strategyByStrategyId(strategyId: String): Strategy")
    void strategyByStrategyId() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new StrategyByStrategyIdGraphQLQuery.Builder().strategyId("002").build(),
                new StrategyByStrategyIdProjectionRoot().uid()
                        .strategyId()
                        .name());
        // Query execution.
        Strategy strategy = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.StrategyByStrategyId,
                new TypeRef<>() {
                });
        // Tests.
        assertNotNull(strategy);
        assertEquals(2, strategy.getUid());
        assertEquals("002", strategy.getStrategyId());
        assertEquals("Uniswap", strategy.getName());
    }

}
