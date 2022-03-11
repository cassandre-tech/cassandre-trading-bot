package tech.cassandre.trading.bot.api.graphql.test.core;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.api.graphql.data.StrategyDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    @DisplayName("Get all strategies")
    void getAllStrategies() {
        List<String> ids = dgsQueryExecutor.executeAndExtractJsonPath(
                " { strategies { strategyId }}",
                "data.strategies[*].strategyId");
        assertTrue(ids.contains("001"));
        assertTrue(ids.contains("002"));
    }

    @Test
    @DisplayName("Get strategy by uid")
    void getStrategyById() {
        Map<String, Object> result = dgsQueryExecutor.executeAndExtractJsonPath(
                " { strategy(uid: 2) { uid strategyId type name } }",
                "data.strategy");
        assertEquals(2, result.get("uid"));
        assertEquals("002", result.get("strategyId"));
        assertEquals("BASIC_TA4J_STRATEGY", result.get("type"));
        assertEquals("Uniswap", result.get("name"));
    }

    @Test
    @DisplayName("Get strategy by strategy Id")
    void getStrategyByStrategyId() {
        Map<String, Object> result = dgsQueryExecutor.executeAndExtractJsonPath(
                " { strategyByStrategyId(strategyId: \"002\") { uid strategyId type name } }",
                "data.strategyByStrategyId");
        assertEquals(2, result.get("uid"));
        assertEquals("002", result.get("strategyId"));
        assertEquals("BASIC_TA4J_STRATEGY", result.get("type"));
        assertEquals("Uniswap", result.get("name"));
    }

}
