package tech.cassandre.trading.bot.tmp.modes.dry;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.tmp.modes.dry.mocks.PositionServiceDryModeTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_EXCHANGE_DRY;


@SpringBootTest
@DisplayName("Dry mode - Position service")
@ActiveProfiles("schedule-disabled")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@DirtiesContext(classMode = AFTER_CLASS)
@Import(PositionServiceDryModeTestMock.class)
@Disabled
public class PositionServiceDryModeTest extends BaseTest {

    public static final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

    public static final CurrencyPairDTO cp2 = new CurrencyPairDTO(ETH, USDT);

    @Autowired
    private PositionService positionService;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private PositionFlux positionFlux;

    @Autowired
    private PositionRepository positionRepository;

    @Test
    @Tag("notReviewed")
    @DisplayName("Check position lifecycle")
    public void checkPositionLifecycle() throws InterruptedException {
        // First tickers - cp1 & cp2 (dry mode).
        // ETH, BTC - bid 0.2 / ask 0.2.
        // ETH, USDT - bid 0,3 / ask 0.3.
        tickerFlux.update();
        tickerFlux.update();

        // =============================================================================================================
        // Step 1 - Creates position 1 (ETH/BTC, 0.0001, 100% stop gain, price of 0.2).
        // As the order is validated and the trade arrives, the position should be opened.
        final PositionCreationResultDTO position1Result = positionService.createPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(100f).create());
        assertTrue(position1Result.isSuccessful());
        assertEquals("DRY_ORDER_000000001", position1Result.getOrderId());
        final long position1Id = position1Result.getPositionId();

        // After position creation, its status is OPENING
        // Few seconds after, order and trade will arrive and status will be OPENED.
        // Two updates will have been received.
        assertEquals(OPENING, getPositionDTO(position1Id).getStatus());
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));
        assertEquals(OPENED, strategy.getPositionsUpdateReceived().get(1).getStatus());

        // =============================================================================================================
        // Step 2 - Creates position 2 (ETH/BTC, 0.0002, 20% stop loss, price of 0.2).
        // As the order is validated and the trade arrives, the position should be opened.
        final PositionCreationResultDTO position2Result = positionService.createPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).create());
        assertTrue(position2Result.isSuccessful());
        assertEquals("DRY_ORDER_000000002", position2Result.getOrderId());
        final long position2Id = position2Result.getPositionId();

        // After position creation, its status is OPENING
        // Few seconds after, order and trade will arrive and status will be OPENED.
        // Two updates will have been received.
        assertEquals(OPENING, getPositionDTO(position2Id).getStatus());
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsUpdateReceived().size()));
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position2Id).getStatus()));
        assertEquals(OPENED, strategy.getPositionsUpdateReceived().get(3).getStatus());

        // =============================================================================================================
        // Tickers are coming.

        // Second tickers - cp1 & cp2.
        // ETH, BTC - bid 0.2 / ask 0.3 - 50% gain.
        // ETH, USDT - bid 0,3 / ask 0.3 - no gain.
        // No change.
        tickerFlux.update();
        tickerFlux.update();
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        assertEquals(OPENED, getPositionDTO(position1Id).getStatus());
        assertEquals(OPENED, getPositionDTO(position2Id).getStatus());

        // Third tickers - cp1 & cp2.
        // ETH, BTC - bid 0.2 / ask 0.4 - 100% gain.
        // ETH, USDT - bid 0,3 / ask 0.6 - 100% gain.
        // No change.
        tickerFlux.update();
        tickerFlux.update();
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        assertEquals(CLOSED, getPositionDTO(position1Id).getStatus());
        assertEquals(OPENED, getPositionDTO(position2Id).getStatus());

        // Third tickers - cp1 & cp2.
        // ETH, BTC - bid 0.2 / ask 0.4 - 100% gain.
        // ETH, USDT - bid 0,3 / ask 0.1 - 70% loss.
        // No change.
        tickerFlux.update();
        tickerFlux.update();
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        assertEquals(CLOSED, getPositionDTO(position1Id).getStatus());
        assertEquals(CLOSED, getPositionDTO(position2Id).getStatus());

        // Check everything arrived.
        assertEquals(8, strategy.getPositionsStatusUpdateReceived().size());
    }

    /**
     * Retrieve position from database.
     * @param id position id
     * @return position
     */
    private PositionDTO getPositionDTO(final long id) {
        final Optional<PositionDTO> p = positionService.getPositionById(id);
        if (p.isPresent()) {
            return p.get();
        } else {
            throw new RuntimeException("Position not found : " + id);
        }
    }

}
