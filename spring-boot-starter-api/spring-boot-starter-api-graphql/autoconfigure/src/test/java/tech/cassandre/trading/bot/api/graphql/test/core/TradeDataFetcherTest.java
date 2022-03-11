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
import tech.cassandre.trading.bot.api.graphql.data.TradeDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;
import tech.cassandre.trading.bot.api.graphql.test.util.base.BaseDataFetcherTest;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static graphql.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Trade data fetcher test.
 */
@ActiveProfiles("schedule-disabled")
@DisplayName("Trade data fetcher test")
@SpringBootTest(classes = {DgsAutoConfiguration.class, CassandreTradingBot.class, DgsAutoConfiguration.class, TradeDataFetcher.class})
@TestPropertySource(properties = {"spring.liquibase.change-log = classpath:db/test/core/complete-database.yaml"})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class TradeDataFetcherTest extends BaseDataFetcherTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    @DisplayName("Get all trades")
    void getAllTrades() {
        List<String> ids = dgsQueryExecutor.executeAndExtractJsonPath(
                " { trades { tradeId }}",
                "data.trades[*].tradeId");
        assertTrue(ids.contains("60e447fc2e113d2923b995f6"));   // Real trade.
        assertFalse(ids.contains("60e47fc259_NOT_EXISTING"));   // Invented trade.
        assertEquals(416, ids.size());
    }

    @Test
    @DisplayName("Get trade by uid")
    void getTradeById() {
        Map<String, Object> result = dgsQueryExecutor.executeAndExtractJsonPath(
                " { trade(uid: 24) {" +
                        "uid " +
                        "tradeId " +
                        "type " +
                        "orderId " +
                        "order {uid orderId}" +
                        "currencyPair {baseCurrency{code} quoteCurrency{code}}" +
                        "amount {value currency{code}}" +
                        "price {value currency{code}}" +
                        "fee {value currency{code}}" +
                        "timestamp" +
                        "} }",
                "data.trade");
        assertEquals(24, result.get("uid"));
        assertEquals("60df231c2e113d2923052d18", result.get("tradeId"));
        assertEquals("ASK", result.get("type"));
        assertEquals("60df231c38ec01000687554e", result.get("orderId"));
        Map<String, String> order = (Map<String, String>) result.get("order");
        assertEquals(19, order.get("uid"));
        assertEquals("60df231c38ec01000687554e", order.get("orderId"));
        assertEquals(BTC_USDT, getCurrencyPairValue(result.get("currencyPair")));
        final CurrencyAmountDTO amount = getCurrencyAmountValue(result.get("amount"));
        assertEquals(0, new BigDecimal("0.001").compareTo(amount.getValue()));
        assertEquals(BTC, amount.getCurrency());
        final CurrencyAmountDTO price = getCurrencyAmountValue(result.get("price"));
        assertEquals(0, new BigDecimal("33591.90000000").compareTo(price.getValue()));
        assertEquals(USDT, price.getCurrency());
        final CurrencyAmountDTO fee = getCurrencyAmountValue(result.get("fee"));
        assertEquals(0, new BigDecimal("0.03359190").compareTo(fee.getValue()));
        assertEquals(USDT, fee.getCurrency());
        assertTrue(result.get("timestamp").toString().contains("2021-07-02T16:30:53"));
    }

    @Test
    @DisplayName("Get trade by tradeId")
    void getTradeByTradeId() {
        Map<String, Object> result = dgsQueryExecutor.executeAndExtractJsonPath(
                " { tradeByTradeId(tradeId: \"60df231c2e113d2923052d18\") {" +
                        "uid " +
                        "} }",
                "data.tradeByTradeId");
        assertEquals(24, result.get("uid"));
    }

}
