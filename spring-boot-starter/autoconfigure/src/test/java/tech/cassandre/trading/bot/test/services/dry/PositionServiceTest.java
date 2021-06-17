package tech.cassandre.trading.bot.test.services.dry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.services.dry.mocks.PositionServiceDryModeTestMock;
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
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Service - Dry - Position service")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@DirtiesContext(classMode = BEFORE_CLASS)
@Import(PositionServiceDryModeTestMock.class)
public class PositionServiceTest extends BaseTest {

    @Autowired
    private PositionService positionService;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TickerFlux tickerFlux;

    @Test
    @DisplayName("Check position lifecycle")
    public void checkPositionLifecycle() {
        // First tickers - cp1 & cp2 (dry mode).
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
        final long position1Id = position1Result.getPosition().getId();

        // After position creation, its status is OPENING but order and trades arrives from dry mode.
        // One position status update because of OPENING, one position status update because of OPENED.
        // For position updates.
        // First: because of position creation.
        // Second: order update with status to NEW.
        // Third: trade corresponding to the order arrives.
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));

        // =============================================================================================================
        // Step 2 - Creates position 2 (ETH_USDT, 0.0002, 20% stop loss, price of 0.3).
        // As the order is validated and the trade arrives, the position should be opened.
        final PositionCreationResultDTO position2Result = strategy.createLongPosition(ETH_USDT,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).build());
        assertTrue(position2Result.isSuccessful());
        assertEquals("DRY_ORDER_000000002", position2Result.getPosition().getOpeningOrder().getOrderId());
        final long position2Id = position2Result.getPosition().getId();

        // After position creation, its status is OPENING
        // One position status update because of OPENING, one position status update because of OPENED.
        // For position updates.
        // First: because of position creation.
        // Second: order update with status to NEW.
        // Third: trade corresponding to the order arrives.
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position2Id).getStatus()));

        // =============================================================================================================
        // Tickers are coming.

        // Second tickers - cp1 & cp2.
        // Position 1 (ETH/BTC, 0.0001, 100% stop gain, price of 0.2)
        // Position 2 (ETH_USDT, 0.0002, 20% stop loss, price of 0.3)
        // ETH/BTC - 0.3 - 50% gain.
        // ETH/USDT - 0.3 - no gain.
        // No change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.3")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("0.3")).build());
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position2Id).getStatus()));

        // Third tickers - cp1 & cp2.
        // Position 1 (ETH/BTC, 0.0001, 100% stop gain, price of 0.2)
        // Position 2 (ETH_USDT, 0.0002, 20% stop loss, price of 0.3)
        // ETH/BTC - 0.4 - 100% gain.
        // ETH/USDT - 0.6 - 100% gain.
        // Should close position 1.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.4")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("0.6")).build());
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(position1Id).getStatus()));
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position2Id).getStatus()));

        // Fourth tickers - cp1 & cp2.
        // Position 1 (ETH/BTC, 0.0001, 100% stop gain, price of 0.2)
        // Position 2 (ETH_USDT, 0.0002, 20% stop loss, price of 0.3)
        // ETH/BTC - 0.4 - 100% gain.
        // ETH/USDT - 0.1 - 70% loss.
        // Should close position 2.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.5")).build());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("0.1")).build());
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(position1Id).getStatus()));
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(position2Id).getStatus()));
    }

    /**
     * Retrieve position from database.
     *
     * @param id position id
     * @return position
     */
    private PositionDTO getPositionDTO(final long id) {
        final Optional<PositionDTO> p = positionService.getPositionById(id);
        if (p.isPresent()) {
            return p.get();
        } else {
            throw new PositionException("Position not found : " + id);
        }
    }

}
