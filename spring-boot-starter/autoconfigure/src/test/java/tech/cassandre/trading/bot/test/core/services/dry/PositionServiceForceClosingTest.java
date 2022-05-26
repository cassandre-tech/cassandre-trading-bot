package tech.cassandre.trading.bot.test.core.services.dry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;
import tech.cassandre.trading.bot.util.exception.PositionException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Service - Dry - Position service")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class PositionServiceForceClosingTest extends BaseTest {

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private OrderFlux orderFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Autowired
    private PositionService positionService;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check force closing")
    public void checkForceClosing() {
        assertTrue(strategy.getConfiguration().isDryMode());

        // =============================================================================================================
        // First tickers (dry mode).
        // ETH/BTC - 0.2.
        // ETH/USDT - 0.3.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.2")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("0.3")).build());
        await().untilAsserted(() -> assertEquals(2, strategy.getTickersUpdatesReceived().size()));

        // =============================================================================================================
        // Step 1 - Creates position 1 (ETH/BTC, 0.0001, 100% stop gain, price of 0.2).
        // As the order is validated and the trade arrives, the position should be opened.
        final PositionCreationResultDTO position1Result = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(100f).build());
        assertTrue(position1Result.isSuccessful());
        assertEquals("DRY_ORDER_000000001", position1Result.getPosition().getOpeningOrder().getOrderId());
        final long position1Uid = position1Result.getPosition().getUid();

        // After position creation, its status is OPENING but order and trades will arrive and set it to OPENED.
        await().untilAsserted(() -> {
            orderFlux.update();
            tradeFlux.update();
            assertEquals(OPENED, getPositionDTO(position1Uid).getStatus());
        });
        // One position status update when status is OPENING, another position status update when status is OPENED.
        assertEquals(2, strategy.getPositionsStatusUpdatesCount());
        // For position updates:
        // 1st: position creation with an order having the PENDING_NEW status.
        // 2nd: same order arrives but with a NEW status.
        // 3rd: same order arrives but with a FILLED status.
        // 4th: trade corresponding to the order arrives and the position is now OPENED.
        assertEquals(4, strategy.getPositionsUpdatesCount());

        // =============================================================================================================
        // Step 2 - Creates position 2 (ETH_USDT, 0.0002, 20% stop loss, price of 0.3).
        // As the order is validated and the trade arrives, the position should be opened.
        final PositionCreationResultDTO position2Result = strategy.createLongPosition(ETH_USDT,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).build());
        assertTrue(position2Result.isSuccessful());
        assertEquals("DRY_ORDER_000000002", position2Result.getPosition().getOpeningOrder().getOrderId());
        final long position2Uid = position2Result.getPosition().getUid();

        // After position creation, its status is OPENING but order and trades will arrive and set it to OPENED.
        await().untilAsserted(() -> {
            orderFlux.update();
            tradeFlux.update();
            assertEquals(OPENED, getPositionDTO(position2Uid).getStatus());
        });
        // One position status update when status is OPENING, another position status update when status is OPENED.
        assertEquals(4, strategy.getPositionsStatusUpdatesCount());
        // For position updates:
        // 1st: position creation with an order having the PENDING_NEW status.
        // 2nd: same order arrives but with a NEW status.
        // 3rd: same order arrives but with a FILLED status.
        // 4th: trade corresponding to the order arrives and the position is now OPENED.
        assertEquals(8, strategy.getPositionsUpdatesCount());

        // =============================================================================================================
        // Tickers are coming.

        // Position 1 (ETH/BTC, 0.0001, 100% stop gain, price of 0.2)
        // Position 2 (ETH/USDT, 0.0002, 20% stop loss, price of 0.3)
        // Ticker ETH/BTC - 0.31 - 50% gain.
        // Ticker ETH/USDT - 0.31 - no gain.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.3")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("0.3")).build());
        // Two positions updates arrives because lowestGainPrice, highestGainPrice and latestGainPrice are updated.
        await().untilAsserted(() -> {
            orderFlux.update();
            tradeFlux.update();
            await().untilAsserted(() -> assertEquals(10, strategy.getPositionsUpdatesCount()));
        });
        PositionDTO position1 = getPositionDTO(position1Uid);
        assertEquals(0, new BigDecimal("0.3").compareTo(position1.getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.3").compareTo(position1.getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.3").compareTo(position1.getLatestGainPrice().getValue()));
        PositionDTO position2 = getPositionDTO(position2Uid);
        assertEquals(0, new BigDecimal("0.3").compareTo(position2.getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.3").compareTo(position2.getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.3").compareTo(position2.getLatestGainPrice().getValue()));

        // We will force the closing of position 2.
        strategy.closePosition(position2Uid);

        // Two new tickers are emitted, they will be received by positions but will not trigger rules.
        // Two positions updates arrives because lowestGainPrice, highestGainPrice and latestGainPrice are updated.
        // But, as position 2 is marked as "force closing", the ticker updates will close one position.
        // For position updates:
        // 11: Position 1 updates with new lowestGainPrice, highestGainPrice and latestGainPrice.
        // 12: Position 2 updates with new lowestGainPrice, highestGainPrice and latestGainPrice + close position change.
        // 13: Position 2 now CLOSING with a PENDING_NEW order.
        // 14: Position 2 still CLOSING with updated order (NEW_STATUS status).
        // 15: Position 2 still CLOSING with updated order (FILLED status).
        // 16: Position 2 now CLOSED. Order's trade has arrived.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.31")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("0.31")).build());
        await().untilAsserted(() -> {
            orderFlux.update();
            tradeFlux.update();
            assertEquals(16, strategy.getPositionsUpdatesCount());
        });
        assertEquals(OPENED, getPositionDTO(position1Uid).getStatus());
        assertEquals(CLOSED, getPositionDTO(position2Uid).getStatus());

        // We will force closing of position 1.
        strategy.closePosition(position1Uid);

        // Two new tickers are emitted, they will be received by positions but will not trigger rules.
        // Position1 updates arrives because lowestGainPrice, highestGainPrice and latestGainPrice are updated.
        // But, as position 1 is marked as "force closing", the ticker updates will close one position.
        // Position 2's lowestGainPrice, highestGainPrice and latestGainPrice will not be updated as the position is already closed.
        // For position updates:
        // 17: Position 1 updates with new lowestGainPrice, highestGainPrice and latestGainPrice + close position change.
        // 18: Position 1 now CLOSING with a PENDING_NEW order.
        // 19: Position 1 still CLOSING with updated order (NEW_STATUS status).
        // 20: Position 1 still CLOSING with updated order (FILLED status).
        // 21: Position 1 now CLOSED. Order's trade has arrived.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.32")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("0.32")).build());
        await().untilAsserted(() -> {
            orderFlux.update();
            tradeFlux.update();
            assertEquals(21, strategy.getPositionsUpdatesCount());
        });
        assertEquals(CLOSED, getPositionDTO(position1Uid).getStatus());
        assertEquals(CLOSED, getPositionDTO(position2Uid).getStatus());
    }

    /**
     * Retrieve position from database.
     *
     * @param uid position uid
     * @return position
     */
    private PositionDTO getPositionDTO(final long uid) {
        final Optional<PositionDTO> p = positionService.getPositionByUid(uid);
        if (p.isPresent()) {
            return p.get();
        } else {
            throw new PositionException("Position not found : " + uid);
        }
    }

}
