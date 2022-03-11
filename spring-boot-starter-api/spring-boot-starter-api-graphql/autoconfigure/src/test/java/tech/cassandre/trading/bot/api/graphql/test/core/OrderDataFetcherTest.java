package tech.cassandre.trading.bot.api.graphql.test.core;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.api.graphql.data.OrderDataFetcher;
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
 * Order data fetcher test.
 */
@ActiveProfiles("schedule-disabled")
@DisplayName("Order data fetcher test")
@SpringBootTest(classes = {DgsAutoConfiguration.class, CassandreTradingBot.class, DgsAutoConfiguration.class, OrderDataFetcher.class})
@TestPropertySource(properties = {"spring.liquibase.change-log = classpath:db/test/core/complete-database.yaml"})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class OrderDataFetcherTest extends BaseDataFetcherTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    @DisplayName("Get all orders")
    void getAllOrders() {
        List<String> ids = dgsQueryExecutor.executeAndExtractJsonPath(
                " { orders { orderId }}",
                "data.orders[*].orderId");
        assertTrue(ids.contains("60ddfbc11f8b45000696de3f"));   // Real order.
        assertFalse(ids.contains("60ddfbc11f_NOT_EXISTING"));   // Invented order.
        assertEquals(345, ids.size());
    }

    @Test
    @DisplayName("Get order by uid")
    void getOrderByUid() {
        Map<String, Object> result = dgsQueryExecutor.executeAndExtractJsonPath(
                " { order(uid: 1) {" +
                        "uid " +
                        "orderId " +
                        "type " +
                        "strategy {strategyId} " +
                        "currencyPair {baseCurrency{code} quoteCurrency{code}} " +
                        "amount {value currency{code}} " +
                        "averagePrice {value currency{code}} " +
                        "limitPrice {value currency{code}} " +
                        "marketPrice {value currency{code}} " +
                        "leverage " +
                        "status " +
                        "cumulativeAmount {value currency{code}} " +
                        "userReference " +
                        "timestamp " +
                        "trades {tradeId}" +
                        "} }",
                "data.order");
        assertEquals(1, result.get("uid"));
        assertEquals("60ddfbc11f8b45000696de3f", result.get("orderId"));
        assertEquals("BID", result.get("type"));
        assertEquals("001", getStrategyValue(result.get("strategy")).getStrategyId());
        assertEquals(BTC_USDT, getCurrencyPairValue(result.get("currencyPair")));
        final CurrencyAmountDTO amount = getCurrencyAmountValue(result.get("amount"));
        assertEquals(0, new BigDecimal("0.00100000").compareTo(amount.getValue()));
        assertEquals(BTC, amount.getCurrency());
        final CurrencyAmountDTO averagePrice = getCurrencyAmountValue(result.get("averagePrice"));
        assertEquals(0, new BigDecimal("33183.5").compareTo(averagePrice.getValue()));
        assertEquals(USDT, averagePrice.getCurrency());
        final CurrencyAmountDTO marketPrice = getCurrencyAmountValue(result.get("marketPrice"));
        assertEquals(0, new BigDecimal("33183.50000000").compareTo(marketPrice.getValue()));
        assertEquals(USDT, marketPrice.getCurrency());
        assertEquals("NEW", result.get("status"));
        final CurrencyAmountDTO cumulativeAmount = getCurrencyAmountValue(result.get("cumulativeAmount"));
        assertEquals(0, new BigDecimal("0.001").compareTo(cumulativeAmount.getValue()));
        assertTrue(result.get("timestamp").toString().contains("2021-07-01T19:30:42.054417"));
        JSONArray trades = (JSONArray) result.get("trades");
        assertEquals(2, trades.size());
    }

    @Test
    @DisplayName("Get order by orderId")
    void getOrderByOrderId() {
        Map<String, Object> result = dgsQueryExecutor.executeAndExtractJsonPath(
                " { orderByOrderId(orderId: \"60ddfbc11f8b45000696de3f\") {" +
                        "uid " +
                        "} }",
                "data.orderByOrderId");
        assertEquals(1, result.get("uid"));
    }

}
