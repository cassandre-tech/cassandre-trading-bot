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
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyAndStatusGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyAndStatusProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyIdAndStatusGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyIdAndStatusProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyIdGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyIdProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.TradesGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.TradesProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.Order;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.Position;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.PositionType;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.Trade;
import tech.cassandre.trading.bot.api.graphql.data.PositionDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;
import tech.cassandre.trading.bot.api.graphql.test.util.base.BaseDataFetcherTest;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static graphql.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.api.graphql.client.generated.types.PositionStatus.CLOSED;
import static tech.cassandre.trading.bot.api.graphql.client.generated.types.PositionStatus.OPENED;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.UNI;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Position data fetcher test.
 */
@ActiveProfiles("schedule-disabled")
@DisplayName("Position data fetcher test")
@SpringBootTest(classes = {DgsAutoConfiguration.class, CassandreTradingBot.class, DgsAutoConfiguration.class, PositionDataFetcher.class})
@TestPropertySource(properties = {"spring.liquibase.change-log = classpath:db/test/core/complete-database.yaml"})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class PositionDataFetcherTest extends BaseDataFetcherTest {

    // TODO Test gain fees.

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    @DisplayName("Get all positions")
    void getAllPositions() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new PositionsGraphQLQuery.Builder().build(),
                new PositionsProjectionRoot().uid());
        // Query execution.
        List<Position> positions = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.Positions + "[*]",
                new TypeRef<>() {
                });
        // Tests.
        assertEquals(182, positions.size());
    }

    @Test
    @DisplayName("Get position by uid")
    void getPositionById() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new PositionGraphQLQuery.Builder().uid(31).build(),
                new PositionsProjectionRoot().uid()
                        .positionId()
                        .type().getParent()
                        .strategy().strategyId().getParent()
                        .currencyPair().baseCurrency().code().getParent().quoteCurrency().code().getParent().getParent()
                        .amount().value().currency().code().getParent().getParent()
                        .rules().stopGainPercentage().stopLossPercentage().getParent()
                        .status().getParent()
                        .autoClose()
                        .forceClosing()
                        .openingOrder().uid().orderId().getParent()
                        .closingOrder().uid().orderId().getParent()
                        .lowestCalculatedGain().percentage().amount().value().currency().code().getParent().getParent().getParent()
                        .highestCalculatedGain().percentage().amount().value().currency().code().getParent().getParent().getParent()
                        .latestCalculatedGain().percentage().amount().value().currency().code().getParent().getParent().getParent()
                        .gain().percentage().amount().value().currency().code());
        // Query execution.
        Position position = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.Position,
                new TypeRef<>() {
                });
        // Tests.
        assertNotNull(position);
        assertEquals(31, position.getUid());
        assertEquals(14, position.getPositionId());
        assertEquals(PositionType.LONG, position.getType());
        assertEquals("002", position.getStrategy().getStrategyId());
        // TODO It should not be DTO!
        assertEquals(UNI, position.getCurrencyPair().getBaseCurrency());
        assertEquals(USDT, position.getCurrencyPair().getQuoteCurrency());
        // Amount.
        assertEquals(0, new BigDecimal("1").compareTo(position.getAmount().getValue()));
        assertEquals(UNI, position.getAmount().getCurrency());
        // Rules
        assertEquals(6L, position.getRules().getStopGainPercentage());
        assertEquals(15L, position.getRules().getStopLossPercentage());
        assertEquals(CLOSED, position.getStatus());
        assertTrue(position.getAutoClose());
        assertFalse(position.getForceClosing());
        assertEquals(49, position.getOpeningOrder().getUid());
        assertEquals(54, position.getClosingOrder().getUid());
        // Gains.
        assertEquals(-4.696751117706299, position.getLowestCalculatedGain().getPercentage());
        assertEquals(0, new BigDecimal("-0.9433000000000000").compareTo(position.getLowestCalculatedGain().getAmount().getValue()));
        assertEquals(USDT, position.getLowestCalculatedGain().getAmount().getCurrency());
        assertEquals(5.908155918121338, position.getHighestCalculatedGain().getPercentage());
        assertEquals(0, new BigDecimal("1.1866").compareTo(position.getHighestCalculatedGain().getAmount().getValue()));
        assertEquals(USDT, position.getHighestCalculatedGain().getAmount().getCurrency());
        assertEquals(6.002260208129883, position.getLatestCalculatedGain().getPercentage());
        assertEquals(0, new BigDecimal("1.2055").compareTo(position.getLatestCalculatedGain().getAmount().getValue()));
        assertEquals(USDT, position.getLatestCalculatedGain().getAmount().getCurrency());
        assertEquals(5.93, position.getGain().getPercentage());
        assertEquals(0, new BigDecimal("1.1911").compareTo(position.getGain().getAmount().getValue()));
        assertEquals(USDT, position.getGain().getAmount().getCurrency());
    }

    @Test
    @DisplayName("Get positions by strategy (id)")
    void getPositionsByStrategy() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new PositionsByStrategyGraphQLQuery.Builder().uid(1).build(),
                new PositionsByStrategyProjectionRoot().uid());
        // Query execution.
        List<Position> positions = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.PositionsByStrategy,
                new TypeRef<>() {
                });
        // Tests.
        assertEquals(90, positions.size());
    }

    @Test
    @DisplayName("Get positions by strategyId (strategyId)")
    void getPositionsByStrategyId() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new PositionsByStrategyIdGraphQLQuery.Builder().strategyId("002").build(),
                new PositionsByStrategyIdProjectionRoot().uid());
        // Query execution.
        List<Position> positions = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.PositionsByStrategyId,
                new TypeRef<>() {
                });
        // Tests.
        assertEquals(92, positions.size());
    }


    @Test
    @DisplayName("Get positions by strategy (id) and status")
    void getPositionsByStrategyAndStatus() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new PositionsByStrategyAndStatusGraphQLQuery.Builder().uid(1).status(CLOSED).build(),
                new PositionsByStrategyAndStatusProjectionRoot().uid());
        // Query execution.
        List<Position> positions = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.PositionsByStrategyAndStatus,
                new TypeRef<>() {
                });
        // Tests.
        assertEquals(82, positions.size());
    }

    @Test
    @DisplayName("Get positions by strategyId (strategyId) and status")
    void getPositionsByStrategyIdAndStatus() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new PositionsByStrategyIdAndStatusGraphQLQuery.Builder().strategyId("001").status(OPENED).build(),
                new PositionsByStrategyIdAndStatusProjectionRoot().uid());
        // Query execution.
        List<Position> positions = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.PositionsByStrategyIdAndStatus,
                new TypeRef<>() {
                });
        // Tests.
        assertEquals(8, positions.size());
    }

}
