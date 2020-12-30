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
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
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
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.LONG;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
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
    private OrderRepository orderRepository;

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
        assertEquals(0, new BigDecimal("10").compareTo(position.getAmount().getValue()));
        assertEquals(BTC, position.getAmount().getCurrency());
        assertFalse(position.getRules().isStopGainPercentageSet());
        assertFalse(position.getRules().isStopLossPercentageSet());
        assertEquals("BACKUP_OPENING_ORDER_01", position.getOpeningOrder().getOrderId());
        assertTrue(position.getOpeningTrades().isEmpty());
        assertNull(position.getClosingOrder());
        assertTrue(position.getClosingTrades().isEmpty());
        assertNull(position.getLowestPrice());
        assertNull(position.getHighestPrice());
        assertNull(position.getLatestPrice());
        assertNotNull(position.getStrategy());
        assertEquals("01", position.getStrategy().getStrategyId());

        // =============================================================================================================
        // Check position 2 - OPENED.
        position = strategy.getPositions().get(2L);
        assertNotNull(position);
        assertEquals(2L, position.getId());
        assertEquals(OPENED, position.getStatus());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), position.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(position.getAmount().getValue()));
        assertEquals(BTC, position.getAmount().getCurrency());
        assertTrue(position.getRules().isStopGainPercentageSet());
        assertEquals(10, position.getRules().getStopGainPercentage());
        assertFalse(position.getRules().isStopLossPercentageSet());
        assertEquals("BACKUP_OPENING_ORDER_02", position.getOpeningOrder().getOrderId());
        assertEquals(1, position.getOpeningTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_01").isPresent());
        assertNull(position.getClosingOrder());
        assertTrue(position.getClosingTrades().isEmpty());
        assertEquals(0, new BigDecimal("1").compareTo(position.getLowestPrice().getValue()));
        assertEquals(USDT, position.getLowestPrice().getCurrency());
        assertEquals(0, new BigDecimal("2").compareTo(position.getHighestPrice().getValue()));
        assertEquals(USDT, position.getHighestPrice().getCurrency());
        assertEquals(0, new BigDecimal("3").compareTo(position.getLatestPrice().getValue()));
        assertEquals(USDT, position.getLatestPrice().getCurrency());
        assertNotNull(position.getStrategy());
        assertEquals("01", position.getStrategy().getStrategyId());

        // =============================================================================================================
        // Check position 3 - CLOSING.
        position = strategy.getPositions().get(3L);
        assertNotNull(position);
        assertEquals(3L, position.getId());
        assertEquals(CLOSING, position.getStatus());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), position.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(position.getAmount().getValue()));
        assertEquals(BTC, position.getAmount().getCurrency());
        assertFalse(position.getRules().isStopGainPercentageSet());
        assertTrue(position.getRules().isStopLossPercentageSet());
        assertEquals(20, position.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPENING_ORDER_03", position.getOpeningOrder().getOrderId());
        assertEquals(1, position.getOpeningTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_02").isPresent());
        assertEquals("BACKUP_CLOSING_ORDER_01", position.getClosingOrder().getOrderId());
        assertEquals(1, position.getClosingTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_04").isPresent());
        assertEquals(0, new BigDecimal("17").compareTo(position.getLowestPrice().getValue()));
        assertEquals(USDT, position.getLowestPrice().getCurrency());
        assertEquals(0, new BigDecimal("68").compareTo(position.getHighestPrice().getValue()));
        assertEquals(USDT, position.getHighestPrice().getCurrency());
        assertEquals(0, new BigDecimal("92").compareTo(position.getLatestPrice().getValue()));
        assertEquals(USDT, position.getLatestPrice().getCurrency());
        assertNotNull(position.getStrategy());
        assertEquals("01", position.getStrategy().getStrategyId());

        // =============================================================================================================
        // Check position 4 - CLOSED.
        position = strategy.getPositions().get(4L);
        assertNotNull(position);
        assertEquals(4L, position.getId());
        assertEquals(CLOSED, position.getStatus());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), position.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(position.getAmount().getValue()));
        assertEquals(BTC, position.getAmount().getCurrency());
        assertTrue(position.getRules().isStopGainPercentageSet());
        assertEquals(30, position.getRules().getStopGainPercentage());
        assertTrue(position.getRules().isStopLossPercentageSet());
        assertEquals(40, position.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPENING_ORDER_04", position.getOpeningOrder().getOrderId());
        assertEquals(1, position.getOpeningTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_03").isPresent());
        assertEquals("BACKUP_CLOSING_ORDER_02", position.getClosingOrder().getOrderId());
        assertEquals(1, position.getClosingTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_05").isPresent());
        assertEquals(0, new BigDecimal("17").compareTo(position.getLowestPrice().getValue()));
        assertEquals(USDT, position.getLowestPrice().getCurrency());
        assertEquals(0, new BigDecimal("68").compareTo(position.getHighestPrice().getValue()));
        assertEquals(USDT, position.getHighestPrice().getCurrency());
        assertEquals(0, new BigDecimal("93").compareTo(position.getLatestPrice().getValue()));
        assertEquals(USDT, position.getLatestPrice().getCurrency());
        assertNotNull(position.getStrategy());
        assertEquals("01", position.getStrategy().getStrategyId());

        // =============================================================================================================
        // Check position 5 - CLOSED with several trades.
        position = strategy.getPositions().get(5L);
        assertEquals(5L, position.getId());
        assertEquals(CLOSED, position.getStatus());
        assertEquals(new CurrencyPairDTO("ETH/USD"), position.getCurrencyPair());
        assertEquals(0, new BigDecimal("50").compareTo(position.getAmount().getValue()));
        assertEquals(ETH, position.getAmount().getCurrency());
        assertTrue(position.getRules().isStopGainPercentageSet());
        assertEquals(30, position.getRules().getStopGainPercentage());
        assertTrue(position.getRules().isStopLossPercentageSet());
        assertEquals(40, position.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPENING_ORDER_05", position.getOpeningOrder().getOrderId());
        assertEquals("BACKUP_CLOSING_ORDER_03", position.getClosingOrder().getOrderId());
        assertEquals(0, new BigDecimal("17").compareTo(position.getLowestPrice().getValue()));
        assertEquals(USD, position.getLowestPrice().getCurrency());
        assertEquals(0, new BigDecimal("68").compareTo(position.getHighestPrice().getValue()));
        assertEquals(USD, position.getHighestPrice().getCurrency());
        assertEquals(0, new BigDecimal("94").compareTo(position.getLatestPrice().getValue()));
        assertEquals(USD, position.getLatestPrice().getCurrency());
        assertNotNull(position.getStrategy());
        assertEquals("01", position.getStrategy().getStrategyId());
        // Open trades.
        assertEquals(2, position.getOpeningTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_06").isPresent());
        assertEquals("BACKUP_TRADE_06", position.getTrade("BACKUP_TRADE_06").get().getTradeId());
        assertTrue(position.getOpeningTrades().stream().anyMatch(t -> "BACKUP_TRADE_06".equals(t.getTradeId())));
        assertTrue(position.getTrade("BACKUP_TRADE_07").isPresent());
        assertTrue(position.getOpeningTrades().stream().anyMatch(t -> "BACKUP_TRADE_07".equals(t.getTradeId())));
        assertEquals("BACKUP_TRADE_07", position.getTrade("BACKUP_TRADE_07").get().getTradeId());
        // Close trades.
        assertEquals(3, position.getClosingTrades().size());
        assertTrue(position.getTrade("BACKUP_TRADE_08").isPresent());
        assertTrue(position.getClosingTrades().stream().anyMatch(t -> "BACKUP_TRADE_08".equals(t.getTradeId())));
        assertEquals("BACKUP_TRADE_08", position.getTrade("BACKUP_TRADE_08").get().getTradeId());
        assertTrue(position.getTrade("BACKUP_TRADE_09").isPresent());
        assertTrue(position.getClosingTrades().stream().anyMatch(t -> "BACKUP_TRADE_09".equals(t.getTradeId())));
        assertEquals("BACKUP_TRADE_09", position.getTrade("BACKUP_TRADE_09").get().getTradeId());
        assertTrue(position.getTrade("BACKUP_TRADE_10").isPresent());
        assertTrue(position.getClosingTrades().stream().anyMatch(t -> "BACKUP_TRADE_10".equals(t.getTradeId())));
        assertEquals("BACKUP_TRADE_10", position.getTrade("BACKUP_TRADE_10").get().getTradeId());
        // Check trade orders.
        final Iterator<TradeDTO> openTradesIterator = position.getOpeningTrades().iterator();
        assertEquals("BACKUP_TRADE_06", openTradesIterator.next().getTradeId());
        assertEquals("BACKUP_TRADE_07", openTradesIterator.next().getTradeId());
        final Iterator<TradeDTO> closeTradesIterator = position.getClosingTrades().iterator();
        assertEquals("BACKUP_TRADE_08", closeTradesIterator.next().getTradeId());
        assertEquals("BACKUP_TRADE_09", closeTradesIterator.next().getTradeId());
        assertEquals("BACKUP_TRADE_10", closeTradesIterator.next().getTradeId());
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
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).timestamp(createDate(1)).last(new BigDecimal("0.2")).build());
        await().untilAsserted(() -> assertEquals(1, strategy.getLastTickers().size()));

        // =============================================================================================================
        // Creates a position with ID 6 - waiting for order DRY_ORDER_000000001.
        long positionCount = positionRepository.count();
        PositionRulesDTO rules = PositionRulesDTO.builder().stopGainPercentage(1f).stopLossPercentage(2f).build();
        PositionCreationResultDTO creationResult1 = strategy.createLongPosition(cp1, new BigDecimal("0.0001"), rules);
        assertTrue(creationResult1.isSuccessful());
        assertEquals(6, creationResult1.getPosition().getId());
        assertEquals("DRY_ORDER_000000001", creationResult1.getPosition().getOpeningOrder().getOrderId());

        // Check that the position was correctly created.
        // The corresponding order and trade will arrive in few seconds.
        // In the mean time, the position should be in OPENING status.
        await().untilAsserted(() -> assertEquals(positionCount + 1, positionRepository.count()));
        Position p6 = getPosition(6L);
        assertEquals(6L, p6.getId());
        assertEquals(OPENING, p6.getStatus());
        assertEquals(cp1.toString(), p6.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0001").compareTo(p6.getAmount().getValue()));
        assertEquals(1, p6.getStopGainPercentageRule());
        assertEquals(2, p6.getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p6.getOpeningOrder().getOrderId());
        assertNull(p6.getClosingOrder());
        assertTrue(p6.getOpeningOrder().getTrades().isEmpty());
        assertNull(p6.getClosingOrder());

        // If we wait a bit, the order and trade will arrive and the position status will be OPENED.
        await().untilAsserted(() -> assertEquals(OPENED, getPosition(6L).getStatus()));
        p6 = getPosition(6L);
        assertEquals(6L, p6.getId());
        assertEquals(OPENED, p6.getStatus());
        assertEquals(cp1.toString(), p6.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0001").compareTo(p6.getAmount().getValue()));
        assertEquals(1, p6.getStopGainPercentageRule());
        assertEquals(2, p6.getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p6.getOpeningOrder().getOrderId());
        assertNull(p6.getClosingOrder());
        assertFalse(p6.getOpeningOrder().getTrades().isEmpty());
        assertTrue(p6.getOpeningOrder().getTrades().stream().anyMatch(t -> "DRY_TRADE_000000001".equals(t.getTradeId())));

        // =============================================================================================================
        // Creates a position with ID to 7.
        PositionCreationResultDTO creationResult2 = strategy.createLongPosition(cp1, new BigDecimal("0.0002"), PositionRulesDTO.builder().build());
        assertTrue(creationResult2.isSuccessful());
        assertEquals("DRY_ORDER_000000002", creationResult2.getPosition().getOpeningOrder().getOrderId());

        // Check the created position in database.
        await().untilAsserted(() -> assertEquals(positionCount + 2, positionRepository.count()));
        Position p7 = getPosition(7L);
        assertEquals(7L, p7.getId());
        assertEquals(7L, p7.getPositionId());
        assertEquals(OPENING, p7.getStatus());
        assertEquals(cp1.toString(), p7.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0002").compareTo(p7.getAmount().getValue()));
        assertNull(p7.getStopGainPercentageRule());
        assertNull(p7.getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000002", p7.getOpeningOrder().getOrderId());
        assertNull(p7.getClosingOrder());
        assertEquals(LONG, p7.getType());
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
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).timestamp(createDate(1)).last(new BigDecimal("0.01")).build());
        await().untilAsserted(() -> assertEquals(1, strategy.getTickersUpdateReceived().size()));

        // =============================================================================================================
        // A position is opening on ETH/BTC - ID 6.
        // We buy 1 ETH for 0.01 BTC.
        // Waiting for order DRY_ORDER_000000001.
        final PositionCreationResultDTO positionResult = strategy.createLongPosition(cp1,
                new BigDecimal("1"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1000f)   // 1 000% max gain.
                        .stopLossPercentage(100f)    // 100% max lost.
                        .build());
        assertTrue(positionResult.isSuccessful());
        final long positionId = positionResult.getPosition().getId();

        // =============================================================================================================
        // Still "OPENING".
        // Two tickers arrived - min and max gain should not be set as the position is still in OPENING status.

        // Check that the position was correctly created.
        // The corresponding order and trade will arrive in few seconds.
        // In the mean time, the position should be in OPENING status.
        await().untilAsserted(() -> assertEquals(OPENING, getPosition(positionId).getStatus()));
        Position p = getPosition(positionId);
        assertEquals(positionId, p.getId());
        assertEquals(OPENING, p.getStatus());
        assertEquals(1000, p.getStopGainPercentageRule());
        assertEquals(100, p.getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p.getOpeningOrder().getOrderId());
        assertNull(p.getClosingOrder());
        assertNull(p.getLowestPrice());
        assertNull(p.getHighestPrice());
        ZonedDateTime createdOn = p.getCreatedOn();
        ZonedDateTime updatedON = p.getUpdatedOn();
        assertNotNull(createdOn);
        assertNotNull(updatedON);

        // We should have one more position and one more trade in database.
        await().untilAsserted(() -> assertEquals(6, positionRepository.count()));
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
        assertEquals("DRY_ORDER_000000001", p.getOpeningOrder().getOrderId());
        assertFalse(p.getOpeningOrder().getTrades().isEmpty());
        assertTrue(p.getOpeningOrder().getTrades().stream().anyMatch(t -> "DRY_TRADE_000000001".equals(t.getTradeId())));
        assertNull(p.getClosingOrder());
        assertNull(p.getLatestPrice());
        assertNull(p.getLowestPrice());
        assertNull(p.getHighestPrice());

        // =============================================================================================================
        // Now that the position is OPENED, we are sending tickers to see if lowest, highest and latest price change.
        await().untilAsserted(() -> assertNull(getPosition(positionId).getLatestPrice()));

        // First ticker arrives (500% gain) - min and max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.06")).build());
        await().untilAsserted(() -> assertNotNull(getPosition(positionId).getLatestPrice()));
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.06").compareTo(getPosition(positionId).getLatestPrice().getValue())));

        // Second ticker arrives (100% gain) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.02")).build());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.02").compareTo(getPosition(positionId).getLatestPrice().getValue())));

        // Third ticker arrives (200% gain) - nothing should change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.03")).build());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.03").compareTo(getPosition(positionId).getLatestPrice().getValue())));

        // Fourth ticker arrives (50% loss) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.005")).build());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.005").compareTo(getPosition(positionId).getLatestPrice().getValue())));

        // Firth ticker arrives (600% gain) - max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.07")).build());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.07").compareTo(getPosition(positionId).getLatestPrice().getValue())));

        // Check lowest & highest in database.
        await().untilAsserted(() -> assertEquals(6, strategy.getTickersUpdateReceived().size()));
        assertTrue(getPositionDTO(positionId).getLowestCalculatedGain().isPresent());
        assertTrue(getPositionDTO(positionId).getHighestCalculatedGain().isPresent());
        assertEquals(0, new BigDecimal("0.005").compareTo(getPositionDTO(positionId).getLowestPrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), getPositionDTO(positionId).getLowestPrice().getCurrency());
        assertEquals(0, new BigDecimal("0.07").compareTo(getPositionDTO(positionId).getHighestPrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), getPositionDTO(positionId).getHighestPrice().getCurrency());
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
        await().untilAsserted(() -> assertTrue(() -> orderRepository.findByOrderId("DRY_ORDER_000000002").isPresent()));

        // The first close trade arrives, status should not change as it's not the total amount.
        tradeFlux.emitValue(TradeDTO.builder().tradeId("000002")
                .orderId("DRY_ORDER_000000002")
                .type(ASK)
                .amount(new CurrencyAmountDTO("0.5", cp1.getBaseCurrency()))
                .currencyPair(cp1)
                .price(new CurrencyAmountDTO("1", cp1.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(1, getPositionDTO(positionId).getClosingTrades().size()));
        await().untilAsserted(() -> assertEquals(12, tradeRepository.count()));
        assertEquals(CLOSING, getPositionDTO(positionId).getStatus());

        // The second close trade arrives, status should change
        tradeFlux.emitValue(TradeDTO.builder().tradeId("000003")
                .orderId("DRY_ORDER_000000002")
                .type(ASK)
                .amount(new CurrencyAmountDTO("0.5", cp1.getBaseCurrency()))
                .currencyPair(cp1)
                .price(new CurrencyAmountDTO("1", cp1.getQuoteCurrency()))
                .build());
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
        assertEquals(0, new BigDecimal("1").compareTo(p.getAmount().getValue()));
        assertEquals(1000, p.getStopGainPercentageRule());
        assertEquals(100, p.getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p.getOpeningOrder().getOrderId());
        assertEquals("DRY_ORDER_000000002", p.getClosingOrder().getOrderId());
        assertEquals(1, p.getOpeningOrder().getTrades().size());
        assertEquals(2, p.getClosingOrder().getTrades().size());
        assertTrue(p.getOpeningOrder().getTrades().stream().anyMatch(t -> "DRY_TRADE_000000001".equals(t.getTradeId())));
        assertTrue(p.getClosingOrder().getTrades().stream().anyMatch(t -> "000002".equals(t.getTradeId())));
        assertTrue(p.getClosingOrder().getTrades().stream().anyMatch(t -> "000003".equals(t.getTradeId())));
        assertEquals(0, new BigDecimal("0.005").compareTo(p.getLowestPrice().getValue()));
        assertEquals(0, new BigDecimal("0.07").compareTo(p.getHighestPrice().getValue()));
        assertEquals(0, new BigDecimal("0.07").compareTo(p.getLatestPrice().getValue()));
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
