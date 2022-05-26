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
import tech.cassandre.trading.bot.api.graphql.client.generated.client.OrderByOrderIdGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.OrderGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.OrderProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.OrdersGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.OrdersProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.Order;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.OrderType;
import tech.cassandre.trading.bot.api.graphql.data.OrderDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;
import tech.cassandre.trading.bot.api.graphql.test.util.base.BaseDataFetcherTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.api.graphql.client.generated.types.OrderStatus.NEW;
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
    @DisplayName("orders: [Order]")
    void orders() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new OrdersGraphQLQuery.Builder().build(),
                new OrdersProjectionRoot().uid());
        // Query execution.
        List<Order> orders = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.Orders + "[*]",
                new TypeRef<>() {
                });
        // Tests.
        assertEquals(345, orders.size());
    }

    @Test
    @DisplayName("order(uid: Int): Order")
    void order() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new OrderGraphQLQuery.Builder().uid(1).build(),
                new OrderProjectionRoot().uid()
                        .orderId()
                        .type().getParent()
                        .strategy().uid().strategyId().getParent()
                        .currencyPair().baseCurrency().code().getParent().quoteCurrency().code().getParent().getParent()
                        .amount().value().currency().code().getParent().getParent()
                        .averagePrice().value().currency().code().getParent().getParent()
                        .limitPrice().value().currency().code().getParent().getParent()
                        .marketPrice().value().currency().code().getParent().getParent()
                        .leverage()
                        .status().getParent()
                        .cumulativeAmount().value().currency().code().getParent().getParent()
                        .userReference()
                        .timestamp()
                        .trades().uid()
                        .tradeId());
        // Query execution.
        Order order = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.Order,
                new TypeRef<>() {
                });
        // Tests.
        assertNotNull(order);
        assertEquals(1, order.getUid());
        assertEquals("60ddfbc11f8b45000696de3f", order.getOrderId());
        assertEquals(OrderType.BID, order.getType());
        assertEquals(1, order.getStrategy().getUid());
        assertEquals("001", order.getStrategy().getStrategyId());
        assertEquals(BTC_USDT, order.getCurrencyPair());
        // Amount.
        assertEquals(0, new BigDecimal("0.00100000").compareTo(order.getAmount().getValue()));
        assertEquals(BTC, order.getAmount().getCurrency());
        // Average price.
        assertEquals(0,  new BigDecimal("33183.5").compareTo(order.getAveragePrice().getValue()));
        assertEquals(USDT, order.getAveragePrice().getCurrency());
        // Market price.
        assertEquals(0,  new BigDecimal("33183.50000000").compareTo(order.getMarketPrice().getValue()));
        assertEquals(USDT, order.getMarketPrice().getCurrency());
        // Cumulative amount.
        assertEquals(0,  new BigDecimal("0.001").compareTo(order.getCumulativeAmount().getValue()));
        assertEquals(BTC, order.getCumulativeAmount().getCurrency());
        assertEquals(NEW, order.getStatus());
        assertNotNull(order.getTimestamp().toString());
        // Trades
        assertEquals(2, order.getTrades().size());
    }

    @Test
    @DisplayName("orderByOrderId(orderId: String): Order")
    void orderByOrderId() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new OrderByOrderIdGraphQLQuery.Builder().orderId("60ddfbc11f8b45000696de3f").build(),
                new OrderProjectionRoot().uid()
                        .orderId()
                        .type().getParent()
                        .strategy().uid().strategyId().getParent()
                        .currencyPair().baseCurrency().code().getParent().quoteCurrency().code().getParent().getParent()
                        .amount().value().currency().code().getParent().getParent()
                        .averagePrice().value().currency().code().getParent().getParent()
                        .limitPrice().value().currency().code().getParent().getParent()
                        .marketPrice().value().currency().code().getParent().getParent()
                        .leverage()
                        .status().getParent()
                        .cumulativeAmount().value().currency().code().getParent().getParent()
                        .userReference()
                        .timestamp()
                        .trades().uid()
                        .tradeId());
        // Query execution.
        Order order = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.OrderByOrderId,
                new TypeRef<>() {
                });
        // Tests.
        assertNotNull(order);
        assertEquals(1, order.getUid());
        assertEquals("60ddfbc11f8b45000696de3f", order.getOrderId());
        assertEquals(OrderType.BID, order.getType());
        assertEquals(1, order.getStrategy().getUid());
        assertEquals("001", order.getStrategy().getStrategyId());
        assertEquals(BTC_USDT, order.getCurrencyPair());
        // Amount.
        assertEquals(0, new BigDecimal("0.00100000").compareTo(order.getAmount().getValue()));
        assertEquals(BTC, order.getAmount().getCurrency());
        // Average price.
        assertEquals(0,  new BigDecimal("33183.5").compareTo(order.getAveragePrice().getValue()));
        assertEquals(USDT, order.getAveragePrice().getCurrency());
        // Market price.
        assertEquals(0,  new BigDecimal("33183.50000000").compareTo(order.getMarketPrice().getValue()));
        assertEquals(USDT, order.getMarketPrice().getCurrency());
        // Cumulative amount.
        assertEquals(0,  new BigDecimal("0.001").compareTo(order.getCumulativeAmount().getValue()));
        assertEquals(BTC, order.getCumulativeAmount().getCurrency());
        assertEquals(NEW, order.getStatus());
        assertNotNull(order.getTimestamp().toString());
        // Trades
        assertEquals(2, order.getTrades().size());
    }

}
