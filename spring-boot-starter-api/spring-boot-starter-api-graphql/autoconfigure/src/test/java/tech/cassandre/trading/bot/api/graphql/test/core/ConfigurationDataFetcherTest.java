package tech.cassandre.trading.bot.api.graphql.test.core;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.api.graphql.data.ConfigurationDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

/**
 * Configuration data fetcher test.
 */
@ActiveProfiles("schedule-disabled")
@DisplayName("Version data fetcher test")
@SpringBootTest(classes = {CassandreTradingBot.class, DgsAutoConfiguration.class, ConfigurationDataFetcher.class})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class ConfigurationDataFetcherTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    @DisplayName("Get configuration")
    void getConfiguration() {
        String apiVersion = dgsQueryExecutor.executeAndExtractJsonPath(
                " { configuration { apiVersion } }",
                "data.configuration.apiVersion");
        assertEquals(apiVersion, "1.0");
    }

}
