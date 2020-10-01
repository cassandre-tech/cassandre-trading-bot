package tech.cassandre.trading.bot.test.modes.dry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.modes.dry.mocks.PositionServiceDryModeTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_DRY;


@SpringBootTest
@DisplayName("Dry mode - Position service")
@ActiveProfiles("schedule-disabled")
@Configuration({
        @Property(key = PARAMETER_DRY, value = "true")
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(PositionServiceDryModeTestMock.class)
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

    @Test
    @DisplayName("Check position lifecycle")
    public void checkPositionLifecycle() throws InterruptedException {
        // First tickers - cp1 & cp2.
        // ETH, BTC - bid 0.2 / ask 0.2.
        // ETH, USDT - bid 0,3 / ask 0.3.
        tickerFlux.update();
        tickerFlux.update();

        // Step 1 - Creates position 1 (ETH/BTC, 0.0001, 100% stop gain, price of 0.2).
        // As the order is validated and the trade arrives, the position should be opened.
        final PositionCreationResultDTO position01 = positionService.createPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(100).create());
        assertTrue(position01.isSuccessful());
        assertEquals(1, position01.getPositionId());
        assertEquals("DRY_ORDER_000000001", position01.getOrderId());
        await().untilAsserted(() -> assertTrue(positionService.getPositionById(1).isPresent()));
        await().untilAsserted(() -> assertEquals(OPENED, positionService.getPositionById(1).get().getStatus()));

        // Check position flux.
        positionFlux.update();
        await().untilAsserted(() -> assertEquals(1, strategy.getPositionsUpdateReceived().size()));
        assertNotNull(strategy.getPositionsUpdateReceived().get(0));
        assertEquals(1, strategy.getPositionsUpdateReceived().get(0).getId());
        assertEquals(OPENED, strategy.getPositionsUpdateReceived().get(0).getStatus());

        // Step 2 - Creates position 2 (ETH/BTC, 0.0002, 20% stop loss, price of 0.2).
        // As the order is validated and the trade arrives, the position should be opened.
        final PositionCreationResultDTO p2 = positionService.createPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20).create());
        assertTrue(p2.isSuccessful());
        assertEquals(2, p2.getPositionId());
        assertEquals("DRY_ORDER_000000002", p2.getOrderId());
        await().untilAsserted(() -> assertTrue(positionService.getPositionById(2).isPresent()));
        await().untilAsserted(() -> assertTrue(positionService.getPositionById(2).isPresent()));
        await().untilAsserted(() -> assertEquals(OPENED, positionService.getPositionById(2).get().getStatus()));

        // Check position flux.
        positionFlux.update();
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdateReceived().size()));
        assertNotNull(strategy.getPositionsUpdateReceived().get(1));
        assertEquals(2, strategy.getPositionsUpdateReceived().get(1).getId());
        assertEquals(OPENED, strategy.getPositionsUpdateReceived().get(1).getStatus());

        // Second tickers - cp1 & cp2.
        // ETH, BTC - bid 0.2 / ask 0.3 - 50% gain.
        // ETH, USDT - bid 0,3 / ask 0.3 - no gain.
        // No change.
        tickerFlux.update();
        tickerFlux.update();
        TimeUnit.SECONDS.sleep(TEN_SECONDS);
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENED, positionService.getPositionById(1).get().getStatus());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(OPENED, positionService.getPositionById(2).get().getStatus());

        // Third tickers - cp1 & cp2.
        // ETH, BTC - bid 0.2 / ask 0.4 - 100% gain.
        // ETH, USDT - bid 0,3 / ask 0.6 - 100% gain.
        // No change.
        tickerFlux.update();
        tickerFlux.update();
        TimeUnit.SECONDS.sleep(TEN_SECONDS);
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(CLOSED, positionService.getPositionById(1).get().getStatus());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(OPENED, positionService.getPositionById(2).get().getStatus());

        // Third tickers - cp1 & cp2.
        // ETH, BTC - bid 0.2 / ask 0.4 - 100% gain.
        // ETH, USDT - bid 0,3 / ask 0.1 - 70% loss.
        // No change.
        tickerFlux.update();
        tickerFlux.update();
        TimeUnit.SECONDS.sleep(TEN_SECONDS);
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(CLOSED, positionService.getPositionById(1).get().getStatus());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(CLOSED, positionService.getPositionById(2).get().getStatus());

        // Check position flux.
        positionFlux.update();
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsUpdateReceived().size()));
        assertNotNull(strategy.getPositionsUpdateReceived().get(2));
        assertEquals(1, strategy.getPositionsUpdateReceived().get(2).getId());
        assertEquals(CLOSED, strategy.getPositionsUpdateReceived().get(2).getStatus());
        assertNotNull(strategy.getPositionsUpdateReceived().get(3));
        assertEquals(2, strategy.getPositionsUpdateReceived().get(3).getId());
        assertEquals(CLOSED, strategy.getPositionsUpdateReceived().get(3).getStatus());
    }

}
