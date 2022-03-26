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
import tech.cassandre.trading.bot.api.graphql.client.generated.client.TradeByTradeIdGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.TradeByTradeIdProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.TradeGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.TradesGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.TradesProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.Order;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.Trade;
import tech.cassandre.trading.bot.api.graphql.data.TradeDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;
import tech.cassandre.trading.bot.api.graphql.test.util.base.BaseDataFetcherTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.api.graphql.client.generated.types.TradeType.ASK;
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
    @DisplayName("trades: [Trade]")
    void trades() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new TradesGraphQLQuery.Builder().build(),
                new TradesProjectionRoot().uid());
        // Query execution.
        List<Order> orders = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.Trades + "[*]",
                new TypeRef<>() {
                });
        // Tests.
        assertEquals(416, orders.size());
    }

    @Test
    @DisplayName("trade(uid: Int): Trade")
    void trade() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new TradeGraphQLQuery.Builder().uid(24).build(),
                new TradesProjectionRoot().uid()
                        .tradeId()
                        .type().getParent()
                        .orderId()
                        .order().uid().orderId().getParent()
                        .currencyPair().baseCurrency().code().getParent().quoteCurrency().code().getParent().getParent()
                        .amount().value().currency().code().getParent().getParent()
                        .price().value().currency().code().getParent().getParent()
                        .fee().value().currency().code().getParent().getParent()
                        .timestamp());
        // Query execution.
        Trade trade = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.Trade,
                new TypeRef<>() {
                });
        // Tests.
        assertNotNull(trade);
        assertEquals(24, trade.getUid());
        assertEquals("60df231c2e113d2923052d18", trade.getTradeId());
        assertEquals(ASK, trade.getType());
        assertEquals("60df231c38ec01000687554e", trade.getOrderId());
        assertEquals(19, trade.getOrder().getUid());
        assertEquals("60df231c38ec01000687554e", trade.getOrder().getOrderId());
        assertEquals(BTC, trade.getCurrencyPair().getBaseCurrency());
        assertEquals(USDT, trade.getCurrencyPair().getQuoteCurrency());
        // Amount.
        assertEquals(0, new BigDecimal("0.00100000").compareTo(trade.getAmount().getValue()));
        assertEquals(BTC, trade.getAmount().getCurrency());
        // Price.
        assertEquals(0, new BigDecimal("33591.90000000").compareTo(trade.getPrice().getValue()));
        assertEquals(USDT, trade.getPrice().getCurrency());
        // Fee.
        assertEquals(0, new BigDecimal("0.03359190").compareTo(trade.getFee().getValue()));
        assertEquals(USDT, trade.getFee().getCurrency());
        assertNotNull(trade.getTimestamp().toString());
    }

    @Test
    @DisplayName("tradeByTradeId(tradeId: String): Trade")
    void getTradeByTradeId() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new TradeByTradeIdGraphQLQuery.Builder().tradeId("60df231c2e113d2923052d18").build(),
                new TradeByTradeIdProjectionRoot().uid()
                        .tradeId()
                        .type().getParent()
                        .orderId()
                        .order().uid().orderId().getParent()
                        .currencyPair().baseCurrency().code().getParent().quoteCurrency().code().getParent().getParent()
                        .amount().value().currency().code().getParent().getParent()
                        .price().value().currency().code().getParent().getParent()
                        .fee().value().currency().code().getParent().getParent()
                        .timestamp());
        // Query execution.
        Trade trade = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.TradeByTradeId,
                new TypeRef<>() {
                });
        // Tests.
        assertNotNull(trade);
        assertEquals(24, trade.getUid());
        assertEquals("60df231c2e113d2923052d18", trade.getTradeId());
        assertEquals(ASK, trade.getType());
        assertEquals("60df231c38ec01000687554e", trade.getOrderId());
        assertEquals(19, trade.getOrder().getUid());
        assertEquals("60df231c38ec01000687554e", trade.getOrder().getOrderId());
        assertEquals(BTC, trade.getCurrencyPair().getBaseCurrency());
        assertEquals(USDT, trade.getCurrencyPair().getQuoteCurrency());
        // Amount.
        assertEquals(0, new BigDecimal("0.00100000").compareTo(trade.getAmount().getValue()));
        assertEquals(BTC, trade.getAmount().getCurrency());
        // Price.
        assertEquals(0, new BigDecimal("33591.90000000").compareTo(trade.getPrice().getValue()));
        assertEquals(USDT, trade.getPrice().getCurrency());
        // Fee.
        assertEquals(0, new BigDecimal("0.03359190").compareTo(trade.getFee().getValue()));
        assertEquals(USDT, trade.getFee().getCurrency());
        assertNotNull(trade.getTimestamp().toString());
    }

}
