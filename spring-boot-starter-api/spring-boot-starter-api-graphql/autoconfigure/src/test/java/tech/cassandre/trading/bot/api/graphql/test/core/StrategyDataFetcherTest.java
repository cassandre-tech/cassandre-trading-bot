package tech.cassandre.trading.bot.api.graphql.test.core;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.api.graphql.data.StrategyDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

/**
 * Strategy data fetcher test.
 */
@ActiveProfiles("schedule-disabled")
@DisplayName("Strategy data fetcher test")
@SpringBootTest(classes = {CassandreTradingBot.class, DgsAutoConfiguration.class, StrategyDataFetcher.class})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class StrategyDataFetcherTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    @DisplayName("Get all strategies")
    void getStrategies() {
        List<String> ids = dgsQueryExecutor.executeAndExtractJsonPath(
                " { strategies { strategyId }}",
                "data.strategies[*].strategyId");
        assertThat(ids).contains("001");
        assertThat(ids).contains("002");
    }

    @Test
    @DisplayName("Get strategy by its strategy Id")
    void getStrategyByStrategyId() {
        List<String> titles = dgsQueryExecutor.executeAndExtractJsonPath(
                " { strategy { strategyId name }}",
                "data.strategy[*].name");

        //assertThat(titles).contains("Ozark");
    }

}
