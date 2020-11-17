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
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USD;

@SpringBootTest
@DisplayName("Batch - Position flux")
@Configuration({
        @Property(key = "TEST_NAME", value = "Batch - Position flux")
})
@DirtiesContext(classMode = AFTER_CLASS)
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
        int positionStatusUpdateIndex = 0;
        int positionUpdateIndex = 0;

        // =============================================================================================================
        // Creates position 1 - should be OPENING.
        final PositionCreationResultDTO position1Result = positionService.createPosition(cp1,
                new BigDecimal("10"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1000f)   // 1 000% max gain.
                        .stopLossPercentage(100f)    // 100% max lost.
                        .create());
        assertEquals("ORDER00010", position1Result.getOrderId());
        long position1Id = position1Result.getPositionId();

        // onPositionUpdate - Position 1 should arrive.
        await().untilAsserted(() -> assertEquals(1, strategy.getPositionsStatusUpdateReceived().size()));
        PositionDTO p = strategy.getPositionsStatusUpdateReceived().get(positionStatusUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(OPENING, p.getStatus());
        positionStatusUpdateIndex++;
        // onPosition - Position 1 should arrive.
        await().untilAsserted(() -> assertEquals(1, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(OPENING, p.getStatus());
        positionUpdateIndex++;

        // =============================================================================================================
        // Creates positions 2 - should be OPENING.
        final PositionCreationResultDTO position2Result = positionService.createPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(10000000f)
                        .stopLossPercentage(10000000f)
                        .create());
        assertEquals("ORDER00020", position2Result.getOrderId());
        long position2Id = position2Result.getPositionId();

        // onPositionUpdate - Position 2 should arrive.
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsStatusUpdateReceived().size()));
        p = strategy.getPositionsStatusUpdateReceived().get(positionStatusUpdateIndex);
        assertNotNull(p);
        assertEquals(2, p.getId());
        assertEquals(OPENING, p.getStatus());
        positionStatusUpdateIndex++;
        // onPosition - Position 2 should arrive.
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertNotNull(p);
        assertEquals(position2Id, p.getId());
        assertEquals(OPENING, p.getStatus());
        positionUpdateIndex++;

        // =============================================================================================================
        // As the trade expected by position 1 arrives, position 1 should now be OPENED.
        tradeFlux.emitValue(TradeDTO.builder().id("000001")
                .orderId("ORDER00010")
                .type(BID)
                .currencyPair(cp1)
                .originalAmount(new BigDecimal("10"))
                .price(new BigDecimal("0.03"))
                .create());

        // onPositionUpdate - Position 1 should change.
        await().untilAsserted(() -> assertEquals(3, strategy.getPositionsStatusUpdateReceived().size()));
        p = strategy.getPositionsStatusUpdateReceived().get(positionStatusUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(OPENED, p.getStatus());
        positionStatusUpdateIndex++;
        // onPosition.
        await().untilAsserted(() -> assertEquals(3, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(OPENED, p.getStatus());
        positionUpdateIndex++;

        // =============================================================================================================
        // Test of tickers updating the position 1.

        // First ticker arrives (500% gain) - min, max and last gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.18")).create());
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getLatestPrice()));
        positionUpdateIndex++;

        // Second ticker arrives (100% gain) - min and last gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.06")).create());
        await().untilAsserted(() -> assertEquals(5, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.06").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.06").compareTo(p.getLatestPrice()));
        positionUpdateIndex++;

        // Third ticker arrives (200% gain) - only last should change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.09")).create());
        await().untilAsserted(() -> assertEquals(6, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.06").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.09").compareTo(p.getLatestPrice()));
        positionUpdateIndex++;

        // Fourth ticker arrives (50% loss) - min and last gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.015")).create());
        await().untilAsserted(() -> assertEquals(7, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.015").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.015").compareTo(p.getLatestPrice()));
        positionUpdateIndex++;

        // Firth ticker arrives (600% gain) - max and last gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.21")).create());
        await().untilAsserted(() -> assertEquals(8, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.015").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p.getLatestPrice()));
        positionUpdateIndex++;

        // =============================================================================================================
        // Trade arrives to open position 2 - should now be OPENED
        tradeFlux.emitValue(TradeDTO.builder().id("000002")
                .orderId("ORDER00020")
                .type(BID)
                .currencyPair(cp2)
                .originalAmount(new BigDecimal("0.0002"))
                .price(new BigDecimal("0.03"))
                .create());

        // onPositionUpdate - Position 2 should be opened.
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsStatusUpdateReceived().size()));
        p = strategy.getPositionsStatusUpdateReceived().get(positionStatusUpdateIndex);
        assertNotNull(p);
        assertEquals(position2Id, p.getId());
        assertEquals(OPENED, p.getStatus());
        positionStatusUpdateIndex++;
        // onPosition.
        await().untilAsserted(() -> assertEquals(9, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertNotNull(p);
        assertEquals(position2Id, p.getId());
        assertEquals(OPENED, p.getStatus());
        positionUpdateIndex++;

        // =============================================================================================================
        // A ticker arrives that triggers max gain rules of position 1 - should now be CLOSING.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("100")).create());
        // onPositionUpdate - Position should be closing.
        await().untilAsserted(() -> assertEquals(5, strategy.getPositionsStatusUpdateReceived().size()));
        p = strategy.getPositionsStatusUpdateReceived().get(positionStatusUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSING, p.getStatus());
        positionStatusUpdateIndex++;
        // onPosition.
        await().untilAsserted(() -> assertEquals(10, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSING, p.getStatus());
        positionUpdateIndex++;

        // =============================================================================================================
        // Position 1 will have CLOSED close status as the trade arrives.
        // The first close trade arrives but not enough.
        tradeFlux.emitValue(TradeDTO.builder().id("000003")
                .orderId("ORDER00011")
                .type(ASK)
                .currencyPair(cp1)
                .originalAmount(new BigDecimal("5"))
                .price(new BigDecimal("1"))
                .create());

        // onPosition for first trade arrival.
        await().untilAsserted(() -> assertEquals(11, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSING, p.getStatus());
        positionUpdateIndex++;

        // The second close trade arrives now closed.
        tradeFlux.emitValue(TradeDTO.builder().id("000004")
                .orderId("ORDER00011")
                .type(ASK)
                .currencyPair(cp1)
                .originalAmount(new BigDecimal("5"))
                .price(new BigDecimal("1"))
                .create());

        // onPositionUpdate - Position should be closed.
        await().untilAsserted(() -> assertEquals(6, strategy.getPositionsStatusUpdateReceived().size()));
        p = strategy.getPositionsStatusUpdateReceived().get(positionStatusUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSED, p.getStatus());

        // onPosition for second trade arrival.
        await().untilAsserted(() -> assertEquals(12, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSED, p.getStatus());
    }

}
