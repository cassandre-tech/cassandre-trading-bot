package tech.cassandre.trading.bot.test.core.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
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
import tech.cassandre.trading.bot.test.core.batch.mocks.PositionShortFluxTestMock;
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
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.SHORT;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Batch - Short position flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@Import(PositionShortFluxTestMock.class)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class PositionShortFluxTest extends BaseTest {

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

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // =============================================================================================================
        // Creates short position n°1 - 10 ETH sold for BTC.
        // Position will be closed if 1 000% gain or 100% loss.
        final PositionCreationResultDTO position1Result = strategy.createShortPosition(ETH_BTC,
                new BigDecimal("10"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1_000f)   // 1 000% max gain.
                        .stopLossPercentage(100f)    // 100% max lost.
                        .build());
        assertEquals("ORDER00010", position1Result.getPosition().getOpeningOrder().getOrderId());
        long position1Uid = position1Result.getPosition().getUid();

        // onPositionStatusUpdate - Position 1 should arrive (OPENING).
        // 1 position status update:
        // - The position is created with the OPENING status.
        await().untilAsserted(() -> assertEquals(1, strategy.getPositionsStatusUpdatesCount()));
        PositionDTO p = strategy.getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position1Uid, p.getUid());
        assertEquals(OPENING, p.getStatus());
        assertEquals("Short position n°1 of 10 ETH (rules: 1000.0 % gain / 100.0 % loss) - Opening - Waiting for the trades of order ORDER00010", p.getDescription());

        // onPositionUpdate - Position 1 should arrive (OPENING).
        // 2 positions updates:
        // - Position created with a local order saved in database (Order with status PENDING_NEW).
        // - Position updated with the local order retrieved from getOrders with status NEW.
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsUpdatesReceived().size()));
        p = strategy.getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position1Uid, p.getUid());
        assertEquals(OPENING, p.getStatus());

        // Check data in strategy & database.
        assertEquals(1, positionRepository.count());
        assertEquals(1, strategy.getPositions().size());
        Optional<PositionDTO> p1 = strategy.getPositionByPositionId(position1Uid);
        assertTrue(p1.isPresent());
        assertEquals(1, p1.get().getUid());
        assertEquals(1, p1.get().getPositionId());
        assertEquals(SHORT, p1.get().getType());
        assertNotNull(p1.get().getStrategy());
        assertEquals(1, p1.get().getStrategy().getUid());
        assertEquals("01", p1.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, p1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), p1.get().getAmount().getCurrency());
        assertTrue(p1.get().getRules().isStopGainPercentageSet());
        assertEquals(1_000f, p1.get().getRules().getStopGainPercentage());
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
        // Creates short position n°2 - 0.0002 ETH sold for BTC.
        // Position will be closed if 10 000% gain or 10 000% loss.
        final PositionCreationResultDTO position2Result = strategy.createShortPosition(ETH_USDT,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(10_000f)
                        .stopLossPercentage(10_000f)
                        .build());
        assertEquals("ORDER00020", position2Result.getPosition().getOpeningOrder().getOrderId());
        long position2Id = position2Result.getPosition().getUid();

        // onPositionStatusUpdate - Position 2 should arrive (OPENING).
        // 1 more position status update - Position 2 is created with the OPENING status.
        await().untilAsserted(() -> assertEquals(2, strategy.getPositionsStatusUpdatesCount()));
        p = strategy.getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position2Id, p.getUid());
        assertEquals(OPENING, p.getStatus());
        assertEquals("Short position n°2 of 0.0002 ETH (rules: 10000.0 % gain / 10000.0 % loss) - Opening - Waiting for the trades of order ORDER00020", p.getDescription());

        // onPositionUpdate - Position 2 should arrive (OPENING).
        // - Position created with a local order saved in database (Order with status PENDING_NEW).
        // - Position updated with the local order retrieved from getOrders with status NEW.
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsUpdatesCount()));
        p = strategy.getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position2Id, p.getUid());
        assertEquals(OPENING, p.getStatus());

        // Check data in strategy & database.
        assertEquals(2, positionRepository.count());
        assertEquals(2, strategy.getPositions().size());
        Optional<PositionDTO> p2 = strategy.getPositionByPositionId(position2Id);
        assertTrue(p2.isPresent());
        assertEquals(2, p2.get().getUid());
        assertEquals(2, p2.get().getPositionId());
        assertEquals(SHORT, p2.get().getType());
        assertNotNull(p2.get().getStrategy());
        assertEquals(1, p2.get().getStrategy().getUid());
        assertEquals("01", p2.get().getStrategy().getStrategyId());
        assertEquals(ETH_USDT, p2.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0002").compareTo(p2.get().getAmount().getValue()));
        assertEquals(ETH_USDT.getBaseCurrency(), p2.get().getAmount().getCurrency());
        assertTrue(p2.get().getRules().isStopGainPercentageSet());
        assertEquals(10_000f, p2.get().getRules().getStopGainPercentage());
        assertTrue(p2.get().getRules().isStopLossPercentageSet());
        assertEquals(10_000f, p2.get().getRules().getStopLossPercentage());
        assertEquals(OPENING, p2.get().getStatus());
        assertEquals("ORDER00020", p2.get().getOpeningOrder().getOrderId());
        assertTrue(p2.get().getOpeningOrder().getTrades().isEmpty());
        assertNull(p2.get().getClosingOrder());
        assertNull(p2.get().getLowestGainPrice());
        assertNull(p2.get().getHighestGainPrice());
        assertNull(p2.get().getLatestGainPrice());

        // =============================================================================================================
        // Position n°1 is selling 10 ETH for BTC.
        // Two trades arrives with 5 ETH sold each (so the two makes 10 ETH).
        // 11 is before 1 to test the timestamp order of getOpenTrades & getCloseTrades.

        // First trade.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000011")
                .type(ASK)
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
                .type(ASK)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.02", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("03-02-2020"))
                .build());

        // Second trade.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000001")
                .orderId("ORDER00010")
                .type(ASK)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.04", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("01-01-2020"))
                .build());
        // The same trade is emitted two times with an update (on time).
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000001")
                .orderId("ORDER00010")
                .type(ASK)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.04", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("02-01-2020"))
                .build());

        // onPositionStatusUpdate - Position 1 should change from OPENING to OPENED.
        // With the two trades emitted, status should change to OPENED.
        await().untilAsserted(() -> assertEquals(3, strategy.getPositionsStatusUpdatesCount()));
        p = strategy.getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position1Uid, p.getUid());
        assertEquals(OPENED, p.getStatus());
        assertEquals("Short position n°1 of 10 ETH (rules: 1000.0 % gain / 100.0 % loss) - Opened", p.getDescription());

        // onPositionUpdate - 2 trades emitted 2 times so 4 updates (+4 already received for position opening).
        // We were at 4 first.
        // Trade 000011 arrives with 5 ETH.
        // Trade 000011 arrives with timestamp updated.
        // Trade 000001 arrives with 5 ETH.
        // Trade 000001 arrives with timestamp updated.
        // Now we have 8 updates.
        await().untilAsserted(() -> assertEquals(8, strategy.getPositionsUpdatesCount()));
        p = strategy.getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position1Uid, p.getUid());
        assertEquals(OPENED, p.getStatus());

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositionByPositionId(position1Uid);
        assertTrue(p1.isPresent());
        assertEquals(1, p1.get().getUid());
        assertEquals(1, p1.get().getPositionId());
        assertEquals(SHORT, p1.get().getType());
        assertNotNull(p1.get().getStrategy());
        assertEquals(1, p1.get().getStrategy().getUid());
        assertEquals("01", p1.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, p1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), p1.get().getAmount().getCurrency());
        assertTrue(p1.get().getRules().isStopGainPercentageSet());
        assertEquals(1_000f, p1.get().getRules().getStopGainPercentage());
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
        // Test of tickers updating position n°1.
        // I sold 10 ETH for BTC
        // Two trades :
        // - 5 at 0.02 (1 ETH costs 0.02 BTC) = 0.1.
        // - 5 at 0.04 (1 ETH costs 0.04 BTC) = 0.2.
        // Meaning I now have 0.3 BTC (at the mean price of 0.03).

        // First ticker arrives - min, max and last gain should be set to that value.
        // Price goes to 0.01 meaning that with my 0.3 BTC I can buy 30 eth.
        // I gain ((0.03 - 0.01) / 0.01) * 100 = 200 %
        // And, in amount : 30 - 10 = 20.
        // Price update so a new position update.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.01")).build());
        await().untilAsserted(() -> assertEquals(9, strategy.getPositionsUpdatesCount()));
        p = strategy.getLastPositionUpdate();
        assertEquals(position1Uid, p.getUid());
        assertEquals(0, new BigDecimal("0.01").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.01").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.01").compareTo(p.getLatestGainPrice().getValue()));
        assertEquals("Short position n°1 of 10 ETH (rules: 1000.0 % gain / 100.0 % loss) - Opened - Last gain calculated 200 %", p.getDescription());
        // We check the gain.
        Optional<GainDTO> latestCalculatedGain = p.getLatestCalculatedGain();
        assertTrue(latestCalculatedGain.isPresent());
        assertEquals(200, latestCalculatedGain.get().getPercentage());
        assertEquals(0, new BigDecimal(20).compareTo(latestCalculatedGain.get().getAmount().getValue()));
        assertEquals(ETH, latestCalculatedGain.get().getAmount().getCurrency());

        // Second ticker arrives (100% gain).
        // I sold 10 ETH for BTC meaning I now have 0.3 BTC (at the price of 0.03).
        // Price goes to 0.015 meaning I can now buy 20 ETH.
        // 100% gain and 10 ETH in amount.
        // Price update so a new position update.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.015")).build());
        await().untilAsserted(() -> assertEquals(10, strategy.getPositionsUpdatesCount()));
        p = strategy.getLastPositionUpdate();
        assertEquals(position1Uid, p.getUid());
        assertEquals(0, new BigDecimal("0.015").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.01").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.015").compareTo(p.getLatestGainPrice().getValue()));
        // We check the gain.
        latestCalculatedGain = p.getLatestCalculatedGain();
        assertTrue(latestCalculatedGain.isPresent());
        assertEquals(100, latestCalculatedGain.get().getPercentage());
        assertEquals(0, new BigDecimal(10).compareTo(latestCalculatedGain.get().getAmount().getValue()));
        assertEquals(ETH, latestCalculatedGain.get().getAmount().getCurrency());

        // A ticker arrive for another cp. Nothing should change.
        // And no position update.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("100")).build());

        // Third ticker arrives (90% loss) - min and last gain should be set to that value.
        // I sold 10 ETH for BTC meaning I now have 0.3 BTC (at the price of 0.03).
        // Price goes to 0.3 meaning I can now buy 1 ETH.
        // Price update so a new position update.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.3")).build());
        await().untilAsserted(() -> assertEquals(11, strategy.getPositionsUpdatesCount()));
        p = strategy.getLastPositionUpdate();
        assertEquals(position1Uid, p.getUid());
        assertEquals(0, new BigDecimal("0.3").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.01").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.3").compareTo(p.getLatestGainPrice().getValue()));
        // We check the gain.
        latestCalculatedGain = p.getLatestCalculatedGain();
        assertTrue(latestCalculatedGain.isPresent());
        assertEquals(-90, latestCalculatedGain.get().getPercentage());
        assertEquals(0, new BigDecimal("-9").compareTo(latestCalculatedGain.get().getAmount().getValue()));
        assertEquals(ETH, latestCalculatedGain.get().getAmount().getCurrency());

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositionByPositionId(position1Uid);
        assertTrue(p1.isPresent());
        assertEquals(1, p1.get().getUid());
        assertEquals(1, p1.get().getPositionId());
        assertEquals(SHORT, p1.get().getType());
        assertNotNull(p1.get().getStrategy());
        assertEquals(1, p1.get().getStrategy().getUid());
        assertEquals("01", p1.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, p1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), p1.get().getAmount().getCurrency());
        assertTrue(p1.get().getRules().isStopGainPercentageSet());
        assertEquals(1_000f, p1.get().getRules().getStopGainPercentage());
        assertTrue(p1.get().getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.get().getRules().getStopLossPercentage());
        assertEquals(OPENED, p1.get().getStatus());
        assertEquals("ORDER00010", p1.get().getOpeningOrder().getOrderId());
        openingTradesIterator = p1.get().getOpeningOrder().getTrades().iterator();
        assertEquals("000001", openingTradesIterator.next().getTradeId());
        assertEquals("000011", openingTradesIterator.next().getTradeId());
        assertNull(p1.get().getClosingOrder());
        assertEquals(0, new BigDecimal("0.3").compareTo(p1.get().getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.01").compareTo(p1.get().getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.3").compareTo(p1.get().getLatestGainPrice().getValue()));

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
        // - Update 1 : position n°1 OPENING.
        // - Update 2 : position n°2 OPENING.
        // - Update 3 : position n°1 OPENED.
        // - Update 4 : position n°2 OPENED.
        await().untilAsserted(() -> assertEquals(4, strategy.getPositionsStatusUpdatesCount()));
        p = strategy.getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position2Id, p.getUid());
        assertEquals(OPENED, p.getStatus());
        assertEquals("Short position n°2 of 0.0002 ETH (rules: 10000.0 % gain / 10000.0 % loss) - Opened", p.getDescription());

        // A ticker arrive for position 2.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_USDT).last(new BigDecimal("100")).build());

        // onPositionUpdate with two new updates.
        // One trade arrives, so we have a position update because of this trade.
        // A ticker set the new price just after.
        await().untilAsserted(() -> assertEquals(13, strategy.getPositionsUpdatesCount()));
        p = strategy.getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position2Id, p.getUid());
        assertEquals(OPENED, p.getStatus());

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p2 = strategy.getPositionByPositionId(position2Id);
        assertTrue(p2.isPresent());
        assertEquals(2, p2.get().getUid());
        assertEquals(2, p2.get().getPositionId());
        assertEquals(SHORT, p2.get().getType());
        assertNotNull(p2.get().getStrategy());
        assertEquals(1, p2.get().getStrategy().getUid());
        assertEquals("01", p2.get().getStrategy().getStrategyId());
        assertEquals(ETH_USDT, p2.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0002").compareTo(p2.get().getAmount().getValue()));
        assertEquals(ETH_USDT.getBaseCurrency(), p2.get().getAmount().getCurrency());
        assertTrue(p2.get().getRules().isStopGainPercentageSet());
        assertEquals(10_000f, p2.get().getRules().getStopGainPercentage());
        assertTrue(p2.get().getRules().isStopLossPercentageSet());
        assertEquals(10_000f, p2.get().getRules().getStopLossPercentage());
        assertEquals(OPENED, p2.get().getStatus());
        assertEquals("ORDER00020", p2.get().getOpeningOrder().getOrderId());
        openingTradesIterator = p2.get().getOpeningOrder().getTrades().iterator();
        assertEquals("000002", openingTradesIterator.next().getTradeId());
        assertNull(p2.get().getClosingOrder());
        assertEquals(0, new BigDecimal("100").compareTo(p2.get().getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("100").compareTo(p2.get().getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("100").compareTo(p2.get().getLatestGainPrice().getValue()));

        // =============================================================================================================
        // A ticker arrives that triggers max gain rules of position 1 - should now be CLOSING.
        // I sold 10 ETH for BTC meaning I now have 0.3 BTC (at the price of 0.03).
        // Price goes to 0.0003 meaning I can now buy 1 000 ETH.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.0003")).build());

        // onPositionStatusUpdate - Position 1 should be closing.
        // - Update 1 : position n°1 OPENING.
        // - Update 2 : position n°2 OPENING.
        // - Update 3 : position n°1 OPENED.
        // - Update 4 : position n°2 OPENED.
        // - Update 5 : position n°1 CLOSING.
        await().untilAsserted(() -> assertEquals(5, strategy.getPositionsStatusUpdatesCount()));
        p = strategy.getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position1Uid, p.getUid());
        assertEquals(CLOSING, p.getStatus());

        // OnPositionUpdate - We were having 13 updates.
        // - A ticker triggering position closure arrives.
        // - Position closed with the local order (status PENDING_NEW).
        // - Position updated with the distant order (status NEW).
        await().untilAsserted(() -> assertEquals(16, strategy.getPositionsUpdatesCount()));
        p = strategy.getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position1Uid, p.getUid());
        assertEquals(CLOSING, p.getStatus());

        // We check the gain.
        latestCalculatedGain = strategy.getLastPositionUpdate().getLatestCalculatedGain();
        assertTrue(latestCalculatedGain.isPresent());
        assertEquals(9900, latestCalculatedGain.get().getPercentage());
        assertEquals(0, new BigDecimal("990").compareTo(latestCalculatedGain.get().getAmount().getValue()));
        assertEquals(ETH, latestCalculatedGain.get().getAmount().getCurrency());

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositionByPositionId(position1Uid);
        assertTrue(p1.isPresent());
        assertEquals(1, p1.get().getUid());
        assertEquals(1, p1.get().getPositionId());
        assertEquals(SHORT, p1.get().getType());
        assertNotNull(p1.get().getStrategy());
        assertEquals(1, p1.get().getStrategy().getUid());
        assertEquals("01", p1.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, p1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), p1.get().getAmount().getCurrency());
        assertTrue(p1.get().getRules().isStopGainPercentageSet());
        assertEquals(1_000f, p1.get().getRules().getStopGainPercentage());
        assertTrue(p1.get().getRules().isStopLossPercentageSet());
        assertEquals(100f, p1.get().getRules().getStopLossPercentage());
        assertEquals(CLOSING, p1.get().getStatus());
        assertEquals("ORDER00010", p1.get().getOpeningOrder().getOrderId());
        openingTradesIterator = p1.get().getOpeningOrder().getTrades().iterator();
        assertEquals("000001", openingTradesIterator.next().getTradeId());
        assertEquals("000011", openingTradesIterator.next().getTradeId());
        assertEquals("ORDER00011", p1.get().getClosingOrder().getOrderId());
        assertTrue(p1.get().getClosingOrder().getTrades().isEmpty());
        assertEquals(0, new BigDecimal("0.3").compareTo(p1.get().getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.01").compareTo(p1.get().getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.0003").compareTo(p1.get().getLatestGainPrice().getValue()));

        // =============================================================================================================
        // Position 1 will move to CLOSED status when the trades arrive.
        // The first close trade arrives but the amount is not enough.
        tradeFlux.emitValue(TradeDTO.builder().tradeId("000003")
                .orderId("ORDER00011")
                .type(BID)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("500", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("1", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("01-01-2020"))
                .build());

        // We send a duplicated value.
        tradeFlux.emitValue(TradeDTO.builder().tradeId("000003")
                .orderId("ORDER00011")
                .type(BID)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("500", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("1", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("01-01-2020"))
                .build());

        // onPosition for first trade arrival.
        // Two more updates because of two trades arriving (duplicated, but we use emit function).
        await().untilAsserted(() -> assertEquals(18, strategy.getPositionsUpdatesCount()));
        p = strategy.getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position1Uid, p.getUid());
        assertEquals(CLOSING, p.getStatus());

        // The second close trade arrives now closed.
        tradeFlux.emitValue(TradeDTO.builder().tradeId("000004")
                .orderId("ORDER00011")
                .type(BID)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("500", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("1", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("02-01-2020"))
                .build());

        // onPositionStatusUpdate - Position should be closed.
        // - Update 1 : position n°1 OPENING.
        // - Update 2 : position n°2 OPENING.
        // - Update 3 : position n°1 OPENED.
        // - Update 4 : position n°2 OPENED.
        // - Update 5 : position n°1 CLOSING.
        // - Update 6 : position n°1 CLOSED.
        await().untilAsserted(() -> assertEquals(6, strategy.getPositionsStatusUpdatesCount()));
        p = strategy.getLastPositionStatusUpdate();
        assertNotNull(p);
        assertEquals(position1Uid, p.getUid());
        assertEquals(CLOSED, p.getStatus());
        assertEquals("Short position n°1 of 10 ETH (rules: 1000.0 % gain / 100.0 % loss) - Closed - Gains: 990 ETH (9900.0 %)", p.getDescription());

        // onPosition for second trade arrival.
        p = strategy.getLastPositionUpdate();
        assertNotNull(p);
        assertEquals(position1Uid, p.getUid());
        assertEquals(CLOSED, p.getStatus());

        // We check the gain.
        latestCalculatedGain = p.getLatestCalculatedGain();
        assertTrue(latestCalculatedGain.isPresent());
        assertEquals(9900, latestCalculatedGain.get().getPercentage());
        assertEquals(0, new BigDecimal("990").compareTo(latestCalculatedGain.get().getAmount().getValue()));
        assertEquals(ETH, latestCalculatedGain.get().getAmount().getCurrency());

        // Checking what we have in database.
        assertEquals(2, strategy.getPositions().size());
        p1 = strategy.getPositionByPositionId(position1Uid);
        assertTrue(p1.isPresent());
        assertEquals(1, p1.get().getUid());
        assertEquals(1, p1.get().getPositionId());
        assertEquals(SHORT, p1.get().getType());
        assertNotNull(p1.get().getStrategy());
        assertEquals(1, p1.get().getStrategy().getUid());
        assertEquals("01", p1.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, p1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p1.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), p1.get().getAmount().getCurrency());
        assertTrue(p1.get().getRules().isStopGainPercentageSet());
        assertEquals(1_000f, p1.get().getRules().getStopGainPercentage());
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
        assertEquals(0, new BigDecimal("0.3").compareTo(p1.get().getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.01").compareTo(p1.get().getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.0003").compareTo(p1.get().getLatestGainPrice().getValue()));

        // Just checking trades creation.
        assertNotNull(strategy.getPositionByPositionId(position1Uid));
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

}
