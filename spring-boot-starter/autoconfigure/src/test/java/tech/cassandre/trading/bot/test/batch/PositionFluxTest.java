package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.batch.mocks.PositionFluxTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USD;

@SpringBootTest
@DisplayName("Batch - Position flux")
@Configuration({
        @Property(key = "TEST_NAME", value = "Batch - Position flux")
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(PositionFluxTestMock.class)
public class PositionFluxTest extends BaseTest {

    public static final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

    public static final CurrencyPairDTO cp2 = new CurrencyPairDTO(USD, BTC);

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private PositionService positionService;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        assertEquals(0, strategy.getPositionsUpdateReceived().size());
        int positionUpdateIndex = 0;

        // Creates position 1.
        final PositionCreationResultDTO position1Result = positionService.createPosition(cp1,
                new BigDecimal("10"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1000)   // 1 000% max gain.
                        .stopLossPercentage(100)    // 100% max lost.
                        .create());

        // onPositionUpdate - Position 1 should arrive.
        await().untilAsserted(() -> assertEquals(1, strategy.getPositionsStatusUpdateReceived().size()));
        PositionDTO p = strategy.getPositionsStatusUpdateReceived().get(0);
        assertNotNull(p);
        assertEquals(1, p.getId());
        assertEquals(OPENING, p.getStatus());

        // onPosition - Position 1 should arrive.
        await().untilAsserted(() -> assertEquals(1, strategy.getPositionsUpdateReceived().size()));
        assertNotNull(strategy.getPositionsUpdateReceived().get(positionUpdateIndex));
        assertEquals(position1Result.getPositionId(), strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getId());
        assertEquals(OPENING, strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getStatus());

        // Creates positions 2.
        final PositionCreationResultDTO position2Result = positionService.createPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(10000000)
                        .stopLossPercentage(10000000)
                        .create());

        // onPositionUpdate - Position 2 should arrive.
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsStatusUpdateReceived().size()));
        p = strategy.getPositionsStatusUpdateReceived().get(1);
        assertNotNull(p);
        assertEquals(2, p.getId());
        assertEquals(OPENING, p.getStatus());

        // onPosition - Position 2 should arrive.
        positionUpdateIndex++;
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdateReceived().size()));
        assertNotNull(strategy.getPositionsUpdateReceived().get(positionUpdateIndex));
        assertEquals(position2Result.getPositionId(), strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getId());
        assertEquals(OPENING, strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getStatus());

        // Trade arrives, position 1 will be opened.
        tradeFlux.emitValue(TradeDTO.builder().id("000001")
                .orderId("ORDER00010")
                .currencyPair(cp1)
                .originalAmount(new BigDecimal("10"))
                .price(new BigDecimal("0.03"))
                .create());

        // onPositionUpdate - Position 2 should arrive.
        await().untilAsserted(() -> assertEquals(3, strategy.getPositionsStatusUpdateReceived().size()));
        p = strategy.getPositionsStatusUpdateReceived().get(2);
        assertNotNull(p);
        assertEquals(1, p.getId());
        assertEquals(OPENED, p.getStatus());

        // onPosition.
        positionUpdateIndex++;
        await().untilAsserted(() -> assertEquals(3, strategy.getPositionsUpdateReceived().size()));
        assertNotNull(strategy.getPositionsUpdateReceived().get(positionUpdateIndex));
        assertEquals(position1Result.getPositionId(), strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getId());
        assertEquals(OPENED, strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getStatus());

        // First ticker arrives (500% gain) - min and max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.18")).create());
        positionUpdateIndex++;
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsUpdateReceived().size()));
        assertEquals(position1Result.getPositionId(), strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getId());

        // Second ticker arrives (100% gain) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.06")).create());
        positionUpdateIndex++;
        await().untilAsserted(() -> assertEquals(5, strategy.getPositionsUpdateReceived().size()));
        assertEquals(position1Result.getPositionId(), strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getId());

        // Third ticker arrives (200% gain) - nothing should change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.09")).create());

        // Fourth ticker arrives (50% loss) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.015")).create());
        positionUpdateIndex++;
        await().untilAsserted(() -> assertEquals(6, strategy.getPositionsUpdateReceived().size()));
        assertEquals(position1Result.getPositionId(), strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getId());

        // Firth ticker arrives (600% gain) - max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.21")).create());
        positionUpdateIndex++;
        await().untilAsserted(() -> assertEquals(7, strategy.getPositionsUpdateReceived().size()));
        assertEquals(position1Result.getPositionId(), strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getId());
        assertEquals(OPENED, strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getStatus());

        // Trade arrives to open position 2
        tradeFlux.emitValue(TradeDTO.builder().id("000002")
                .orderId("ORDER00020")
                .currencyPair(cp2)
                .originalAmount(new BigDecimal("10"))
                .price(new BigDecimal("0.03"))
                .create());

        // onPositionUpdate - Position 2 should be opened.
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsStatusUpdateReceived().size()));
        p = strategy.getPositionsStatusUpdateReceived().get(3);
        assertNotNull(p);
        assertEquals(2, p.getId());
        assertEquals(OPENED, p.getStatus());

        // onPosition.
        positionUpdateIndex++;
        await().untilAsserted(() -> assertEquals(8, strategy.getPositionsUpdateReceived().size()));
        assertEquals(position2Result.getPositionId(), strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getId());
        assertEquals(OPENED, strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getStatus());

        // Closing the trade.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("100")).create());
        positionUpdateIndex++;
        await().untilAsserted(() -> assertEquals(9, strategy.getPositionsUpdateReceived().size()));
        assertEquals(position1Result.getPositionId(), strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getId());
        assertEquals(CLOSING, strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getStatus());

        // onPositionUpdate - Position should be closing.
        await().untilAsserted(() -> assertEquals(5, strategy.getPositionsStatusUpdateReceived().size()));
        p = strategy.getPositionsStatusUpdateReceived().get(4);
        assertNotNull(p);
        assertEquals(1, p.getId());
        assertEquals(CLOSING, p.getStatus());

        // The close trade arrives, change the status and set the price.
        tradeFlux.emitValue(TradeDTO.builder().id("000003")
                .orderId("ORDER00011")
                .originalAmount(new BigDecimal("10"))
                .currencyPair(cp1)
                .price(new BigDecimal("1"))
                .create());
        positionUpdateIndex++;
        await().untilAsserted(() -> assertEquals(10, strategy.getPositionsUpdateReceived().size()));
        assertEquals(position1Result.getPositionId(), strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getId());
        assertEquals(CLOSED, strategy.getPositionsUpdateReceived().get(positionUpdateIndex).getStatus());

        // Position 1 should be closed.
        await().untilAsserted(() -> assertEquals(6, strategy.getPositionsStatusUpdateReceived().size()));
        p = strategy.getPositionsStatusUpdateReceived().get(5);
        assertNotNull(p);
        assertEquals(1, p.getId());
        assertEquals(CLOSED, p.getStatus());
    }

}
