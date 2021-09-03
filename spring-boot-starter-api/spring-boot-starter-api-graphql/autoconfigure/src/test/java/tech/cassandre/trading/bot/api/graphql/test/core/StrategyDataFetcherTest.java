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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

/**
 * Strategy data fetcher test.
 */
@ActiveProfiles("schedule-disabled")
@DisplayName("Strategy data fetcher test")
@SpringBootTest(classes = {CassandreTradingBot.class, DgsAutoConfiguration.class, StrategyDataFetcher.class})
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
    @DisplayName("Get strategy by Id")
    void getStrategyById() {
        String strategyName = dgsQueryExecutor.executeAndExtractJsonPath(
                " { strategy(id: 2) { name } }",
                "data.strategy.name");
        assertEquals(strategyName, "Uniswap");
    }

    @Test
    @DisplayName("Get strategy by strategy Id")
    void getStrategyByStrategyId() {
        String strategyName = dgsQueryExecutor.executeAndExtractJsonPath(
                " { strategyByStrategyId(strategyId: \"002\") { name } }",
                "data.strategyByStrategyId.name");
        assertEquals(strategyName, "Uniswap");
    }

}
