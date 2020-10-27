package tech.cassandre.trading.bot.test.backup;

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
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Modes.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Backup - Positions")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql"),
        @Property(key = "spring.jpa.hibernate.ddl-auto", value = "create-drop"),
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PositionBackupTest extends BaseTest {

    public static final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private PositionService positionService;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PositionFlux positionFlux;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Test
    @DisplayName("Check restored positions")
    public void checkRestoredPositions() {
        // =============================================================================================================
        // Check that positions and restored in strategy, services & flux.strategy.getPositionService().getPositions().size()
        assertTrue(strategy.getPositionService().getPositions().size() >= 5);
        assertTrue(strategy.getPositions().size() >= 5);
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());

        // Check position 1 - OPENING.
        PositionDTO p = strategy.getPositions().get(1L);
        assertNotNull(p);
        assertEquals(OPENING, p.getStatus());
        assertEquals(1L, p.getId());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), p.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p.getAmount()));
        assertFalse(p.getRules().isStopGainPercentageSet());
        assertFalse(p.getRules().isStopLossPercentageSet());
        assertEquals("BACKUP_OPEN_ORDER_01", p.getOpenOrderId());
        assertTrue(p.getOpenTrades().isEmpty());
        assertNull(p.getCloseOrderId());
        assertTrue(p.getCloseTrades().isEmpty());

        // Check position 2 - OPENED.
        p = strategy.getPositions().get(2L);
        assertNotNull(p);
        assertEquals(OPENED, p.getStatus());
        assertEquals(2L, p.getId());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), p.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(p.getAmount()));
        assertTrue(p.getRules().isStopGainPercentageSet());
        assertEquals(10, p.getRules().getStopGainPercentage());
        assertFalse(p.getRules().isStopLossPercentageSet());
        assertEquals("BACKUP_OPEN_ORDER_02", p.getOpenOrderId());
        assertEquals(1, p.getOpenTrades().size());
        assertTrue(p.getTrade("BACKUP_TRADE_01").isPresent());
        assertNull(p.getCloseOrderId());
        assertTrue(p.getCloseTrades().isEmpty());

        // Check position 3 - CLOSING.
        p = strategy.getPositions().get(3L);
        assertNotNull(p);
        assertEquals(CLOSING, p.getStatus());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), p.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(p.getAmount()));
        assertEquals(3L, p.getId());
        assertFalse(p.getRules().isStopGainPercentageSet());
        assertTrue(p.getRules().isStopLossPercentageSet());
        assertEquals(20, p.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPEN_ORDER_03", p.getOpenOrderId());
        assertEquals(1, p.getOpenTrades().size());
        assertTrue(p.getTrade("BACKUP_TRADE_02").isPresent());
        assertEquals("NON_EXISTING_TRADE", p.getCloseOrderId());
        assertTrue(p.getCloseTrades().isEmpty());

        // Check position 4 - CLOSED.
        p = strategy.getPositions().get(4L);
        assertNotNull(p);
        assertEquals(CLOSED, p.getStatus());
        assertEquals(new CurrencyPairDTO("BTC/USDT"), p.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(p.getAmount()));
        assertEquals(4L, p.getId());
        assertTrue(p.getRules().isStopGainPercentageSet());
        assertEquals(30, p.getRules().getStopGainPercentage());
        assertTrue(p.getRules().isStopLossPercentageSet());
        assertEquals(40, p.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPEN_ORDER_04", p.getOpenOrderId());
        assertEquals(1, p.getOpenTrades().size());
        assertTrue(p.getTrade("BACKUP_TRADE_03").isPresent());
        assertEquals("BACKUP_OPEN_ORDER_05", p.getCloseOrderId());
        assertEquals(1, p.getCloseTrades().size());
        assertTrue(p.getTrade("BACKUP_TRADE_04").isPresent());

        // Check position 5 - CLOSED with several trades.
        p = strategy.getPositions().get(5L);
        assertEquals(5L, p.getId());
        assertEquals(CLOSED, p.getStatus());
        assertEquals(new CurrencyPairDTO("ETH/USD"), p.getCurrencyPair());
        assertEquals(0, new BigDecimal("50").compareTo(p.getAmount()));
        assertTrue(p.getRules().isStopGainPercentageSet());
        assertEquals(30, p.getRules().getStopGainPercentage());
        assertTrue(p.getRules().isStopLossPercentageSet());
        assertEquals(40, p.getRules().getStopLossPercentage());
        assertEquals("OPEN_ORDER_01", p.getOpenOrderId());
        assertEquals("CLOSE_ORDER_01", p.getCloseOrderId());
        assertEquals(0, new BigDecimal("17").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("68").compareTo(p.getHighestPrice()));
        // Open trades.
        assertEquals(2, p.getOpenTrades().size());
        assertTrue(p.getTrade("TRADE_01").isPresent());
        assertEquals( "TRADE_01", p.getTrade("TRADE_01").get().getId());
        assertTrue(p.getTrade("TRADE_02").isPresent());
        assertEquals( "TRADE_02", p.getTrade("TRADE_02").get().getId());
        // Close trades.
        assertTrue(p.getTrade("TRADE_03").isPresent());
        assertEquals( "TRADE_03", p.getTrade("TRADE_03").get().getId());
        assertTrue(p.getTrade("TRADE_04").isPresent());
        assertEquals( "TRADE_04", p.getTrade("TRADE_04").get().getId());
        assertTrue(p.getTrade("TRADE_05").isPresent());
        assertEquals( "TRADE_05", p.getTrade("TRADE_05").get().getId());
    }

    @Test
    @DisplayName("Check how a new positions is saved")
    public void checkSavedNewPosition() {
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp).timestamp(createDay(1)).last(new BigDecimal("0.2")).create());

        // =============================================================================================================
        // Add a position.
        long positionCount = positionRepository.count();
        PositionRulesDTO rules = PositionRulesDTO.builder().stopGainPercentage(1).stopLossPercentage(2).create();
        PositionCreationResultDTO creationResult1 = positionService.createPosition(cp, new BigDecimal("0.0001"), rules);
        assertTrue(creationResult1.isSuccessful());
        assertEquals("DRY_ORDER_000000001", creationResult1.getOrderId());

        // Check the position created.
        await().untilAsserted(() -> assertEquals(positionCount + 1, positionRepository.count()));
        Optional<Position> p = positionRepository.findById(6L);
        assertTrue(p.isPresent());
        assertEquals(6L, p.get().getId());
        assertEquals(p.get().getStatus(), OPENING.toString());
        assertEquals(cp.toString(), p.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0001").compareTo(p.get().getAmount()));
        assertEquals(1, p.get().getStopGainPercentageRule());
        assertEquals(2, p.get().getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p.get().getOpenOrderId());
        assertNull(p.get().getCloseOrderId());

        // =============================================================================================================
        // Add another position.
        PositionCreationResultDTO creationResult2 = positionService.createPosition(cp, new BigDecimal("0.0002"), PositionRulesDTO.builder().create());
        assertTrue(creationResult2.isSuccessful());
        assertEquals("DRY_ORDER_000000002", creationResult2.getOrderId());

        // Check the position created.
        await().untilAsserted(() -> assertEquals(positionCount + 2, positionRepository.count()));
        p = positionRepository.findById(7L);
        assertTrue(p.isPresent());
        assertEquals(7L, p.get().getId());
        assertEquals(p.get().getStatus(), OPENING.toString());
        assertEquals(cp.toString(), p.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0002").compareTo(p.get().getAmount()));
        assertNull(p.get().getStopGainPercentageRule());
        assertNull(p.get().getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000002", p.get().getOpenOrderId());
        assertNull(p.get().getCloseOrderId());

        // =============================================================================================================
        // Position 2 is opened - try to close it with CLOSE_ORDER_FOR_POSITION_2.

        // Closing the position - order created but trade not arrived.
        Optional<PositionDTO> positionDTO2 = positionService.getPositionById(2L);
        assertTrue(positionDTO2.isPresent());
        assertEquals(OPENED, positionDTO2.get().getStatus());
        assertEquals(cp.toString(), p.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("0.0002").compareTo(p.get().getAmount()));
        positionDTO2.get().setCloseOrderId("CLOSE_ORDER_FOR_POSITION_2");
        assertEquals(CLOSING, positionDTO2.get().getStatus());
        positionFlux.emitValue(positionDTO2.get());
        assertTrue(positionRepository.findById(2L).isPresent());
        await().untilAsserted(() -> assertEquals("CLOSE_ORDER_FOR_POSITION_2", positionRepository.findById(2L).get().getCloseOrderId()));
    }

    @Test
    @DisplayName("Check saved data during position lifecycle")
    public void checkSavedDataDuringPositionLifecycle() {
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp).timestamp(createDay(1)).last(new BigDecimal("0.01")).create());
        // A position is opening on ETH/BTC.
        // We buy 1 ETH for 100 BTC.
        final PositionCreationResultDTO positionResult = positionService.createPosition(cp,
                new BigDecimal("1"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1000)   // 1 000% max gain.
                        .stopLossPercentage(100)    // 100% max lost.
                        .create());

        // We retrieve the position id and the position.
        final long positionId = positionResult.getPositionId();
        final Optional<PositionDTO> positionDTO = positionService.getPositionById(positionId);
        assertTrue(positionDTO.isPresent());
        assertTrue(positionRepository.findById(positionId).isPresent());

        // Two tickers arrived - min and max gain should not be set.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp).last(new BigDecimal("100")).create());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp).last(new BigDecimal("0.01")).create());

        // Check saved position.
        positionFlux.emitValue(positionDTO.get());
        await().until(() -> positionRepository.findById(positionId).isPresent() &&
                positionRepository.findById(positionId).get().getStatus().equals(OPENING.toString()));
        Optional<Position> p = positionRepository.findById(positionId);
        assertTrue(p.isPresent());
        assertEquals(6, p.get().getId());
        assertEquals(OPENING.toString(), p.get().getStatus());
        assertEquals(1000, p.get().getStopGainPercentageRule());
        assertEquals(100, p.get().getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p.get().getOpenOrderId());
        assertNull(p.get().getCloseOrderId());
        assertNull(p.get().getLowestPrice());
        assertNull(p.get().getHighestPrice());

        // Trade arrives, position will be opened.
        tradeFlux.emitValue(TradeDTO.builder().id("000001")
                .orderId("DRY_ORDER_000000001")
                .type(BID)
                .currencyPair(cp)
                .originalAmount(new BigDecimal("1"))
                .price(new BigDecimal("0.03"))
                .create());
        await().untilAsserted(() -> assertEquals(OPENED, positionDTO.get().getStatus()));
        positionFlux.emitValue(positionDTO.get());
        positionFlux.update();

        // Check saved position.
        await().until(() -> positionRepository.findById(positionId).isPresent() &&
                positionRepository.findById(positionId).get().getStatus().equals(OPENED.toString()));
        p = positionRepository.findById(positionId);
        assertTrue(p.isPresent());
        assertEquals(6, p.get().getId());
        assertEquals(OPENED.toString(), p.get().getStatus());
        assertEquals(1000, p.get().getStopGainPercentageRule());
        assertEquals(100, p.get().getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p.get().getOpenOrderId());
        assertNull(p.get().getCloseOrderId());
        assertNull(p.get().getLowestPrice());
        assertNull(p.get().getHighestPrice());

        // First ticker arrives (500% gain) - min and max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp).last(new BigDecimal("0.06")).create());
        // Second ticker arrives (100% gain) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp).last(new BigDecimal("0.02")).create());
        // Third ticker arrives (200% gain) - nothing should change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp).last(new BigDecimal("0.03")).create());
        // Fourth ticker arrives (50% loss) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp).last(new BigDecimal("0.005")).create());
        // Firth ticker arrives (600% gain) - max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp).last(new BigDecimal("0.07")).create());
        await().until(() -> strategy.getTickersUpdateReceived().size() == 8);

        assertTrue(positionDTO.get().getLowestCalculatedGain().isPresent());
        assertTrue(positionDTO.get().getHighestCalculatedGain().isPresent());
        assertEquals(-50, positionDTO.get().getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, positionDTO.get().getHighestCalculatedGain().get().getPercentage());

        // Closing the trade - min and max should not change.
        positionDTO.get().setCloseOrderId("DRY_ORDER_000000002");
        // tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp).last(new BigDecimal("100")).create());
        // await().untilAsserted(() -> assertEquals(CLOSING, positionDTO.get().getStatus()));
        // assertEquals(CLOSING, positionDTO.get().getStatus());

        // The first close trade arrives, status should not change
        tradeFlux.emitValue(TradeDTO.builder().id("000002")
                .orderId("DRY_ORDER_000000002")
                .type(ASK)
                .originalAmount(new BigDecimal("0.5"))
                .currencyPair(cp)
                .price(new BigDecimal("1"))
                .create());
        await().untilAsserted(() -> assertEquals(1, positionDTO.get().getCloseTrades().size()));
        assertEquals(CLOSING, positionDTO.get().getStatus());

        // The second close trade arrives, status should change
        tradeFlux.emitValue(TradeDTO.builder().id("000003")
                .orderId("DRY_ORDER_000000002")
                .type(ASK)
                .originalAmount(new BigDecimal("0.5"))
                .currencyPair(cp)
                .price(new BigDecimal("1"))
                .create());
        await().untilAsserted(() -> assertEquals(2, positionDTO.get().getCloseTrades().size()));
        await().untilAsserted(() -> assertEquals(CLOSED, positionDTO.get().getStatus()));

        // Check saved position.
        positionFlux.emitValue(positionDTO.get());
        await().until(() -> positionRepository.findById(positionId).isPresent() &&
                positionRepository.findById(positionId).get().getStatus().equals(CLOSED.toString()));
        p = positionRepository.findById(positionId);
        assertTrue(p.isPresent());
        assertEquals(6, p.get().getId());
        assertEquals(CLOSED.toString(), p.get().getStatus());
        assertEquals(cp.toString(), p.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("1").compareTo(p.get().getAmount()));
        assertEquals(1000, p.get().getStopGainPercentageRule());
        assertEquals(100, p.get().getStopLossPercentageRule());
        assertEquals("DRY_ORDER_000000001", p.get().getOpenOrderId());
        assertEquals("DRY_ORDER_000000002", p.get().getCloseOrderId());
        assertEquals(3, p.get().getTrades().size());
        assertTrue(p.get().getTrades().stream().anyMatch(t -> "DRY_TRADE_000000001".equals(t.getId())));
        assertTrue(p.get().getTrades().stream().anyMatch(t -> "000002".equals(t.getId())));
        assertTrue(p.get().getTrades().stream().anyMatch(t -> "000003".equals(t.getId())));
        assertEquals(0, new BigDecimal("0.005").compareTo(p.get().getLowestPrice()));
        assertEquals(0, new BigDecimal("0.07").compareTo(p.get().getHighestPrice()));
    }

}
