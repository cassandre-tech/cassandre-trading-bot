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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Position data fetcher test.
 */
@ActiveProfiles("schedule-disabled")
@DisplayName("Position data fetcher test")
@SpringBootTest(classes = {CassandreTradingBot.class, DgsAutoConfiguration.class, PositionDataFetcher.class})
@TestPropertySource(properties = {"spring.liquibase.change-log = classpath:db/test/core/complete-database.yaml"})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class PositionDataFetcherTest extends BaseDataFetcherTest {

    @Autowired
    DgsQueryExecutor dgsQueryExecutor;

    @Test
    @DisplayName("Get all positions")
    void getAllPositions() {
        List<Integer> ids = dgsQueryExecutor.executeAndExtractJsonPath(
                " { positions { positionId }}",
                "data.positions[*].positionId");
        assertTrue(ids.contains(1));   // Real order.
        assertFalse(ids.contains(999));   // Invented order.
        assertEquals(182, ids.size());
    }

    @Test
    @DisplayName("Get position by uid")
    void getPositionById() {
        Map<String, Object> result = dgsQueryExecutor.executeAndExtractJsonPath(
                " { position(uid: 31) {" +
                        "uid " +
                        "positionId " +
                        "type " +
                        "strategy {strategyId} " +
                        "currencyPair {baseCurrency{code} quoteCurrency{code}}" +
                        "amount {value currency{code}} " +
                        "rules {stopGainPercentage stopLossPercentage} " +
                        "status " +
                        "forceClosing " +
                        "openingOrder {uid orderId}" +
                        "closingOrder {uid orderId}" +
                        "lowestCalculatedGain {percentage amount{value currency{code}} fees{value currency{code}}}" +
                        "highestCalculatedGain {percentage amount{value currency{code}} fees{value currency{code}}}" +
                        "latestCalculatedGain {percentage amount{value currency{code}} fees{value currency{code}}}" +
                        "gain {percentage amount{value currency{code}} fees{value currency{code}}}" +
                        "} }",
                "data.position");
        assertEquals(31, result.get("uid"));
        assertEquals(14, result.get("positionId"));
        assertEquals("LONG", result.get("type"));
        assertEquals("002", getStrategyValue(result.get("strategy")).getStrategyId());
        assertEquals(UNI_USDT, getCurrencyPairValue(result.get("currencyPair")));
        final CurrencyAmountDTO amount = getCurrencyAmountValue(result.get("amount"));
        assertEquals(0, new BigDecimal("1").compareTo(amount.getValue()));
        assertEquals(UNI, amount.getCurrency());
        Map<String, Double> rules = (Map<String, Double>) result.get("rules");
        assertEquals(6L, rules.get("stopGainPercentage"));
        assertEquals(15L, rules.get("stopLossPercentage"));
        assertEquals("CLOSED", result.get("status"));
        assertEquals(false, result.get("forceClosing"));
        Map<String, String> openingOrder = (Map<String, String>) result.get("openingOrder");
        assertEquals(49, openingOrder.get("uid"));
        Map<String, String> closingOrder = (Map<String, String>) result.get("closingOrder");
        assertEquals(54, closingOrder.get("uid"));

        GainDTO lowestCalculatedGain = getGainValue(result.get("lowestCalculatedGain"));
        assertEquals(-4.696751117706299, lowestCalculatedGain.getPercentage());
        assertEquals(0, new BigDecimal("-0.9433000000000000").compareTo(lowestCalculatedGain.getAmount().getValue()));
        assertEquals(USDT, lowestCalculatedGain.getAmount().getCurrency());

        GainDTO highestCalculatedGain = getGainValue(result.get("highestCalculatedGain"));
        assertEquals(5.908155918121338, highestCalculatedGain.getPercentage());
        assertEquals(0, new BigDecimal("1.1866").compareTo(highestCalculatedGain.getAmount().getValue()));
        assertEquals(USDT, highestCalculatedGain.getAmount().getCurrency());

        GainDTO latestCalculatedGain = getGainValue(result.get("latestCalculatedGain"));
        assertEquals(6.002260208129883, latestCalculatedGain.getPercentage());
        assertEquals(0, new BigDecimal("1.2055").compareTo(latestCalculatedGain.getAmount().getValue()));
        assertEquals(USDT, latestCalculatedGain.getAmount().getCurrency());

        GainDTO gain = getGainValue(result.get("gain"));
        assertEquals(5.93, gain.getPercentage());
        assertEquals(0, new BigDecimal("1.1911").compareTo(gain.getAmount().getValue()));
        assertEquals(USDT, gain.getAmount().getCurrency());
    }

    @Test
    @DisplayName("Get positions by strategy (id)")
    void getPositionsByStrategy() {
        List<Integer> position1Strategies = dgsQueryExecutor.executeAndExtractJsonPath(
                " { positionsByStrategy(uid: 1) {" +
                        "uid " +
                        "} }",
                "data.positionsByStrategy");
        assertEquals(90, position1Strategies.size());

        List<Integer> position2Strategies = dgsQueryExecutor.executeAndExtractJsonPath(
                " { positionsByStrategy(uid: 2) {" +
                        "uid " +
                        "} }",
                "data.positionsByStrategy");
        assertEquals(92, position2Strategies.size());
    }

    @Test
    @DisplayName("Get positions by strategyId (strategyId)")
    void getPositionsByStrategyId() {
        List<Integer> position1Strategies = dgsQueryExecutor.executeAndExtractJsonPath(
                " { positionsByStrategyId(strategyId: \"001\") {" +
                        "uid " +
                        "} }",
                "data.positionsByStrategyId");
        assertEquals(90, position1Strategies.size());

        List<Integer> position2Strategies = dgsQueryExecutor.executeAndExtractJsonPath(
                " { positionsByStrategyId(strategyId: \"002\") {" +
                        "uid " +
                        "} }",
                "data.positionsByStrategyId");
        assertEquals(92, position2Strategies.size());
    }


    @Test
    @DisplayName("Get positions by strategy (id) and status")
    void getPositionsByStrategyAndStatus() {
        List<Integer> closedPositions = dgsQueryExecutor.executeAndExtractJsonPath(
                " { positionsByStrategyAndStatus(uid: 1, status:CLOSED) {" +
                        "uid " +
                        "} }",
                "data.positionsByStrategyAndStatus");
        assertEquals(82, closedPositions.size());

        List<Integer> openedPositions = dgsQueryExecutor.executeAndExtractJsonPath(
                " { positionsByStrategyAndStatus(uid: 1, status:OPENED) {" +
                        "uid " +
                        "} }",
                "data.positionsByStrategyAndStatus");
        assertEquals(8, openedPositions.size());
    }

    @Test
    @DisplayName("Get positions by strategyId (strategyId) and status")
    void getPositionsByStrategyIdAndStatus() {
        List<Integer> closedPositions = dgsQueryExecutor.executeAndExtractJsonPath(
                " { positionsByStrategyIdAndStatus(strategyId: \"001\", status:CLOSED) {" +
                        "uid " +
                        "} }",
                "data.positionsByStrategyIdAndStatus");
        assertEquals(82, closedPositions.size());

        List<Integer> openedPositions = dgsQueryExecutor.executeAndExtractJsonPath(
                " { positionsByStrategyIdAndStatus(strategyId: \"001\", status:OPENED) {" +
                        "uid " +
                        "} }",
                "data.positionsByStrategyIdAndStatus");
        assertEquals(8, openedPositions.size());
    }

}
