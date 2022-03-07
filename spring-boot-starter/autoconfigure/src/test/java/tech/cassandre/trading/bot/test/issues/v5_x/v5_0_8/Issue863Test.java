package tech.cassandre.trading.bot.test.issues.v5_x.v5_0_8;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 863")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@Import(Issue863TestMock.class)
public class Issue863Test extends BaseTest {

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private OrderFlux orderFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Autowired
    private PositionService positionService;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check position auto close")
    public void checkAutoClose() {
        // =============================================================================================================
        // Creates position 1 (ETH/BTC, 0.0001, 100% stop gain).
        // Autoclose = true (by default).
        final PositionCreationResultDTO creationResult1 = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0001"),
                PositionRulesDTO.builder().stopGainPercentage(100f).build());
        final long position1Id = creationResult1.getPosition().getUid();
        assertEquals("ORDER00010", creationResult1.getPosition().getOpeningOrder().getOrderId());

        // =============================================================================================================
        // Creates position 2 (ETH/BTC, 0.0001, 100% stop gain).
        // Autoclose = false (set by user).
        final PositionCreationResultDTO creationResult2 = strategy.createLongPosition(ETH_BTC,
                new BigDecimal("0.0002"),
                PositionRulesDTO.builder().stopGainPercentage(100f).build());
        final long position2Id = creationResult2.getPosition().getUid();
        assertEquals("ORDER00020", creationResult2.getPosition().getOpeningOrder().getOrderId());
        strategy.setAutoClose(position2Id, false);

        // =============================================================================================================
        // We check that updates arrived, status should be opening and autoclose must be set to false on 2.
        await().untilAsserted(() -> {
            orderFlux.update();
            assertEquals(4, strategy.getPositionsUpdatesReceived().size());
        });
        // Testing position 1.
        PositionDTO p1 = getPositionDTO(position1Id);
        assertEquals(OPENING, p1.getStatus());
        assertTrue(p1.isAutoClose());
        // Testing position 2.
        PositionDTO p2 = getPositionDTO(position2Id);
        assertEquals(OPENING, p2.getStatus());
        assertFalse(p2.isAutoClose());

        // =============================================================================================================
        // We now send/receive trades and the two positions should be set to OPENED.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000001")
                .type(BID)
                .orderId("ORDER00010")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("0.0001", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.2", ETH_BTC.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position1Id).getStatus()));
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("000002")
                .type(BID)
                .orderId("ORDER00020")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("0.0002", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("0.2", ETH_BTC.getQuoteCurrency()))
                .build());
        await().untilAsserted(() -> assertEquals(OPENED, getPositionDTO(position2Id).getStatus()));

        // =============================================================================================================
        // We now receive a ticker that should close all positions but not the second one as auto close is set to false.
        tickerFlux.emitValue(TickerDTO.builder().currencyPair(ETH_BTC).last(new BigDecimal("1")).build());

        // Position 1 should be closing (autoclose to true).
        await().untilAsserted(() -> assertEquals(CLOSING, getPositionDTO(position1Id).getStatus()));
        // Position 2 should be opened (autoclose to false).
        assertEquals(OPENED, getPositionDTO(position2Id).getStatus());
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
            throw new NoSuchElementException("Position not found : " + uid);
        }
    }

}
