package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.test.batch.mocks.PositionLongFluxTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.LONG;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Batch - Long position flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@Import(PositionLongFluxTestMock.class)
public class PositionLongFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // =============================================================================================================
        // Creates position 1 - should be OPENING.
        final PositionCreationResultDTO position1Result = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("10"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1000f)   // 1 000% max gain.
                        .stopLossPercentage(100f)    // 100% max lost.
                        .build());
        assertEquals("ORDER00010", position1Result.getPosition().getOpeningOrder().getOrderId());
        long position1Id = position1Result.getPosition().getId();

        // onPositionStatusUpdate - Position 1 should arrive (OPENING).
        // 1 position status update - The position is created with the OPENING status.
        await().untilAsserted(() -> assertEquals(1, getPositionsStatusUpdatesCount()));
        PositionDTO p = getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(OPENING, p.getStatus());

        // onPositionUpdate - Position 1 should arrive (OPENING).
        // 2 positions updates:
        // - Position created with the local order (status PENDING_NEW).
        // - Position updated with the distant order (status NEW).
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdatesReceived().size()));
        p = getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(OPENING, p.getStatus());

        // Check data we have in strategy & database.
        assertEquals(1, positionRepository.count());
        assertEquals(1, strategy.getPositions().size());
        Optional<PositionDTO> p1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(p1.isPresent());
        assertEquals(1, p1.get().getId());
        assertEquals(1, p1.get().getPositionId());
        assertEquals(LONG, p1.get().getType());
        assertNotNull(p1.get().getStrategy());
        assertEquals(1, p1.get().getStrategy().getId());
        assertEquals("01", p1.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, p1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), p1.get().getAmount().getCurrency());
        assertTrue(p1.get().getRules().isStopGainPercentageSet());
        assertEquals(1000f, p1.get().getRules().getStopGainPercentage());
        assertTrue(p1.get().getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.get().getRules().getStopLossPercentage());
        assertEquals(OPENING, p1.get().getStatus());
        assertEquals("ORDER00010", p1.get().getOpeningOrder().getOrderId());
        assertEquals("ORDER00010", p1.get().getOpeningOrder().getOrderId());
        assertTrue(p1.get().getOpeningOrder().getTrades().isEmpty());
        assertNull(p1.get().getClosingOrder());
        assertNull(p1.get().getLowestGainPrice());
        assertNull(p1.get().getHighestGainPrice());
        assertNull(p1.get().getLatestGainPrice());

        // =============================================================================================================
        // Creates positions 2 - should be OPENING.
        final PositionCreationResultDTO position2Result = strategy.createLongPosition(ETH_USDT,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(10000000f)
                        .stopLossPercentage(10000000f)
                        .build());
        assertEquals("ORDER00020", position2Result.getPosition().getOpeningOrder().getOrderId());
        long position2Id = position2Result.getPosition().getId();

        // onPositionStatusUpdate - Position 2 should arrive (OPENING).
        // 1 position status update - The position is created with the OPENING status.
        await().untilAsserted(() -> assertEquals(2, getPositionsStatusUpdatesCount()));
        p = getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position2Id, p.getId());
        assertEquals(OPENING, p.getStatus());

        // onPositionUpdate - Position 2 should arrive (OPENING).
        // - Position created with the local order (status PENDING_NEW).
        // - Position updated with the distant order (status NEW).
        await().untilAsserted(() -> assertEquals(4, getPositionsUpdatesCount()));
        p = getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position2Id, p.getId());
        assertEquals(OPENING, p.getStatus());

        // Check data we have in strategy & database.
        assertEquals(2, positionRepository.count());
        assertEquals(2, strategy.getPositions().size());
        Optional<PositionDTO> p2 = strategy.getPositionByPositionId(position2Id);
        assertTrue(p2.isPresent());
        assertEquals(2, p2.get().getId());
        assertEquals(2, p2.get().getPositionId());
        assertEquals(LONG, p2.get().getType());
        assertNotNull(p2.get().getStrategy());
        assertEquals(1, p2.get().getStrategy().getId());
        assertEquals("01", p2.get().getStrategy().getStrategyId());
        assertEquals(ETH_USDT, p2.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0002").compareTo(p2.get().getAmount().getValue()));
        assertEquals(ETH_USDT.getBaseCurrency(), p2.get().getAmount().getCurrency());
        assertTrue(p2.get().getRules().isStopGainPercentageSet());
        assertEquals(10000000f, p2.get().getRules().getStopGainPercentage());
        assertTrue(p2.get().getRules().isStopLossPercentageSet());
        assertEquals(10000000f, p2.get().getRules().getStopLossPercentage());
        assertEquals(OPENING, p2.get().getStatus());
        assertEquals("ORDER00020", p2.get().getOpeningOrder().getOrderId());
        assertTrue(p2.get().getOpeningOrder().getTrades().isEmpty());
        assertNull(p2.get().getClosingOrder());
        assertNull(p2.get().getLowestGainPrice());
        assertNull(p2.get().getHighestGainPrice());
        assertNull(p2.get().getLatestGainPrice());

        // =============================================================================================================
        // As the two trades expected by position 1 arrives, position 1 should now be OPENED.
        // 11 is before 1 to test the timestamp order of getOpenTrades & getCloseTrades.

        // First trade.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000011")
                .type(BID)
                .orderId("ORDER00010")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.02", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("02-02-2020"))
                .build());
        // The same trade is emitted two times with an update (on timestamp).
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000011")
                .orderId("ORDER00010")
                .type(BID)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.02", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("03-02-2020"))
                .build());

        // Second trade.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000001")
                .orderId("ORDER00010")
                .type(BID)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.04", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("01-01-2020"))
                .build());
        // The same trade is emitted two times with an update (on timestamp).
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000001")
                .orderId("ORDER00010")
                .type(BID)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.04", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("02-01-2020"))
                .build());

        // onPositionStatusUpdate - Position 1 should change to OPENED.
        // With the two trades emitted, status should change to OPENED.
        await().untilAsserted(() -> assertEquals(3, getPositionsStatusUpdatesCount()));
        p = getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(OPENED, p.getStatus());

        // onPositionUpdate - 2 trades emitted 2 times so 4 updates (+4 already received for position opening).
        await().untilAsserted(() -> assertEquals(8, getPositionsUpdatesCount()));
        p = getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(OPENED, p.getStatus());

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(p1.isPresent());
        assertEquals(1, p1.get().getId());
        assertEquals(1, p1.get().getPositionId());
        assertEquals(LONG, p1.get().getType());
        assertNotNull(p1.get().getStrategy());
        assertEquals(1, p1.get().getStrategy().getId());
        assertEquals("01", p1.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, p1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), p1.get().getAmount().getCurrency());
        assertTrue(p1.get().getRules().isStopGainPercentageSet());
        assertEquals(1000f, p1.get().getRules().getStopGainPercentage());
        assertTrue(p1.get().getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.get().getRules().getStopLossPercentage());
        assertEquals(OPENED, p1.get().getStatus());
        assertEquals("ORDER00010", p1.get().getOpeningOrder().getOrderId());
        Iterator<TradeDTO> openingTradesIterator = p1.get().getOpeningOrder().getTrades().iterator();
        assertEquals("000001", openingTradesIterator.next().getTradeId());
        assertEquals("000011", openingTradesIterator.next().getTradeId());
        assertNull(p1.get().getClosingOrder());
        assertNull(p1.get().getLowestGainPrice());
        assertNull(p1.get().getHighestGainPrice());
        assertNull(p1.get().getLatestGainPrice());

        // Check if we don't have duplicated trades in database !
        assertEquals(2, tradeRepository.count());
        Optional<Order> order00010 = orderRepository.findByOrderId("ORDER00010");
        assertTrue(order00010.isPresent());
        assertEquals(2, order00010.get().getTrades().size());

        // =============================================================================================================
        // Test of tickers updating the position 1.

        // First ticker arrives (500% gain) - min, max and last gain should be set to that value.
        // Price update so a new position update.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.18")).build());
        await().untilAsserted(() -> assertEquals(9, getPositionsUpdatesCount()));
        p = getLastPositionUpdate();
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getLatestGainPrice().getValue()));

        // We check the gain.
        Optional<GainDTO> latestCalculatedGain = p.getLatestCalculatedGain();
        assertTrue(latestCalculatedGain.isPresent());
        assertEquals(500, latestCalculatedGain.get().getPercentage());
        assertEquals(0, new BigDecimal("1.5").compareTo(latestCalculatedGain.get().getAmount().getValue()));
        assertEquals(BTC, latestCalculatedGain.get().getAmount().getCurrency());

        // Second ticker arrives (100% gain) - min and last gain should be set to that value.
        // Price update so a new position update.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.06")).build());
        await().untilAsserted(() -> assertEquals(10, getPositionsUpdatesCount()));
        p = getLastPositionUpdate();
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.06").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.06").compareTo(p.getLatestGainPrice().getValue()));

        // Third ticker arrives (200% gain) - only last should change.
        // Price update so a new position update.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.09")).build());
        await().untilAsserted(() -> assertEquals(11, getPositionsUpdatesCount()));
        p = getLastPositionUpdate();
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.06").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.09").compareTo(p.getLatestGainPrice().getValue()));

        // Fourth ticker arrives (50% loss) - min and last gain should be set to that value.
        // Price update so a new position update.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.015")).build());
        await().untilAsserted(() -> assertEquals(12, getPositionsUpdatesCount()));
        p = getLastPositionUpdate();
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.015").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.18").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.015").compareTo(p.getLatestGainPrice().getValue()));

        // A ticker arrive for another cp. Nothing should change.
        // And no position update.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("100")).build());

        // Firth ticker arrives (600% gain) - max and last gain should be set to that value.
        // Price update so a new position update.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.21")).build());
        await().untilAsserted(() -> assertEquals(14, getPositionsUpdatesCount()));
        p = getLastPositionUpdate();
        assertEquals(position1Id, p.getId());
        assertEquals(0, new BigDecimal("0.015").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p.getLatestGainPrice().getValue()));

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(p1.isPresent());
        assertEquals(1, p1.get().getId());
        assertEquals(1, p1.get().getPositionId());
        assertEquals(LONG, p1.get().getType());
        assertNotNull(p1.get().getStrategy());
        assertEquals(1, p1.get().getStrategy().getId());
        assertEquals("01", p1.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, p1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), p1.get().getAmount().getCurrency());
        assertTrue(p1.get().getRules().isStopGainPercentageSet());
        assertEquals(1000f, p1.get().getRules().getStopGainPercentage());
        assertTrue(p1.get().getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.get().getRules().getStopLossPercentage());
        assertEquals(OPENED, p1.get().getStatus());
        assertEquals("ORDER00010", p1.get().getOpeningOrder().getOrderId());
        openingTradesIterator = p1.get().getOpeningOrder().getTrades().iterator();
        assertEquals("000001", openingTradesIterator.next().getTradeId());
        assertEquals("000011", openingTradesIterator.next().getTradeId());
        assertNull(p1.get().getClosingOrder());
        assertEquals(0, new BigDecimal("0.015").compareTo(p1.get().getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p1.get().getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p1.get().getLatestGainPrice().getValue()));

        // =============================================================================================================
        // Trade arrives for position 2 - should now be OPENED
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000002")
                .type(BID)
                .orderId("ORDER00020")
                .currencyPair(ETH_USDT)
                .amount(new CurrencyAmountDTO("0.0002", ETH_USDT.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.03", ETH_USDT.getQuoteCurrency()))
                .build());

        // onPositionStatusUpdate - Position 2 should be opened.
        // As it's now CLOSED, we should have received a position update
        await().untilAsserted(() -> assertEquals(4, getPositionsStatusUpdatesCount()));
        p = getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position2Id, p.getId());
        assertEquals(OPENED, p.getStatus());

        // onPositionUpdate.
        // One trade arrives so we have a position update.
        await().untilAsserted(() -> assertEquals(15, getPositionsUpdatesCount()));
        p = getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position2Id, p.getId());
        assertEquals(OPENED, p.getStatus());

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p2 = strategy.getPositionByPositionId(position2Id);
        assertTrue(p2.isPresent());
        assertEquals(2, p2.get().getId());
        assertEquals(2, p2.get().getPositionId());
        assertEquals(LONG, p2.get().getType());
        assertNotNull(p2.get().getStrategy());
        assertEquals(1, p2.get().getStrategy().getId());
        assertEquals("01", p2.get().getStrategy().getStrategyId());
        assertEquals(ETH_USDT, p2.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0002").compareTo(p2.get().getAmount().getValue()));
        assertEquals(ETH_USDT.getBaseCurrency(), p2.get().getAmount().getCurrency());
        assertTrue(p2.get().getRules().isStopGainPercentageSet());
        assertEquals(10000000f, p2.get().getRules().getStopGainPercentage());
        assertTrue(p2.get().getRules().isStopLossPercentageSet());
        assertEquals(10000000f, p2.get().getRules().getStopLossPercentage());
        assertEquals(OPENED, p2.get().getStatus());
        assertEquals("ORDER00020", p2.get().getOpeningOrder().getOrderId());
        openingTradesIterator = p2.get().getOpeningOrder().getTrades().iterator();
        assertEquals("000002", openingTradesIterator.next().getTradeId());
        assertNull(p2.get().getClosingOrder());
        assertNull(p2.get().getLowestGainPrice());
        assertNull(p2.get().getHighestGainPrice());
        assertNull(p2.get().getLatestGainPrice());

        // =============================================================================================================
        // A ticker arrives that triggers max gain rules of position 1 - should now be CLOSING.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("100")).build());

        // onPositionStatusUpdate - Position should be closing.
        // As position should now be CLOSING, we have a status update.
        await().untilAsserted(() -> assertEquals(5, getPositionsStatusUpdatesCount()));
        p = getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSING, p.getStatus());

        // OnPositionUpdate.
        // - Position closed with the local order (status PENDING_NEW).
        // - Position updated with the distant order (status NEW).
        await().untilAsserted(() -> assertEquals(17, getPositionsUpdatesCount()));
        p = getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSING, p.getStatus());

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(p1.isPresent());
        assertEquals(1, p1.get().getId());
        assertEquals(1, p1.get().getPositionId());
        assertEquals(LONG, p1.get().getType());
        assertNotNull(p1.get().getStrategy());
        assertEquals(1, p1.get().getStrategy().getId());
        assertEquals("01", p1.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, p1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), p1.get().getAmount().getCurrency());
        assertTrue(p1.get().getRules().isStopGainPercentageSet());
        assertEquals(1000f, p1.get().getRules().getStopGainPercentage());
        assertTrue(p1.get().getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.get().getRules().getStopLossPercentage());
        assertEquals(CLOSING, p1.get().getStatus());
        assertEquals("ORDER00010", p1.get().getOpeningOrder().getOrderId());
        openingTradesIterator = p1.get().getOpeningOrder().getTrades().iterator();
        assertEquals("000001", openingTradesIterator.next().getTradeId());
        assertEquals("000011", openingTradesIterator.next().getTradeId());
        assertEquals("ORDER00011", p1.get().getClosingOrder().getOrderId());
        assertTrue(p1.get().getClosingOrder().getTrades().isEmpty());
        assertEquals(0, new BigDecimal("0.015").compareTo(p1.get().getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p1.get().getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("100").compareTo(p1.get().getLatestGainPrice().getValue()));

        // =============================================================================================================
        // Position 1 will have CLOSED status as the trade arrives.
        // The first close trade arrives but not enough.
        tradeFlux.emitValue(TradeDTO.builder().tradeId("000003")
                .orderId("ORDER00011")
                .type(ASK)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("1", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("01-01-2020"))
                .build());

        // We send a duplicated value.
        tradeFlux.emitValue(TradeDTO.builder().tradeId("000003")
                .orderId("ORDER00011")
                .type(ASK)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("1", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("01-01-2020"))
                .build());

        // onPosition for first trade arrival.
        // Two new updates : the two trades received (even if they were the same as we use emit method).
        await().untilAsserted(() -> assertEquals(19, getPositionsUpdatesCount()));
        p = getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSING, p.getStatus());

        // The second close trade arrives now closed.
        tradeFlux.emitValue(TradeDTO.builder().tradeId("000004")
                .orderId("ORDER00011")
                .type(ASK)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("1", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("02-01-2020"))
                .build());

        // We send a duplicated value.
        tradeFlux.emitValue(TradeDTO.builder().tradeId("000004")
                .orderId("ORDER00011")
                .type(ASK)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("2", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("02-01-2020").plusDays(1))
                .build());

        // onPositionStatusUpdate - Position should be closed.
        await().untilAsserted(() -> assertEquals(6, getPositionsStatusUpdatesCount()));
        p = getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSED, p.getStatus());

        // onPosition for second trade arrival.
        // Positions updates:
        // - Trade 000004 arrives. In one update we have one more trade and a status change.
        await().untilAsserted(() -> assertEquals(20, getPositionsUpdatesCount()));
        p = getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position1Id, p.getId());
        assertEquals(CLOSED, p.getStatus());

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositionByPositionId(position1Id);
        assertTrue(p1.isPresent());
        assertEquals(1, p1.get().getId());
        assertEquals(1, p1.get().getPositionId());
        assertEquals(LONG, p1.get().getType());
        assertNotNull(p1.get().getStrategy());
        assertEquals(1, p1.get().getStrategy().getId());
        assertEquals("01", p1.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, p1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), p1.get().getAmount().getCurrency());
        assertTrue(p1.get().getRules().isStopGainPercentageSet());
        assertEquals(1000f, p1.get().getRules().getStopGainPercentage());
        assertTrue(p1.get().getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.get().getRules().getStopLossPercentage());
        assertEquals(CLOSED, p1.get().getStatus());
        assertEquals("ORDER00010", p1.get().getOpeningOrder().getOrderId());
        openingTradesIterator = p1.get().getOpeningOrder().getTrades().iterator();
        assertEquals("000001", openingTradesIterator.next().getTradeId());
        assertEquals("000011", openingTradesIterator.next().getTradeId());
        assertEquals("ORDER00011", p1.get().getClosingOrder().getOrderId());
        final Iterator<TradeDTO> closingTradesIterator = p1.get().getClosingOrder().getTrades().iterator();
        assertEquals("000003", closingTradesIterator.next().getTradeId());
        assertEquals("000004", closingTradesIterator.next().getTradeId());
        assertEquals(0, new BigDecimal("0.015").compareTo(p1.get().getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.21").compareTo(p1.get().getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("100").compareTo(p1.get().getLatestGainPrice().getValue()));

        // Just checking trades creation.
        assertNotNull(strategy.getPositionByPositionId(position1Id));
        assertNotNull(strategy.getPositionByPositionId(position2Id));
        assertEquals(5, strategy.getTrades().size());

        // Check if we don't have duplicated trades in database !
        assertEquals(5, tradeRepository.count());
        order00010 = orderRepository.findByOrderId("ORDER00010");
        assertTrue(order00010.isPresent());
        assertEquals(2, order00010.get().getTrades().size());
        final Optional<Order> order00011 = orderRepository.findByOrderId("ORDER00011");
        assertTrue(order00011.isPresent());
        assertEquals(2, order00011.get().getTrades().size());
    }

    private int getPositionsUpdatesCount() {
        return strategy.getPositionsUpdatesReceived().size();
    }

    private int getPositionsStatusUpdatesCount() {
        return strategy.getPositionsStatusUpdatesReceived().size();
    }

    private PositionDTO getLastPositionUpdate() {
        return strategy.getPositionsUpdatesReceived().get(strategy.getPositionsUpdatesReceived().size() - 1);
    }

    private PositionDTO getLastPositionStatusUpdate() {
        return strategy.getPositionsStatusUpdatesReceived().get(strategy.getPositionsStatusUpdatesReceived().size() - 1);
    }

}
