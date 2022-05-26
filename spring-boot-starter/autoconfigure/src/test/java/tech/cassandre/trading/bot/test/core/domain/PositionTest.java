package tech.cassandre.trading.bot.test.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;
import tech.cassandre.trading.bot.util.exception.PositionException;

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
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Domain - Position")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true"),
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/test/core/backup.yaml"),
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class PositionTest extends BaseTest {

    @Autowired
    private OrderFlux orderFlux;

    @Autowired
    private PositionFlux positionFlux;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PositionService positionService;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check load position from database")
    public void checkLoadPositionFromDatabase() {
        // =============================================================================================================
        // Check position 1 - OPENING.
        PositionDTO position1 = strategy.getPositions().get(1L);
        assertNotNull(position1);
        assertEquals(1L, position1.getUid());
        assertEquals(1L, position1.getPositionId());
        assertEquals(LONG, position1.getType());
        assertNotNull(position1.getStrategy());
        assertEquals(1, position1.getStrategy().getUid());
        assertEquals("01", position1.getStrategy().getStrategyId());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), position1.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(position1.getAmount().getValue()));
        assertEquals(BTC, position1.getAmount().getCurrency());
        assertFalse(position1.getRules().isStopGainPercentageSet());
        assertFalse(position1.getRules().isStopLossPercentageSet());
        assertEquals(OPENING, position1.getStatus());
        assertEquals("BACKUP_OPENING_ORDER_01", position1.getOpeningOrder().getOrderId());
        assertTrue(position1.getOpeningOrder().getTrades().isEmpty());
        assertNull(position1.getClosingOrder());
        assertNull(position1.getLowestGainPrice());
        assertNull(position1.getHighestGainPrice());
        assertNull(position1.getLatestGainPrice());

        // Test equals.
        Optional<PositionDTO> position1Bis = strategy.getPositionByPositionId(1L);
        assertTrue(position1Bis.isPresent());
        assertEquals(position1, position1Bis.get());

        // =============================================================================================================
        // Check position 2 - OPENED.
        PositionDTO position2 = strategy.getPositions().get(2L);
        assertNotNull(position2);
        assertEquals(2L, position2.getUid());
        assertEquals(2L, position2.getPositionId());
        assertEquals(LONG, position2.getType());
        assertNotNull(position2.getStrategy());
        assertEquals(1, position2.getStrategy().getUid());
        assertEquals("01", position2.getStrategy().getStrategyId());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), position2.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(position2.getAmount().getValue()));
        assertEquals(BTC, position2.getAmount().getCurrency());
        assertTrue(position2.getRules().isStopGainPercentageSet());
        assertEquals(10, position2.getRules().getStopGainPercentage());
        assertFalse(position2.getRules().isStopLossPercentageSet());
        assertEquals(OPENED, position2.getStatus());
        assertEquals("BACKUP_OPENING_ORDER_02", position2.getOpeningOrder().getOrderId());
        assertEquals(1, position2.getOpeningOrder().getTrades().size());
        assertTrue(position2.getOpeningOrder().getTrade("BACKUP_TRADE_01").isPresent());
        assertNull(position2.getClosingOrder());
        assertEquals(0, new BigDecimal("1").compareTo(position2.getLowestGainPrice().getValue()));
        assertEquals(USDT, position2.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("2").compareTo(position2.getHighestGainPrice().getValue()));
        assertEquals(USDT, position2.getHighestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("3").compareTo(position2.getLatestGainPrice().getValue()));
        assertEquals(USDT, position2.getLatestGainPrice().getCurrency());

        // =============================================================================================================
        // Check position 3 - CLOSING.
        PositionDTO position3 = strategy.getPositions().get(3L);
        assertNotNull(position3);
        assertEquals(3L, position3.getUid());
        assertEquals(3L, position3.getPositionId());
        assertEquals(LONG, position3.getType());
        assertNotNull(position3.getStrategy());
        assertEquals(1, position3.getStrategy().getUid());
        assertEquals("01", position3.getStrategy().getStrategyId());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), position3.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(position3.getAmount().getValue()));
        assertEquals(BTC, position3.getAmount().getCurrency());
        assertFalse(position3.getRules().isStopGainPercentageSet());
        assertTrue(position3.getRules().isStopLossPercentageSet());
        assertEquals(20, position3.getRules().getStopLossPercentage());
        assertEquals(CLOSING, position3.getStatus());
        assertEquals("BACKUP_OPENING_ORDER_03", position3.getOpeningOrder().getOrderId());
        assertEquals(1, position3.getOpeningOrder().getTrades().size());
        assertTrue(position3.getOpeningOrder().getTrade("BACKUP_TRADE_02").isPresent());
        assertEquals("BACKUP_CLOSING_ORDER_01", position3.getClosingOrder().getOrderId());
        assertEquals(1, position3.getClosingOrder().getTrades().size());
        assertTrue(position3.getClosingOrder().getTrade("BACKUP_TRADE_04").isPresent());
        assertEquals(0, new BigDecimal("17").compareTo(position3.getLowestGainPrice().getValue()));
        assertEquals(USDT, position3.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("68").compareTo(position3.getHighestGainPrice().getValue()));
        assertEquals(USDT, position3.getHighestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("92").compareTo(position3.getLatestGainPrice().getValue()));
        assertEquals(USDT, position3.getLatestGainPrice().getCurrency());

        // =============================================================================================================
        // Check position 4 - CLOSED.
        PositionDTO position4 = strategy.getPositions().get(4L);
        assertNotNull(position4);
        assertEquals(4L, position4.getUid());
        assertEquals(4L, position4.getPositionId());
        assertEquals(LONG, position4.getType());
        assertNotNull(position4.getStrategy());
        assertEquals(1, position4.getStrategy().getUid());
        assertEquals("01", position4.getStrategy().getStrategyId());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), position4.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(position4.getAmount().getValue()));
        assertEquals(BTC, position4.getAmount().getCurrency());
        assertTrue(position4.getRules().isStopGainPercentageSet());
        assertEquals(30, position4.getRules().getStopGainPercentage());
        assertTrue(position4.getRules().isStopLossPercentageSet());
        assertEquals(40, position4.getRules().getStopLossPercentage());
        assertEquals(CLOSED, position4.getStatus());
        assertEquals("BACKUP_OPENING_ORDER_04", position4.getOpeningOrder().getOrderId());
        assertEquals(1, position4.getOpeningOrder().getTrades().size());
        assertTrue(position4.getOpeningOrder().getTrade("BACKUP_TRADE_03").isPresent());
        assertEquals("BACKUP_CLOSING_ORDER_02", position4.getClosingOrder().getOrderId());
        assertEquals(1, position4.getClosingOrder().getTrades().size());
        assertTrue(position4.getClosingOrder().getTrade("BACKUP_TRADE_05").isPresent());
        assertEquals(0, new BigDecimal("17").compareTo(position4.getLowestGainPrice().getValue()));
        assertEquals(USDT, position4.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("68").compareTo(position4.getHighestGainPrice().getValue()));
        assertEquals(USDT, position4.getHighestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("93").compareTo(position4.getLatestGainPrice().getValue()));
        assertEquals(USDT, position4.getLatestGainPrice().getCurrency());

        // =============================================================================================================
        // Check position 5 - CLOSED with several trades.
        PositionDTO position5 = strategy.getPositions().get(5L);
        assertNotNull(position5);
        assertEquals(5L, position5.getUid());
        assertEquals(5L, position5.getPositionId());
        assertEquals(LONG, position5.getType());
        assertNotNull(position5.getStrategy());
        assertEquals(1, position5.getStrategy().getUid());
        assertEquals("01", position5.getStrategy().getStrategyId());
        assertEquals(new CurrencyPairDTO("ETH/USD"), position5.getCurrencyPair());
        assertEquals(0, new BigDecimal("50").compareTo(position5.getAmount().getValue()));
        assertEquals(ETH, position5.getAmount().getCurrency());
        assertTrue(position5.getRules().isStopGainPercentageSet());
        assertEquals(30, position5.getRules().getStopGainPercentage());
        assertTrue(position5.getRules().isStopLossPercentageSet());
        assertEquals(40, position5.getRules().getStopLossPercentage());
        assertEquals(CLOSED, position5.getStatus());
        assertEquals("BACKUP_OPENING_ORDER_05", position5.getOpeningOrder().getOrderId());
        assertEquals("BACKUP_CLOSING_ORDER_03", position5.getClosingOrder().getOrderId());
        assertEquals(0, new BigDecimal("17").compareTo(position5.getLowestGainPrice().getValue()));
        assertEquals(USD, position5.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("68").compareTo(position5.getHighestGainPrice().getValue()));
        assertEquals(USD, position5.getHighestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("94").compareTo(position5.getLatestGainPrice().getValue()));
        assertEquals(USD, position5.getLatestGainPrice().getCurrency());
        // Open trades.
        assertEquals(2, position5.getOpeningOrder().getTrades().size());
        assertTrue(position5.getOpeningOrder().getTrade("BACKUP_TRADE_06").isPresent());
        assertEquals("BACKUP_TRADE_06", position5.getOpeningOrder().getTrade("BACKUP_TRADE_06").get().getTradeId());
        assertTrue(position5.getOpeningOrder().getTrades().stream().anyMatch(t -> "BACKUP_TRADE_06".equals(t.getTradeId())));
        assertTrue(position5.getOpeningOrder().getTrade("BACKUP_TRADE_07").isPresent());
        assertTrue(position5.getOpeningOrder().getTrades().stream().anyMatch(t -> "BACKUP_TRADE_07".equals(t.getTradeId())));
        assertEquals("BACKUP_TRADE_07", position5.getOpeningOrder().getTrade("BACKUP_TRADE_07").get().getTradeId());
        // Close trades.
        assertEquals(3, position5.getClosingOrder().getTrades().size());
        assertTrue(position5.getClosingOrder().getTrade("BACKUP_TRADE_08").isPresent());
        assertTrue(position5.getClosingOrder().getTrades().stream().anyMatch(t -> "BACKUP_TRADE_08".equals(t.getTradeId())));
        assertEquals("BACKUP_TRADE_08", position5.getClosingOrder().getTrade("BACKUP_TRADE_08").get().getTradeId());
        assertTrue(position5.getClosingOrder().getTrade("BACKUP_TRADE_09").isPresent());
        assertTrue(position5.getClosingOrder().getTrades().stream().anyMatch(t -> "BACKUP_TRADE_09".equals(t.getTradeId())));
        assertEquals("BACKUP_TRADE_09", position5.getClosingOrder().getTrade("BACKUP_TRADE_09").get().getTradeId());
        assertTrue(position5.getClosingOrder().getTrade("BACKUP_TRADE_10").isPresent());
        assertTrue(position5.getClosingOrder().getTrades().stream().anyMatch(t -> "BACKUP_TRADE_10".equals(t.getTradeId())));
        assertEquals("BACKUP_TRADE_10", position5.getClosingOrder().getTrade("BACKUP_TRADE_10").get().getTradeId());
        // Check trade orders.
        final Iterator<TradeDTO> openTradesIterator = position5.getOpeningOrder().getTrades().iterator();
        assertEquals("BACKUP_TRADE_06", openTradesIterator.next().getTradeId());
        assertEquals("BACKUP_TRADE_07", openTradesIterator.next().getTradeId());
        final Iterator<TradeDTO> closeTradesIterator = position5.getClosingOrder().getTrades().iterator();
        assertEquals("BACKUP_TRADE_08", closeTradesIterator.next().getTradeId());
        assertEquals("BACKUP_TRADE_09", closeTradesIterator.next().getTradeId());
        assertEquals("BACKUP_TRADE_10", closeTradesIterator.next().getTradeId());
    }

    @Test
    @DisplayName("Check how a new position is saved")
    public void checkSavedNewPosition() {
        // First ticker emitted for dry mode - MANDATORY.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).timestamp(createZonedDateTime(1)).last(new BigDecimal("0.2")).build());
        await().untilAsserted(() -> assertEquals(1, strategy.getLastTickers().size()));

        // =============================================================================================================
        // Creates a position with ID 6 - waiting for order DRY_ORDER_000000001.
        long positionCount = positionRepository.count();
        PositionRulesDTO rules = PositionRulesDTO.builder().stopGainPercentage(1f).stopLossPercentage(2f).build();
        PositionCreationResultDTO creationResult1 = strategy.createLongPosition(ETH_BTC, new BigDecimal("0.0001"), rules);
        assertTrue(creationResult1.isSuccessful());
        assertEquals(6, creationResult1.getPosition().getUid());
        assertEquals("DRY_ORDER_000000001", creationResult1.getPosition().getOpeningOrder().getOrderId());

        // Check that the position was correctly created.
        // The corresponding order and trade will arrive in few moments.
        // In the meantime, the position should be in OPENING status.
        await().untilAsserted(() -> assertEquals(positionCount + 1, positionRepository.count()));
        Position p6 = getPosition(6L);
        assertEquals(6L, p6.getUid());
        assertEquals(6L, p6.getPositionId());
        assertEquals(LONG, p6.getType());
        assertNotNull(p6.getStrategy());
        assertEquals(1, p6.getStrategy().getUid());
        assertEquals("01", p6.getStrategy().getStrategyId());
        assertEquals(ETH_BTC.toString(), p6.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0001").compareTo(p6.getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency().toString(), p6.getAmount().getCurrency());
        assertEquals(1, p6.getStopGainPercentageRule());
        assertEquals(2, p6.getStopLossPercentageRule());
        assertEquals(OPENING, p6.getStatus());
        assertEquals("DRY_ORDER_000000001", p6.getOpeningOrder().getOrderId());
        assertEquals("DRY_ORDER_000000001", p6.getOpeningOrder().getOrderId());
        assertNull(p6.getClosingOrder());

        // If we wait a bit, the order and trade will arrive and the position status will move to OPENED.
        await().untilAsserted(() -> {
            orderFlux.update();
            tradeFlux.update();
            assertEquals(OPENED, getPosition(6L).getStatus());
        });

        p6 = getPosition(6L);
        assertEquals(6L, p6.getUid());
        assertEquals(6L, p6.getPositionId());
        assertEquals(LONG, p6.getType());
        assertNotNull(p6.getStrategy());
        assertEquals(1, p6.getStrategy().getUid());
        assertEquals("01", p6.getStrategy().getStrategyId());
        assertEquals(ETH_BTC.toString(), p6.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0001").compareTo(p6.getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency().toString(), p6.getAmount().getCurrency());
        assertEquals(1, p6.getStopGainPercentageRule());
        assertEquals(2, p6.getStopLossPercentageRule());
        assertEquals(OPENED, p6.getStatus());
        assertNotNull(p6.getOpeningOrder());
        assertFalse(p6.getOpeningOrder().getTrades().isEmpty());
        assertTrue(p6.getOpeningOrder().getTrades().stream().anyMatch(t -> "DRY_TRADE_000000001".equals(t.getTradeId())));

        // =============================================================================================================
        // Creates a position with ID to 7.
        PositionCreationResultDTO creationResult2 = strategy.createLongPosition(ETH_BTC, new BigDecimal("0.0002"), PositionRulesDTO.builder().build());
        assertTrue(creationResult2.isSuccessful());
        assertEquals("DRY_ORDER_000000002", creationResult2.getPosition().getOpeningOrder().getOrderId());

        // Check the created position in database.
        await().untilAsserted(() -> assertEquals(positionCount + 2, positionRepository.count()));
        Position p7 = getPosition(7L);
        assertEquals(7L, p7.getUid());
        assertEquals(7L, p7.getPositionId());
        assertEquals(LONG, p7.getType());
        assertNotNull(p7.getStrategy());
        assertEquals(1, p7.getStrategy().getUid());
        assertEquals("01", p7.getStrategy().getStrategyId());
        assertEquals(ETH_BTC.toString(), p7.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0002").compareTo(p7.getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency().toString(), p7.getAmount().getCurrency());
        assertNull(p7.getStopGainPercentageRule());
        assertNull(p7.getStopLossPercentageRule());
        assertEquals(OPENING, p7.getStatus());
        assertEquals("DRY_ORDER_000000002", p7.getOpeningOrder().getOrderId());
        assertNull(p7.getClosingOrder());
    }

    @Test
    @DisplayName("Check saved data during position lifecycle")
    public void checkSavedDataDuringPositionLifecycle() {
        // First ticker emitted for dry mode - MANDATORY.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).timestamp(createZonedDateTime(1)).last(new BigDecimal("0.01")).build());
        await().untilAsserted(() -> assertEquals(1, strategy.getTickersUpdatesReceived().size()));

        // =============================================================================================================
        // A position is created on ETH/BTC - ID 6.
        // We buy 1 ETH for 0.01 BTC.
        // Waiting for order DRY_ORDER_000000001.
        final PositionCreationResultDTO positionResult = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("1"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1000f)   // 1 000% max gain.
                        .stopLossPercentage(100f)    // 100% max lost.
                        .build());
        assertTrue(positionResult.isSuccessful());
        final long positionId = positionResult.getPosition().getUid();

        // =============================================================================================================
        // Still "OPENING".
        // Two tickers arrived - min and max gain should not be set as the position is still in OPENING status.

        // Check that the position was correctly created.
        // The corresponding order and trade will arrive in few seconds.
        // In the meantime, the position should be in OPENING status.
        await().untilAsserted(() -> assertEquals(OPENING, getPosition(positionId).getStatus()));
        Position p = getPosition(positionId);
        assertEquals(positionId, p.getUid());
        assertEquals(positionId, p.getPositionId());
        assertEquals(LONG, p.getType());
        assertEquals(1, p.getStrategy().getUid());
        assertEquals("01", p.getStrategy().getStrategyId());
        assertEquals(ETH_BTC.toString(), p.getCurrencyPair());
        assertEquals(0, new BigDecimal("1").compareTo(p.getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency().toString(), p.getAmount().getCurrency());
        assertEquals(1000, p.getStopGainPercentageRule());
        assertEquals(100, p.getStopLossPercentageRule());
        assertEquals(OPENING, p.getStatus());
        assertEquals("DRY_ORDER_000000001", p.getOpeningOrder().getOrderId());
        assertNull(p.getClosingOrder());
        assertNull(p.getClosingOrder());
        assertNull(p.getLowestGainPrice());
        assertNull(p.getHighestGainPrice());
        ZonedDateTime createdOn = p.getCreatedOn();
        ZonedDateTime updatedON = p.getUpdatedOn();
        assertNotNull(createdOn);
        assertNotNull(updatedON);

        // We should have one more position and one more trade in database.
        await().untilAsserted(() -> assertEquals(6, positionRepository.count()));

        // =============================================================================================================
        // The position should now be OPENED.
        // We are in dry mode, we wait for order and trade to arrive, position will now be opened.
        await().untilAsserted(() -> {
            orderFlux.update();
            tradeFlux.update();
            assertEquals(OPENED, getPosition(positionId).getStatus());
        });

        // Check the position data in database.
        p = getPosition(positionId);
        assertEquals(positionId, p.getUid());
        assertEquals(positionId, p.getPositionId());
        assertEquals(LONG, p.getType());
        assertEquals(1, p.getStrategy().getUid());
        assertEquals("01", p.getStrategy().getStrategyId());
        assertEquals(ETH_BTC.toString(), p.getCurrencyPair());
        assertEquals(0, new BigDecimal("1").compareTo(p.getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency().toString(), p.getAmount().getCurrency());
        assertEquals(1000, p.getStopGainPercentageRule());
        assertEquals(100, p.getStopLossPercentageRule());
        assertEquals(OPENED, p.getStatus());
        assertEquals("DRY_ORDER_000000001", p.getOpeningOrder().getOrderId());
        assertFalse(p.getOpeningOrder().getTrades().isEmpty());
        assertTrue(p.getOpeningOrder().getTrades().stream().anyMatch(t -> "DRY_TRADE_000000001".equals(t.getTradeId())));
        assertNull(p.getClosingOrder());

        // =============================================================================================================
        // Now that the position is OPENED, we are sending tickers to see if lowest, highest and latest price change.

        // First ticker arrives (500% gain) - min and max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.06")).build());
        await().untilAsserted(() -> assertNotNull(getPosition(positionId).getLatestGainPrice()));
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.06").compareTo(getPosition(positionId).getLatestGainPrice().getValue())));

        // Second ticker arrives (100% gain) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.02")).build());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.02").compareTo(getPosition(positionId).getLatestGainPrice().getValue())));

        // Third ticker arrives (200% gain) - nothing should change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.03")).build());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.03").compareTo(getPosition(positionId).getLatestGainPrice().getValue())));

        // Fourth ticker arrives (50% loss) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.005")).build());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.005").compareTo(getPosition(positionId).getLatestGainPrice().getValue())));

        // Fifth ticker arrives (600% gain) - max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("0.07")).build());
        await().untilAsserted(() -> assertEquals(0, new BigDecimal("0.07").compareTo(getPosition(positionId).getLatestGainPrice().getValue())));

        // Check lowest & highest in database.
        await().untilAsserted(() -> assertEquals(6, strategy.getTickersUpdatesReceived().size()));
        assertTrue(getPositionDTO(positionId).getLowestCalculatedGain().isPresent());
        assertTrue(getPositionDTO(positionId).getHighestCalculatedGain().isPresent());
        assertEquals(0, new BigDecimal("0.005").compareTo(getPositionDTO(positionId).getLowestGainPrice().getValue()));
        assertEquals(ETH_BTC.getQuoteCurrency(), getPositionDTO(positionId).getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("0.07").compareTo(getPositionDTO(positionId).getHighestGainPrice().getValue()));
        assertEquals(ETH_BTC.getQuoteCurrency(), getPositionDTO(positionId).getHighestGainPrice().getCurrency());
        assertEquals(-50, getPositionDTO(positionId).getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, getPositionDTO(positionId).getHighestCalculatedGain().get().getPercentage());

        // Check that the new data was inserted in database.
        await().untilAsserted(() -> assertEquals(6, positionRepository.count()));
        assertEquals(createdOn, getPosition(positionId).getCreatedOn());
        assertTrue(updatedON.isBefore(getPosition(positionId).getUpdatedOn()));

        // =============================================================================================================
        // The status should now be CLOSING. We are going to receive two trades to close.
        // Closing the trade - min and max should not change.
        PositionDTO pDTO = getPositionDTO(positionId);
        final OrderDTO order2 = OrderDTO.builder()
                .orderId("DRY_ORDER_000000002")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1", ETH_BTC.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("1.00003", ETH_BTC.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("1.00005", ETH_BTC.getQuoteCurrency()))
                .leverage("leverage3")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("1.00002", ETH_BTC.getBaseCurrency()))
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .build();
        pDTO.closePositionWithOrder(order2);
        positionFlux.emitValue(pDTO);
        orderFlux.emitValue(order2);
        await().untilAsserted(() -> assertTrue(() -> orderRepository.findByOrderId("DRY_ORDER_000000002").isPresent()));
        positionFlux.emitValue(pDTO);

        // The first close trade arrives, status should not change as it's not the total amount.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000002")
                .type(ASK)
                .orderId("DRY_ORDER_000000002")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("0.5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("1", ETH_BTC.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(1, getPositionDTO(positionId).getClosingOrder().getTrades().size()));
        assertEquals(CLOSING, getPositionDTO(positionId).getStatus());

        // The second close trade arrives, status should change as the total of trades quantities equals order quantity.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000003")
                .type(ASK)
                .orderId("DRY_ORDER_000000002")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("0.5", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("1", ETH_BTC.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(2, getPositionDTO(positionId).getClosingOrder().getTrades().size()));
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(positionId).getStatus()));

        // =============================================================================================================
        // We should now be CLOSED as we received the two trades.
        // Check saved position.
        await().until(() -> getPosition(positionId).getStatus().equals(CLOSED));
        p = getPosition(positionId);
        assertEquals(positionId, p.getUid());
        assertEquals(positionId, p.getPositionId());
        assertEquals(LONG, p.getType());
        assertEquals(1, p.getStrategy().getUid());
        assertEquals("01", p.getStrategy().getStrategyId());
        assertEquals(ETH_BTC.toString(), p.getCurrencyPair());
        assertEquals(0, new BigDecimal("1").compareTo(p.getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency().toString(), p.getAmount().getCurrency());
        assertEquals(1000, p.getStopGainPercentageRule());
        assertEquals(100, p.getStopLossPercentageRule());
        assertEquals(CLOSED, p.getStatus());
        assertEquals("DRY_ORDER_000000001", p.getOpeningOrder().getOrderId());
        assertEquals("DRY_ORDER_000000002", p.getClosingOrder().getOrderId());
        assertEquals(1, p.getOpeningOrder().getTrades().size());
        assertEquals(2, p.getClosingOrder().getTrades().size());
        assertTrue(p.getOpeningOrder().getTrades().stream().anyMatch(t -> "DRY_TRADE_000000001".equals(t.getTradeId())));
        assertTrue(p.getClosingOrder().getTrades().stream().anyMatch(t -> "000002".equals(t.getTradeId())));
        assertTrue(p.getClosingOrder().getTrades().stream().anyMatch(t -> "000003".equals(t.getTradeId())));
        assertEquals(0, new BigDecimal("0.005").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.07").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals(0, new BigDecimal("0.07").compareTo(p.getLatestGainPrice().getValue()));
    }

    /**
     * Retrieve position from database.
     *
     * @param id position id
     * @return position
     */
    private Position getPosition(final long id) {
        final Optional<Position> p = positionRepository.findById(id);
        if (p.isPresent()) {
            return p.get();
        } else {
            throw new PositionException("Position not found : " + id);
        }
    }

    /**
     * Retrieve position from database.
     *
     * @param uid position uid
     * @return position
     */
    private PositionDTO getPositionDTO(final long uid) {
        final Optional<PositionDTO> p = positionService.getPositionByUid(uid);
        if (p.isPresent()) {
            return p.get();
        } else {
            throw new PositionException("Position not found : " + uid);
        }
    }

}
