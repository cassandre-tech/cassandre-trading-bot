package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
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
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.batch.mocks.PositionFluxTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_TICKER;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_TRADE;

@SpringBootTest
@DisplayName("Batch - Position flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_RATE_TICKER, value = "100"),
        @Property(key = PARAMETER_EXCHANGE_RATE_TRADE, value = "100")
})
@DirtiesContext(classMode = AFTER_CLASS)
@Import(PositionFluxTestMock.class)
public class PositionFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private PositionService positionService;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Test
    @Tag("notReviewed")
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

        // Checking what we have in database.
        assertEquals(1, strategy.getPositions().size());
        PositionDTO p1 = strategy.getPositions().get(position1Id);
        assertNotNull(p1);
        assertEquals(1L, p1.getId());
        assertEquals(OPENING, p1.getStatus());
        assertEquals(cp1, p1.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.getAmount()));
        assertTrue(p1.getRules().isStopGainPercentageSet());
        assertEquals(1000f, p1.getRules().getStopGainPercentage());
        assertTrue(p1.getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.getRules().getStopLossPercentage());
        assertEquals("ORDER00010", p1.getOpenOrderId());
        assertTrue(p1.getOpenTrades().isEmpty());
        assertNull(p1.getCloseOrderId());
        assertTrue(p1.getCloseTrades().isEmpty());
        assertNull(p1.getLowestPrice());
        assertNull(p1.getHighestPrice());
        assertNull(p1.getLatestPrice());

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

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        PositionDTO p2 = strategy.getPositions().get(position2Id);
        assertNotNull(p2);
        assertEquals(2L, p2.getId());
        assertEquals(OPENING, p2.getStatus());
        assertEquals(cp2, p2.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0002").compareTo(p2.getAmount()));
        assertTrue(p2.getRules().isStopGainPercentageSet());
        assertEquals(10000000f, p2.getRules().getStopGainPercentage());
        assertTrue(p2.getRules().isStopLossPercentageSet());
        assertEquals(10000000f, p2.getRules().getStopLossPercentage());
        assertEquals("ORDER00020", p2.getOpenOrderId());
        assertTrue(p2.getOpenTrades().isEmpty());
        assertNull(p2.getCloseOrderId());
        assertTrue(p2.getCloseTrades().isEmpty());
        assertNull(p2.getLowestPrice());
        assertNull(p2.getHighestPrice());
        assertNull(p2.getLatestPrice());

        // =============================================================================================================
        // As the two trades expected by position 1 arrives, position 1 should now be OPENED.
        // 11 is before 1 to test the timestamp order of getOpenTrades & getCloseTrades.
        tradeFlux.emitValue(TradeDTO.builder().id("000011")
                .orderId("ORDER00010")
                .type(BID)
                .currencyPair(cp1)
                .originalAmount(new BigDecimal("4"))
                .price(new BigDecimal("0.03"))
                .create());
        positionUpdateIndex++;

        tradeFlux.emitValue(TradeDTO.builder().id("000001")
                .orderId("ORDER00010")
                .type(BID)
                .currencyPair(cp1)
                .originalAmount(new BigDecimal("6"))
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
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(OPENED, p.getStatus());
        positionUpdateIndex++;

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositions().get(position1Id);
        assertNotNull(p1);
        assertEquals(1L, p1.getId());
        assertEquals(OPENED, p1.getStatus());
        assertEquals(cp1, p1.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.getAmount()));
        assertTrue(p1.getRules().isStopGainPercentageSet());
        assertEquals(1000f, p1.getRules().getStopGainPercentage());
        assertTrue(p1.getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.getRules().getStopLossPercentage());
        assertEquals("ORDER00010", p1.getOpenOrderId());
        assertEquals(2, p1.getOpenTrades().size());
        assertNull(p1.getCloseOrderId());
        assertTrue(p1.getCloseTrades().isEmpty());
        assertNull(p1.getLowestPrice());
        assertNull(p1.getHighestPrice());
        assertNull(p1.getLatestPrice());
        // Check trade orders.
        Iterator<TradeDTO> openTradesIterator = p1.getOpenTrades().iterator();
        assertEquals("000011", openTradesIterator.next().getId());
        assertEquals("000001", openTradesIterator.next().getId());

        // =============================================================================================================
        // Test of tickers updating the position 1.

        // First ticker arrives (500% gain) - min, max and last gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.18")).create());
        await().untilAsserted(() -> assertEquals(5, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getLatestPrice()));
        positionUpdateIndex++;

        // Second ticker arrives (100% gain) - min and last gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.06")).create());
        await().untilAsserted(() -> assertEquals(6, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.06").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.06").compareTo(p.getLatestPrice()));
        positionUpdateIndex++;

        // Third ticker arrives (200% gain) - only last should change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.09")).create());
        await().untilAsserted(() -> assertEquals(7, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.06").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.09").compareTo(p.getLatestPrice()));
        positionUpdateIndex++;

        // Fourth ticker arrives (50% loss) - min and last gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.015")).create());
        await().untilAsserted(() -> assertEquals(8, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.015").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.015").compareTo(p.getLatestPrice()));
        positionUpdateIndex++;

        // Firth ticker arrives (600% gain) - max and last gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.21")).create());
        await().untilAsserted(() -> assertEquals(9, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.015").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p.getLatestPrice()));
        positionUpdateIndex++;

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositions().get(position1Id);
        assertNotNull(p1);
        assertEquals(1L, p1.getId());
        assertEquals(OPENED, p1.getStatus());
        assertEquals(cp1, p1.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.getAmount()));
        assertTrue(p1.getRules().isStopGainPercentageSet());
        assertEquals(1000f, p1.getRules().getStopGainPercentage());
        assertTrue(p1.getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.getRules().getStopLossPercentage());
        assertEquals("ORDER00010", p1.getOpenOrderId());
        assertEquals(2, p1.getOpenTrades().size());
        assertNull(p1.getCloseOrderId());
        assertTrue(p1.getCloseTrades().isEmpty());
        assertEquals(0, new BigDecimal("0.015").compareTo(p1.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p1.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p1.getLatestPrice()));
        // Check trade orders.
        openTradesIterator = p1.getOpenTrades().iterator();
        assertEquals("000011", openTradesIterator.next().getId());
        assertEquals("000001", openTradesIterator.next().getId());

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
        await().untilAsserted(() -> assertEquals(10, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertNotNull(p);
        assertEquals(position2Id, p.getId());
        assertEquals(OPENED, p.getStatus());
        positionUpdateIndex++;

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p2 = strategy.getPositions().get(position2Id);
        assertNotNull(p2);
        assertEquals(2L, p2.getId());
        assertEquals(OPENED, p2.getStatus());
        assertEquals(cp2, p2.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0002").compareTo(p2.getAmount()));
        assertTrue(p2.getRules().isStopGainPercentageSet());
        assertEquals(10000000f, p2.getRules().getStopGainPercentage());
        assertTrue(p2.getRules().isStopLossPercentageSet());
        assertEquals(10000000f, p2.getRules().getStopLossPercentage());
        assertEquals("ORDER00020", p2.getOpenOrderId());
        assertEquals(1, p2.getOpenTrades().size());
        assertNull(p2.getCloseOrderId());
        assertTrue(p2.getCloseTrades().isEmpty());
        assertNull(p2.getLowestPrice());
        assertNull(p2.getHighestPrice());
        assertNull(p2.getLatestPrice());
        // Check trade orders.
        openTradesIterator = p2.getOpenTrades().iterator();
        assertEquals("000002", openTradesIterator.next().getId());

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
        await().untilAsserted(() -> assertEquals(11, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSING, p.getStatus());
        positionUpdateIndex++;

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositions().get(position1Id);
        assertNotNull(p1);
        assertEquals(1L, p1.getId());
        assertEquals(CLOSING, p1.getStatus());
        assertEquals(cp1, p1.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.getAmount()));
        assertTrue(p1.getRules().isStopGainPercentageSet());
        assertEquals(1000f, p1.getRules().getStopGainPercentage());
        assertTrue(p1.getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.getRules().getStopLossPercentage());
        assertEquals("ORDER00010", p1.getOpenOrderId());
        assertEquals(2, p1.getOpenTrades().size());
        assertEquals("ORDER00011", p1.getCloseOrderId());
        assertTrue(p1.getCloseTrades().isEmpty());
        assertEquals(0, new BigDecimal("0.015").compareTo(p1.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p1.getHighestPrice()));
        assertEquals(0, new BigDecimal("100").compareTo(p1.getLatestPrice()));
        // Check trade orders.
        openTradesIterator = p1.getOpenTrades().iterator();
        assertEquals("000011", openTradesIterator.next().getId());
        assertEquals("000001", openTradesIterator.next().getId());

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
        await().untilAsserted(() -> assertEquals(12, strategy.getPositionsUpdateReceived().size()));
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
        await().untilAsserted(() -> assertEquals(13, strategy.getPositionsUpdateReceived().size()));
        p = strategy.getPositionsUpdateReceived().get(positionUpdateIndex);
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSED, p.getStatus());

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositions().get(position1Id);
        assertNotNull(p1);
        assertEquals(1L, p1.getId());
        assertEquals(CLOSED, p1.getStatus());
        assertEquals(cp1, p1.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.getAmount()));
        assertTrue(p1.getRules().isStopGainPercentageSet());
        assertEquals(1000f, p1.getRules().getStopGainPercentage());
        assertTrue(p1.getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.getRules().getStopLossPercentage());
        assertEquals("ORDER00010", p1.getOpenOrderId());
        assertEquals(2, p1.getOpenTrades().size());
        assertEquals("ORDER00011", p1.getCloseOrderId());
        assertEquals(2, p1.getCloseTrades().size());
        assertEquals(0, new BigDecimal("0.015").compareTo(p1.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p1.getHighestPrice()));
        assertEquals(0, new BigDecimal("100").compareTo(p1.getLatestPrice()));
        // Check trade orders.
        openTradesIterator = p1.getOpenTrades().iterator();
        assertEquals("000011", openTradesIterator.next().getId());
        assertEquals("000001", openTradesIterator.next().getId());
        final Iterator<TradeDTO> closeTradesIterator = p1.getCloseTrades().iterator();
        assertEquals("000003", closeTradesIterator.next().getId());
        assertEquals("000004", closeTradesIterator.next().getId());

        // Just checking trades creation.
        assertEquals(5, strategy.getTrades().size());
    }

}
