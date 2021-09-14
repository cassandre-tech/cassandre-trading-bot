package tech.cassandre.trading.bot.api.graphql.test.core;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.api.graphql.data.AccountDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;
import tech.cassandre.trading.bot.api.graphql.test.util.base.BaseDataFetcherTest;
import tech.cassandre.trading.bot.api.graphql.test.util.mock.BaseMock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

/**
 * Account data fetcher test.
 */
@ActiveProfiles("schedule-disabled")
@DisplayName("Account data fetcher test")
@SpringBootTest(classes = {CassandreTradingBot.class, DgsAutoConfiguration.class, AccountDataFetcher.class})
@TestPropertySource(properties = {"spring.liquibase.change-log = classpath:db/test/core/complete-database.yaml"})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(BaseMock.class)
public class AccountDataFetcherTest extends BaseDataFetcherTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    @DisplayName("Get all accounts")
    public void getAllAccounts() {
        List<String> ids = dgsQueryExecutor.executeAndExtractJsonPath(
                " { accounts { accountId }}",
                "data.accounts[*].accountId");
        assertTrue(ids.contains("trade"));
        assertFalse(ids.contains("002"));
    }

    @Test
    @DisplayName("Get account by accountId")
    void getAccountByAccountId() {
        Map<String, Object> result = dgsQueryExecutor.executeAndExtractJsonPath(
                " { accountByAccountId(accountId: \"trade\") {" +
                        "name " +
                        "balances {" +
                        " currency{code} " +
                        " total " +
                        " available " +
                        " frozen " +
                        " loaned " +
                        " borrowed " +
                        " withdrawing " +
                        " depositing " +
                        " } " +
                        "} }",
                "data.accountByAccountId");
        assertEquals("trade account name", result.get("name"));
        JSONArray balances = (JSONArray) result.get("balances");
        final Object btc = balances.get(0);
        Map<String, Object> btcValue = (Map<String, Object>) btc;
        assertEquals("BTC", ((Map<String, String>) btcValue.get("currency")).get("code"));
        assertEquals(0, new BigDecimal("1").compareTo(new BigDecimal(String.valueOf(btcValue.get("total")))));
        assertEquals(0, new BigDecimal("2").compareTo(new BigDecimal(String.valueOf(btcValue.get("available")))));
        assertEquals(0, new BigDecimal("3").compareTo(new BigDecimal(String.valueOf(btcValue.get("frozen")))));
        assertEquals(0, new BigDecimal("4").compareTo(new BigDecimal(String.valueOf(btcValue.get("borrowed")))));
        assertEquals(0, new BigDecimal("5").compareTo(new BigDecimal(String.valueOf(btcValue.get("loaned")))));
        assertEquals(0, new BigDecimal("6").compareTo(new BigDecimal(String.valueOf(btcValue.get("withdrawing")))));
        assertEquals(0, new BigDecimal("7").compareTo(new BigDecimal(String.valueOf(btcValue.get("depositing")))));
    }

}
