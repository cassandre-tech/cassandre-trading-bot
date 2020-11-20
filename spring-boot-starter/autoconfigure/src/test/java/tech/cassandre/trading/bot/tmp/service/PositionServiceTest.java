package tech.cassandre.trading.bot.tmp.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.tmp.service.mocks.PositionServiceTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
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
@DisplayName("Services - Position service")
@ActiveProfiles("schedule-disabled")
@Configuration({
        @Property(key = "TEST_NAME", value = "Configuration parameters - Valid configuration")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(PositionServiceTestMock.class)
@Disabled
public class PositionServiceTest extends BaseTest {

    public static final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

    public static final CurrencyPairDTO cp2 = new CurrencyPairDTO(USD, BTC);

    @Autowired
    private PositionService positionService;

    @Autowired
    private TradeFlux tradeFlux;

    @Autowired
    private TickerFlux tickerFlux;

    @Test
    @Tag("notReviewed")
    @DisplayName("Check position creation")
    public void checkCreatePosition() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = positionService.createPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).create());
        assertTrue(p1.isSuccessful());
        assertEquals(1, p1.getPositionId());
        assertEquals("ORDER00010", p1.getOrderId());
        assertNull(p1.getErrorMessage());
        assertNull(p1.getException());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());

        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        final PositionCreationResultDTO p2 = positionService.createPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).create());
        assertTrue(p2.isSuccessful());
        assertEquals(2, p2.getPositionId());
        assertEquals("ORDER00020", p2.getOrderId());
        assertNull(p2.getErrorMessage());
        assertNull(p2.getException());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // Creates position 3 (ETH/BTC, 0.0003, 30% stop gain, 30% stop loss).
        final PositionCreationResultDTO p3 = positionService.createPosition(cp1,
                new BigDecimal("0.0003"),
                PositionRulesDTO.builder().stopGainPercentage(30f).stopLossPercentage(30f).create());
        assertFalse(p3.isSuccessful());
        assertNull(p3.getPositionId());
        assertNull(p3.getOrderId());
        assertEquals("Error message", p3.getErrorMessage());
        assertEquals("Error exception", p3.getException().getMessage());
        assertEquals(2, positionService.getPositions().size());
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check get positions and get positions by id")
    public void checkGetPosition() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        positionService.createPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).create());
        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        positionService.createPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).create());
        // Creates position 3 (ETH/BTC, 0.0003, 30% stop gain, 30% stop loss).
        positionService.createPosition(cp1,
                new BigDecimal("0.0003"),
                PositionRulesDTO.builder().stopGainPercentage(30f).stopLossPercentage(30f).create());

        // Tests.
        assertEquals(2, positionService.getPositions().size());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(1, positionService.getPositionById(1).get().getId());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(2, positionService.getPositionById(2).get().getId());
        assertFalse(positionService.getPositionById(3).isPresent());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    @Tag("notReviewed")
    @DisplayName("Check trade update")
    public void checkTradeUpdate() {
        // Creates position 1 (ETH/BTC, 0.0001, 10% stop gain).
        final PositionCreationResultDTO p1 = positionService.createPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(10f).create());
        assertEquals("ORDER00010", p1.getOrderId());
        assertTrue(positionService.getPositionById(1).isPresent());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());

        // Creates position 2 (ETH/BTC, 0.0002, 20% stop loss).
        final PositionCreationResultDTO p2 = positionService.createPosition(cp2,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopLossPercentage(20f).create());
        assertEquals("ORDER00020", p2.getOrderId());
        assertTrue(positionService.getPositionById(2).isPresent());
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // Trade 1 - should not change anything.
        tradeFlux.emitValue(TradeDTO.builder().id("000001").currencyPair(cp1).type(BID).orderId("ORDER00001").create());
        assertEquals(OPENING, positionService.getPositionById(1).get().getStatus());

        // Trade 2 - should change status of position 1.
        tradeFlux.emitValue(TradeDTO.builder().id("000002").currencyPair(cp1).type(BID).currencyPair(cp1).originalAmount(new BigDecimal("0.0001")).orderId("ORDER00010").create());
        await().untilAsserted(() -> assertEquals(OPENED, positionService.getPositionById(1).get().getStatus()));
        assertEquals(OPENING, positionService.getPositionById(2).get().getStatus());

        // Trade 3 - should change status of position 2.
        tradeFlux.emitValue(TradeDTO.builder().id("000002").currencyPair(cp1).type(BID).currencyPair(cp1).originalAmount(new BigDecimal("0.0002")).orderId("ORDER00020").create());
        assertEquals(OPENED, positionService.getPositionById(1).get().getStatus());
        await().untilAsserted(() -> assertEquals(OPENED, positionService.getPositionById(2).get().getStatus()));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    @Tag("notReviewed")
    @DisplayName("Check close position")
    public void checkClosePosition() throws InterruptedException {
        // =============================================================================================================
        // Creates position 1 (ETH/BTC, 0.0001, 100% stop gain).
        final PositionCreationResultDTO creationResult1 = positionService.createPosition(cp1,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(100f).create());
        final Long position1Id = creationResult1.getPositionId();
        assertEquals("ORDER00010", creationResult1.getOrderId());

        // The open trade arrives, change the status to OPENED and set the price.
        tradeFlux.emitValue(TradeDTO.builder().id("000002")
                .orderId("ORDER00010")
                .type(BID)
                .currencyPair(cp1)
                .originalAmount(new BigDecimal("0.0001"))
                .price(new BigDecimal("0.2"))
                .create());
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));

        // =============================================================================================================
        // We send tickers.

        // A first ticker arrives with a gain of 100% but for the wrong CP - so it must still be OPENED.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp2).last(new BigDecimal("0.5")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        PositionDTO p = getPositionDTO(position1Id);
        assertEquals(OPENED, p.getStatus());
        // We check the last calculated gain - should be none.
        Optional<GainDTO> gain = p.getLatestCalculatedGain();
        assertFalse(gain.isPresent());

        // A second ticker arrives with a gain of 50%.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.3")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        p = getPositionDTO(position1Id);
        // We check the last calculated gain - should be 50%.
        gain = p.getLatestCalculatedGain();
        assertTrue(gain.isPresent());
        assertEquals(50, gain.get().getPercentage());
        assertEquals(0, new BigDecimal("0.00001").compareTo(gain.get().getAmount().getValue()));
        assertEquals(BTC, gain.get().getAmount().getCurrency());
        assertEquals(BigDecimal.ZERO, gain.get().getFees().getValue());
        assertEquals(BTC, gain.get().getAmount().getCurrency());

        // A third ticker arrives with a gain of 100%- should close the order.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.5")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        assertEquals(CLOSING, getPositionDTO(position1Id).getStatus());

        // The close trade arrives, change the status and set the price.
        tradeFlux.emitValue(TradeDTO.builder().id("000002")
                .orderId("ORDER00011")
                .type(ASK)
                .currencyPair(cp1)
                .originalAmount(new BigDecimal("0.0001"))
                .create());
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(position1Id).getStatus()));
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check min and max gain")
    public void checkMinAndMaxGain() throws InterruptedException {
        // A position is opening on ETH/BTC.
        // We buy 10 ETH for 100 BTC.
        final PositionCreationResultDTO creationResult1 = positionService.createPosition(cp1,
                new BigDecimal("10"),
                PositionRulesDTO.builder()
                        .stopGainPercentage(1000f)   // 1 000% max gain.
                        .stopLossPercentage(100f)    // 100% max lost.
                        .create());
        final Long position1Id = creationResult1.getPositionId();

        // Two tickers arrived - min and max gain should not be set.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("100")).create());
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.000001")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);

        // Trade arrives, position is now opened.
        tradeFlux.emitValue(TradeDTO.builder().id("000001")
                .orderId("ORDER00010")
                .type(BID)
                .currencyPair(cp1)
                .originalAmount(new BigDecimal("10"))
                .price(new BigDecimal("0.03"))
                .create());

        // The two tickers arrived during the OPENING status should not have change highest lowest and latest gain.
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));
        PositionDTO position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isEmpty());
        assertTrue(position1.getHighestCalculatedGain().isEmpty());
        assertTrue(position1.getLatestCalculatedGain().isEmpty());

        // First ticker arrives (500% gain) - min and max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.18")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(500, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getLatestCalculatedGain().get().getPercentage());

        // Second ticker arrives (100% gain) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.06")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(100, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());

        // Third ticker arrives (200% gain) - nothing should change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.09")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(100, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());

        // Fourth ticker arrives (50% loss) - min gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.015")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(500, position1.getHighestCalculatedGain().get().getPercentage());

        // Firth ticker arrives (600% gain) - max gain should be set to that value.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.21")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, position1.getHighestCalculatedGain().get().getPercentage());

        // Closing the trade - min and max should not change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("100")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertEquals(CLOSING, position1.getStatus());

        // The close trade arrives, change the status and set the price.
        tradeFlux.emitValue(TradeDTO.builder().id("000002")
                .orderId("ORDER00011")
                .type(ASK)
                .currencyPair(cp1)
                .originalAmount(new BigDecimal("10"))
                .price(new BigDecimal("20"))
                .create());
        await().untilAsserted(() -> assertEquals(CLOSED, getPositionDTO(position1Id).getStatus()));

        // Sixth ticker arrives (800% gain) - min and max should not change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.27")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, position1.getHighestCalculatedGain().get().getPercentage());

        // Seventh ticker arrives (90% loss) - min and max should not change.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.003")).create());
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        position1 = getPositionDTO(position1Id);
        assertTrue(position1.getLowestCalculatedGain().isPresent());
        assertTrue(position1.getHighestCalculatedGain().isPresent());
        assertTrue(position1.getLatestCalculatedGain().isPresent());
        assertEquals(-50, position1.getLowestCalculatedGain().get().getPercentage());
        assertEquals(600, position1.getHighestCalculatedGain().get().getPercentage());
    }

    /**
     * Retrieve position from database.
     *
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
