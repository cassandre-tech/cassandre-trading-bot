package tech.cassandre.trading.bot.test.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Domain - Position")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql"),
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class PositionTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private PositionService positionService;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private PositionFlux positionFlux;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Test
    @DisplayName("Check load position from database")
    public void checkLoadPositionFromDatabase() {
        // =============================================================================================================
        // Check that positions, orders and trades in database doesn't trigger strategy events.
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());
        assertTrue(strategy.getOrdersUpdateReceived().isEmpty());

        // =============================================================================================================
        // Check position 1 - OPENING.
        PositionDTO position = strategy.getPositions().get(1L);
        assertNotNull(position);
        assertEquals(1L, position.getId());
        assertEquals(OPENING, position.getStatus());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), position.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(position.getAmount()));
        assertFalse(position.getRules().isStopGainPercentageSet());
        assertFalse(position.getRules().isStopLossPercentageSet());
        assertEquals("BACKUP_OPENING_ORDER_01", position.getOpeningOrder().getId());
        assertTrue(position.getOpeningTrades().isEmpty());
        assertNull(position.getClosingOrder());
        assertTrue(position.getClosingTrades().isEmpty());
        assertNull(position.getLowestPrice());
        assertNull(position.getHighestPrice());
        assertNull(position.getLatestPrice());
        assertNotNull(position.getStrategy());
        assertEquals("001", position.getStrategy().getId());

        // =============================================================================================================
        // Check position 2 - OPENED.
        position = strategy.getPositions().get(2L);
        assertNotNull(position);
        assertEquals(2L, position.getId());
        assertEquals(OPENED, position.getStatus());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), position.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(position.getAmount()));
        assertTrue(position.getRules().isStopGainPercentageSet());
        assertEquals(10, position.getRules().getStopGainPercentage());
        assertFalse(position.getRules().isStopLossPercentageSet());
        assertEquals("BACKUP_OPENING_ORDER_02", position.getOpeningOrder().getId());
        assertEquals(1, position.getOpeningTrades().size());
        position.getOpeningTrades().forEach(tradeDTO -> System.out.println(">==> " + tradeDTO));
        assertTrue(position.getTrade("BACKUP_TRADE_01").isPresent());
        assertNull(position.getClosingOrder());
        assertTrue(position.getClosingTrades().isEmpty());
        assertEquals(0, new BigDecimal("1").compareTo(position.getLowestPrice()));
        assertEquals(0, new BigDecimal("2").compareTo(position.getHighestPrice()));
        assertEquals(0, new BigDecimal("3").compareTo(position.getLatestPrice()));
        assertNotNull(position.getStrategy());
        assertEquals("001", position.getStrategy().getId());

        // =============================================================================================================
        // Check position 3 - CLOSING.
        position = strategy.getPositions().get(3L);
        assertNotNull(position);
        assertEquals(3L, position.getId());
        assertEquals(CLOSING, position.getStatus());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), position.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(position.getAmount()));
        assertFalse(position.getRules().isStopGainPercentageSet());
        assertTrue(position.getRules().isStopLossPercentageSet());
        assertEquals(20, position.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPENING_ORDER_03", position.getOpeningOrder().getId());
        assertEquals(1, position.getOpeningTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_02").isPresent());
        assertEquals("BACKUP_CLOSING_ORDER_01", position.getClosingOrder().getId());
        assertEquals(1, position.getClosingTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_04").isPresent());
        assertEquals(0, new BigDecimal("17").compareTo(position.getLowestPrice()));
        assertEquals(0, new BigDecimal("68").compareTo(position.getHighestPrice()));
        assertEquals(0, new BigDecimal("92").compareTo(position.getLatestPrice()));
        assertNotNull(position.getStrategy());
        assertEquals("001", position.getStrategy().getId());

        // =============================================================================================================
        // Check position 4 - CLOSED.
        position = strategy.getPositions().get(4L);
        assertNotNull(position);
        assertEquals(4L, position.getId());
        assertEquals(CLOSED, position.getStatus());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), position.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(position.getAmount()));
        assertTrue(position.getRules().isStopGainPercentageSet());
        assertEquals(30, position.getRules().getStopGainPercentage());
        assertTrue(position.getRules().isStopLossPercentageSet());
        assertEquals(40, position.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPENING_ORDER_04", position.getOpeningOrder().getId());
        assertEquals(1, position.getOpeningTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_03").isPresent());
        assertEquals("BACKUP_CLOSING_ORDER_02", position.getClosingOrder().getId());
        assertEquals(1, position.getClosingTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_05").isPresent());
        assertEquals(0, new BigDecimal("17").compareTo(position.getLowestPrice()));
        assertEquals(0, new BigDecimal("68").compareTo(position.getHighestPrice()));
        assertEquals(0, new BigDecimal("93").compareTo(position.getLatestPrice()));
        assertNotNull(position.getStrategy());
        assertEquals("001", position.getStrategy().getId());

        // =============================================================================================================
        // Check position 5 - CLOSED with several trades.
        position = strategy.getPositions().get(5L);
        assertEquals(5L, position.getId());
        assertEquals(CLOSED, position.getStatus());
        assertEquals(new CurrencyPairDTO("ETH/USD"), position.getCurrencyPair());
        assertEquals(0, new BigDecimal("50").compareTo(position.getAmount()));
        assertTrue(position.getRules().isStopGainPercentageSet());
        assertEquals(30, position.getRules().getStopGainPercentage());
        assertTrue(position.getRules().isStopLossPercentageSet());
        assertEquals(40, position.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPENING_ORDER_05", position.getOpeningOrder().getId());
        assertEquals("BACKUP_CLOSING_ORDER_03", position.getClosingOrder().getId());
        assertEquals(0, new BigDecimal("17").compareTo(position.getLowestPrice()));
        assertEquals(0, new BigDecimal("68").compareTo(position.getHighestPrice()));
        assertEquals(0, new BigDecimal("94").compareTo(position.getLatestPrice()));
        assertNotNull(position.getStrategy());
        assertEquals("001", position.getStrategy().getId());
        // Open trades.
        assertEquals(2, position.getOpeningTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_06").isPresent());
        assertEquals("BACKUP_TRADE_06", position.getTrade("BACKUP_TRADE_06").get().getId());
        assertTrue(position.getOpeningTrades().stream().anyMatch(t -> "BACKUP_TRADE_06".equals(t.getId())));
        assertTrue(position.getTrade("BACKUP_TRADE_07").isPresent());
        assertTrue(position.getOpeningTrades().stream().anyMatch(t -> "BACKUP_TRADE_07".equals(t.getId())));
        assertEquals("BACKUP_TRADE_07", position.getTrade("BACKUP_TRADE_07").get().getId());
        // Close trades.
        assertEquals(3, position.getClosingTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_08").isPresent());
        assertTrue(position.getClosingTrades().stream().anyMatch(t -> "BACKUP_TRADE_08".equals(t.getId())));
        assertEquals("BACKUP_TRADE_08", position.getTrade("BACKUP_TRADE_08").get().getId());
        assertTrue(position.getTrade("BACKUP_TRADE_09").isPresent());
        assertTrue(position.getClosingTrades().stream().anyMatch(t -> "BACKUP_TRADE_09".equals(t.getId())));
        assertEquals("BACKUP_TRADE_09", position.getTrade("BACKUP_TRADE_09").get().getId());
        assertTrue(position.getTrade("BACKUP_TRADE_10").isPresent());
        assertTrue(position.getClosingTrades().stream().anyMatch(t -> "BACKUP_TRADE_10".equals(t.getId())));
        assertEquals("BACKUP_TRADE_10", position.getTrade("BACKUP_TRADE_10").get().getId());
        // Check trade orders.
        final Iterator<TradeDTO> openTradesIterator = position.getOpeningTrades().iterator();
        assertEquals("BACKUP_TRADE_06", openTradesIterator.next().getId());
        assertEquals("BACKUP_TRADE_07", openTradesIterator.next().getId());
        final Iterator<TradeDTO> closeTradesIterator = position.getClosingTrades().iterator();
        assertEquals("BACKUP_TRADE_08", closeTradesIterator.next().getId());
        assertEquals("BACKUP_TRADE_09", closeTradesIterator.next().getId());
        assertEquals("BACKUP_TRADE_10", closeTradesIterator.next().getId());
    }

    @Test
    @DisplayName("Check how a new positions is saved")
    public void checkSavedNewPosition() {
        // =============================================================================================================
        // Check that positions, orders and trades in database doesn't trigger strategy events.
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());
        assertTrue(strategy.getOrdersUpdateReceived().isEmpty());

        // First ticker emitted for dry mode - MANDATORY.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).timestamp(createDate(1)).last(new BigDecimal("0.2")).create());
        await().untilAsserted(() -> assertEquals(1, strategy.getLastTickers().size()));

        // =============================================================================================================
        // Creates a position with ID 6 - waiting for order DRY_ORDER_000000001.
        long positionCount = positionRepository.count();
        PositionRulesDTO rules = PositionRulesDTO.builder().stopGainPercentage(1f).stopLossPercentage(2f).create();
        PositionCreationResultDTO creationResult1 = strategy.createPosition(cp1, new BigDecimal("0.0001"), rules);
        assertTrue(creationResult1.isSuccessful());
        assertEquals(6, creationResult1.getPositionId());
        assertEquals("DRY_ORDER_000000001", creationResult1.getOrderId());

        // Check that the position was correctly created.
        // The corresponding order and trade will arrive in few seconds.
        // In the mean time, the position should be in OPENING status.
        await().untilAsserted(() -> assertEquals(positionCount + 1, positionRepository.count()));
        Position p6 = getPosition(6L);
        assertEquals(6L, p6.getId());
        assertEquals(OPENING, p6.getStatus());
        assertEquals(cp1.toString(), p6.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0001").compareTo(p6.getAmount()));
        assertEquals(1, p6.getStopGainPercentageRule());
        assertEquals(2, p6.getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p6.getOpeningOrder().getId());
        assertNull(p6.getClosingOrder());
        assertTrue(p6.getOpeningOrder().getTrades().isEmpty());
        assertNull(p6.getClosingOrder());

        // If we wait a bit, the order and trade will arrive and the position status will be OPENED.
        await().untilAsserted(() -> assertEquals(OPENED, getPosition(6L).getStatus()));
        p6 = getPosition(6L);
        assertEquals(6L, p6.getId());
        assertEquals(OPENED, p6.getStatus());
        assertEquals(cp1.toString(), p6.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0001").compareTo(p6.getAmount()));
        assertEquals(1, p6.getStopGainPercentageRule());
        assertEquals(2, p6.getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p6.getOpeningOrder().getId());
        assertNull(p6.getClosingOrder());
        assertFalse(p6.getOpeningOrder().getTrades().isEmpty());
        assertTrue(p6.getOpeningOrder().getTrades().stream().anyMatch(t -> "DRY_TRADE_000000001".equals(t.getId())));

        // =============================================================================================================
        // Creates a position with ID to 7.
        PositionCreationResultDTO creationResult2 = strategy.createPosition(cp1, new BigDecimal("0.0002"), PositionRulesDTO.builder().create());
        assertTrue(creationResult2.isSuccessful());
        assertEquals("DRY_ORDER_000000002", creationResult2.getOrderId());

        // Check the created position in database.
        await().untilAsserted(() -> assertEquals(positionCount + 2, positionRepository.count()));
        Position p7 = getPosition(7L);
        assertEquals(7L, p7.getId());
        assertEquals(OPENING, p7.getStatus());
        assertEquals(cp1.toString(), p7.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0002").compareTo(p7.getAmount()));
        assertNull(p7.getStopGainPercentageRule());
        assertNull(p7.getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000002", p7.getOpeningOrder().getId());
        assertNull(p7.getClosingOrder());
    }

    @Test
    @DisplayName("Check saved data during position lifecycle")
    public void checkSavedDataDuringPositionLifecycle() {
        // =============================================================================================================
        // Check that positions, orders and trades are restored in strategy & services.
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());
        assertTrue(strategy.getOrdersUpdateReceived().isEmpty());

        // First ticker emitted for dry mode - MANDATORY.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).timestamp(createDate(1)).last(new BigDecimal("0.01")).create());
        await().untilAsserted(() -> assertEquals(1, strategy.getTickersUpdateReceived().size()));

        // =============================================================================================================
        // A position is opening on ETH/BTC - ID 6.
        // We buy 1 ETH for 0.01 BTC.
        // Waiting for order DRY_ORDER_000000001.
        final PositionCreationResultDTO positionResult = strategy.createPosition(cp1,
                new BigDecimal("1"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1000f)   // 1 000% max gain.
                        .stopLossPercentage(100f)    // 100% max lost.
                        .create());
        assertTrue(positionResult.isSuccessful());
        final long positionId = positionResult.getPositionId();

        // =============================================================================================================
        // Still "OPENING".
        // Two tickers arrived - min and max gain should not be set as the position is still in OPENING status.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("10")).create());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.01")).create());

        // Check that the position was correctly created.
        // The corresponding order and trade will arrive in few seconds.
        // In the mean time, the position should be in OPENING status.
        await().untilAsserted(() -> assertEquals(OPENING, getPosition(positionId).getStatus()));
        Position p = getPosition(positionId);
        assertEquals(positionId, p.getId());
        assertEquals(OPENING, p.getStatus());
        assertEquals(1000, p.getStopGainPercentageRule());
        assertEquals(100, p.getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p.getOpeningOrder().getId());
        assertNull(p.getClosingOrder());
        assertNull(p.getLowestPrice());
        assertNull(p.getHighestPrice());
        ZonedDateTime createdOn = p.getCreatedOn();
        ZonedDateTime updatedON = p.getUpdatedOn();
        assertNotNull(createdOn);
        assertNotNull(updatedON);

        // We should have one more position and one more trade in database.
        await().untilAsserted(() -> assertEquals(6, positionRepository.count()));
        tradeRepository.findByOrderByTimestampAsc().forEach(trade -> System.out.println("==> " + trade));
        await().untilAsserted(() -> assertEquals(11, tradeRepository.count()));

        // =============================================================================================================
        // We should now be OPENED.
        // We are in dry mode, we wait for order and trade to arrive, position will now be opened.
        await().untilAsserted(() -> assertEquals(OPENED, getPosition(positionId).getStatus()));
        await().untilAsserted(() -> assertEquals(1, strategy.getTradesUpdateReceived().size()));

        // Check saved position in database.
        p = getPosition(positionId);
        assertEquals(positionId, p.getId());
        assertEquals(OPENED, p.getStatus());
        assertEquals(1000, p.getStopGainPercentageRule());
        assertEquals(100, p.getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p.getOpeningOrder().getId());
        tradeRepository.findByOrderByTimestampAsc().forEach(trade -> System.out.println(" > " + trade));
        assertFalse(p.getOpeningOrder().getTrades().isEmpty());
        assertTrue(p.getOpeningOrder().getTrades().stream().anyMatch(t -> "DRY_TRADE_000000001".equals(t.getId())));
        assertNull(p.getClosingOrder());
        assertNull(p.getLowestPrice());
        assertNull(p.getHighestPrice());

        // =============================================================================================================
        // Now that the position is OPENED, we are sending tickers to see if lowest, highest and latest price change.
        await().untilAsserted(() -> assertNull(getPosition(positionId).getLatestPrice()));

        // First ticker arrives (500% gain) - min and max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.06")).create());
        await().untilAsserted(() -> assertNotNull(getPosition(positionId).getLatestPrice()));
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.06").compareTo(getPosition(positionId).getLatestPrice())));

        // Second ticker arrives (100% gain) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.02")).create());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.02").compareTo(getPosition(positionId).getLatestPrice())));

        // Third ticker arrives (200% gain) - nothing should change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.03")).create());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.03").compareTo(getPosition(positionId).getLatestPrice())));

        // Fourth ticker arrives (50% loss) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.005")).create());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.005").compareTo(getPosition(positionId).getLatestPrice())));

        // Firth ticker arrives (600% gain) - max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.07")).create());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.07").compareTo(getPosition(positionId).getLatestPrice())));

        // Check lowest & highest in database.
        await().untilAsserted(() -> assertEquals(8, strategy.getTickersUpdateReceived().size()));
        assertTrue(getPositionDTO(positionId).getLowestCalculatedGain().isPresent());
        assertTrue(getPositionDTO(positionId).getHighestCalculatedGain().isPresent());
        assertEquals(0, new BigDecimal("0.005").compareTo(getPositionDTO(positionId).getLowestPrice()));
        assertEquals(0, new BigDecimal("0.07").compareTo(getPositionDTO(positionId).getHighestPrice()));
        assertEquals(-50, getPositionDTO(positionId).getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, getPositionDTO(positionId).getHighestCalculatedGain().get().getPercentage());

        // Check that the new data was inserted in database.
        await().untilAsserted(() -> assertEquals(6, positionRepository.count()));
        await().untilAsserted(() -> assertEquals(11, tradeRepository.count()));
        assertEquals(createdOn, getPosition(positionId).getCreatedOn());
        assertTrue(updatedON.isBefore(getPosition(positionId).getUpdatedOn()));

        // =============================================================================================================
        // We should now be CLOSING. We are going to receive two trades to close.
        // Closing the trade - min and max should not change.
        PositionDTO pDTO = getPositionDTO(positionId);
        pDTO.setClosingOrderId("DRY_ORDER_000000002");
        positionFlux.emitValue(pDTO);

        // The first close trade arrives, status should not change as it's not the total amount.
        tradeFlux.emitValue(TradeDTO.builder().id("000002")
                .orderId("DRY_ORDER_000000002")
                .type(ASK)
                .originalAmount(new BigDecimal("0.5"))
                .currencyPair(cp1)
                .price(new BigDecimal("1"))
                .create());
        await().untilAsserted(() -> assertEquals(1, getPositionDTO(positionId).getClosingTrades().size()));
        await().untilAsserted(() -> assertEquals(12, tradeRepository.count()));
        assertEquals(CLOSING, getPositionDTO(positionId).getStatus());

        // The second close trade arrives, status should change
        tradeFlux.emitValue(TradeDTO.builder().id("000003")
                .orderId("DRY_ORDER_000000002")
                .type(ASK)
                .originalAmount(new BigDecimal("0.5"))
                .currencyPair(cp1)
                .price(new BigDecimal("1"))
                .create());
        await().untilAsserted(() -> assertEquals(2, getPositionDTO(positionId).getClosingTrades().size()));
        await().untilAsserted(() -> assertEquals(13, tradeRepository.count()));
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(positionId).getStatus()));

        // =============================================================================================================
        // We should now be CLOSED as we received the two trades.
        // Check saved position.
        await().until(() -> getPosition(positionId).getStatus().equals(CLOSED));
        p = getPosition(positionId);
        assertEquals(positionId, p.getId());
        assertEquals(CLOSED, p.getStatus());
        assertEquals(cp1.toString(), p.getCurrencyPair());
        assertEquals(0, new BigDecimal("1").compareTo(p.getAmount()));
        assertEquals(1000, p.getStopGainPercentageRule());
        assertEquals(100, p.getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p.getOpeningOrder().getId());
        assertEquals("DRY_ORDER_000000002", p.getClosingOrder().getId());
        assertEquals(1, p.getOpeningOrder().getTrades().size());
        assertEquals(2, p.getClosingOrder().getTrades().size());
        assertTrue(p.getOpeningOrder().getTrades().stream().anyMatch(t -> "DRY_TRADE_000000001".equals(t.getId())));
        assertTrue(p.getClosingOrder().getTrades().stream().anyMatch(t -> "000002".equals(t.getId())));
        assertTrue(p.getClosingOrder().getTrades().stream().anyMatch(t -> "000003".equals(t.getId())));
        assertEquals(0, new BigDecimal("0.005").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("0.07").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("0.07").compareTo(p.getLatestPrice()));
        assertEquals(13, tradeRepository.count());
    }

    /**
     * Retrieve position from database.
     * @param id position id
     * @return position
     */
    private Position getPosition(final long id) {
        final Optional<Position> p = positionRepository.findById(id);
        if (p.isPresent()) {
            return p.get();
        } else {
            throw new RuntimeException("Position not found : " + id);
        }
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
