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
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyIdAndStatusGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyIdAndStatusProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyIdGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyIdProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyUidAndStatusGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyUidAndStatusProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyUidGraphQLQuery;
import tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsProjectionRoot;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.Position;
import tech.cassandre.trading.bot.api.graphql.client.generated.types.PositionType;
import tech.cassandre.trading.bot.api.graphql.data.PositionDataFetcher;
import tech.cassandre.trading.bot.api.graphql.test.CassandreTradingBot;
import tech.cassandre.trading.bot.api.graphql.test.util.base.BaseDataFetcherTest;

import java.math.BigDecimal;
import java.util.List;

import static graphql.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.api.graphql.client.generated.types.PositionStatus.CLOSED;
import static tech.cassandre.trading.bot.api.graphql.client.generated.types.PositionStatus.OPENED;
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

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    @DisplayName("positions: [Position]")
    void positions() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsGraphQLQuery.Builder().build(),
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
    @DisplayName("position(uid: Int): Position")
    void position() {
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
    @DisplayName("positionsByStrategyUid(strategyUid: Int): [Position]")
    void positionsByStrategyUid() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new PositionsByStrategyUidGraphQLQuery.Builder().strategyUid(1).build(),
                new tech.cassandre.trading.bot.api.graphql.client.generated.client.PositionsByStrategyUidProjectionRoot().uid());
        // Query execution.
        List<Position> positions = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.PositionsByStrategyUid,
                new TypeRef<>() {
                });
        // Tests.
        assertEquals(90, positions.size());
    }

    @Test
    @DisplayName("positionsByStrategyId(strategyId: String): [Position]")
    void positionsByStrategyId() {
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
    @DisplayName("positionsByStrategyUidAndStatus(strategyUid: Int, status: PositionStatus): [Position]")
    void positionsByStrategyUidAndStatus() {
        // Query and fields definition.
        GraphQLQueryRequest graphQLQueryRequest = new GraphQLQueryRequest(
                new PositionsByStrategyUidAndStatusGraphQLQuery.Builder().strategyUid(1).status(CLOSED).build(),
                new PositionsByStrategyUidAndStatusProjectionRoot().uid());
        // Query execution.
        List<Position> positions = dgsQueryExecutor.executeAndExtractJsonPathAsObject(
                graphQLQueryRequest.serialize(),
                "data." + DgsConstants.QUERY.PositionsByStrategyUidAndStatus,
                new TypeRef<>() {
                });
        // Tests.
        assertEquals(82, positions.size());
    }

    @Test
    @DisplayName("positionsByStrategyIdAndStatus(strategyId: String, status: PositionStatus): [Position]")
    void positionsByStrategyIdAndStatus() {
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
