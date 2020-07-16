package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.ETH;

/**
 * Position DTO test.
 */
@DisplayName("Position DTO tests")
public class PositionDTOTest {

    /** Currency pair used for test. */
    private final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

    /** Amount used for test. */
    private final BigDecimal amount = new BigDecimal("0.0001");

    /** Empty rules. */
    final PositionRulesDTO noRules = PositionRulesDTO.builder().create();

    @Test
    @DisplayName("Testing the status change")
    public void statusChange() {
        // We create a position that was opened with the order O000001.
        PositionDTO p = new PositionDTO(1, "O000001", noRules);

        // After creation, the position state should be OPENING
        assertEquals(OPENING, p.getStatus());
        assertNull(p.getOpenTrade());
        assertNull(p.getCloseTrade());

        // A first trade arrives for another order, nothing should change.
        p.tradeUpdate(TradeDTO.builder().id("T000001").orderId("O000000").create());
        assertEquals(OPENING, p.getStatus());
        assertNull(p.getOpenTrade());
        assertNull(p.getCloseTrade());

        // A second trade arrives for the order O000001 and should change the state to OPENED.
        p.tradeUpdate(TradeDTO.builder().id("T000002").orderId("O000001").create());
        assertEquals(OPENED, p.getStatus());
        assertEquals("T000002", p.getOpenTrade().getId());
        assertNull(p.getCloseTrade());

        // A close order is set, the status should now be closing.
        p.setCloseOrderId("O000002");
        assertEquals(CLOSING, p.getStatus());
        assertEquals("T000002", p.getOpenTrade().getId());
        assertNull(p.getCloseTrade());

        // Previous trades arrives, nothing should change.
        p.tradeUpdate(TradeDTO.builder().id("T000003").orderId("O000000").create());
        p.tradeUpdate(TradeDTO.builder().id("T000004").orderId("O000001").create());
        assertEquals(CLOSING, p.getStatus());
        assertEquals("T000002", p.getOpenTrade().getId());
        assertNull(p.getCloseTrade());

        // A trade arrives for another order, nothing should change.
        p.tradeUpdate(TradeDTO.builder().id("T000005").orderId("O000003").create());
        assertEquals(CLOSING, p.getStatus());
        assertEquals("T000002", p.getOpenTrade().getId());
        assertNull(p.getCloseTrade());

        // A trade arrives for the closing order
        p.tradeUpdate(TradeDTO.builder().id("T000006").orderId("O000002").create());
        assertEquals(CLOSED, p.getStatus());
        assertEquals("T000002", p.getOpenTrade().getId());
        assertEquals("T000006", p.getCloseTrade().getId());
    }

    @Test
    @DisplayName("Close order update limited to OPENED position")
    public void closeOrderIdUpdate() {
        // We create a position that was opened with the order O000001.
        PositionDTO p = new PositionDTO(1, "O000001", noRules);

        // We are in OPENING status and we try to call setCloseOrderId.
        assertEquals(OPENING, p.getStatus());
        assertThrows(RuntimeException.class, () -> p.setCloseOrderId("O000002"));

        // We move to OPENED status and we try to call setCloseOrderId.
        p.tradeUpdate(TradeDTO.builder().id("T000001").orderId("O000001").create());
        assertEquals(OPENED, p.getStatus());

        // We are in OPENED, we should now be able to setCloseOrderId.
        p.setCloseOrderId("O000002");
        assertEquals(CLOSING, p.getStatus());

        // We are in CLOSING, we should not be able to setCloseOrderId.
        assertThrows(RuntimeException.class, () -> p.setCloseOrderId("O000002"));

        // We move to CLOSED.
        p.tradeUpdate(TradeDTO.builder().id("T000001").orderId("O000002").create());
        assertEquals(CLOSED, p.getStatus());
        assertThrows(RuntimeException.class, () -> p.setCloseOrderId("O000002"));
    }

    @Test
    @DisplayName("Position should be closed (max gain rules)")
    public void shouldBeClosedWithGainRules() {
        // Position 1.
        // Rule : 70% gain.
        PositionDTO p = new PositionDTO(1, "O000011", PositionRulesDTO.builder().stopGainPercentage(70).create());

        // Position opened with this trade.
        // BID ETH / BTC means I'm buying ETH by giving Bitcoins.
        // We bought 0.0001 Ether with the price : 1 Ether = 0,024972 Bitcoin.
        final TradeDTO trade01 = TradeDTO.builder().id("T000001")
                .orderId("O000011")                         // Closing opening order O000011
                .type(OrderTypeDTO.BID)                     // Buying.
                .currencyPair(cp)                           // ETH / BTC.
                .originalAmount(amount)                     // 0.0001.
                .price(new BigDecimal("0.024972"))      // Price 0.025972.
                .create();
        p.tradeUpdate(trade01);
        assertEquals(OPENED, p.getStatus());
        assertEquals("T000001", p.getOpenTrade().getId());

        // New ticker for a currency pair that is not the one of T000001.
        TickerDTO t01 = TickerDTO.builder().currencyPair(new CurrencyPairDTO(BTC, ETH)).bid(new BigDecimal("0.05")).create();
        assertFalse(p.shouldBeClosed(t01));

        // New ticker for the right currency pair but with a profit of 50%.
        TickerDTO t02 = TickerDTO.builder().currencyPair(cp).ask(new BigDecimal("0.036")).create();
        assertFalse(p.shouldBeClosed(t02));

        // New ticker for the right currency pair with a profit of 100% - should be closed.
        TickerDTO t03 = TickerDTO.builder().currencyPair(cp).ask(new BigDecimal("0.05")).create();
        assertTrue(p.shouldBeClosed(t03));
    }

    @Test
    @DisplayName("Position should be closed (max lost rules)")
    public void shouldBeClosedWithLostRules() {
        // Position 1.
        // Rule : 70% loss.
        PositionDTO p = new PositionDTO(1, "O000011", PositionRulesDTO.builder().stopLossPercentage(70).create());

        // Position opened with this trade.
        // BID ETH / BTC means I'm buying ETH by giving Bitcoins.
        // We bought 0.0001 Ether with the price : 1 Ether = 0,024972 Bitcoin.
        final TradeDTO trade01 = TradeDTO.builder().id("T000001")
                .orderId("O000011")                         // Closing opening order O000011
                .type(OrderTypeDTO.BID)                     // Buying.
                .currencyPair(cp)                           // ETH / BTC.
                .originalAmount(amount)                     // 0.0001.
                .price(new BigDecimal("0.024972"))      // Price 0.025972.
                .create();
        p.tradeUpdate(trade01);
        assertEquals(OPENED, p.getStatus());
        assertEquals("T000001", p.getOpenTrade().getId());

        // New ticker for a currency pair that is not the one of T000001.
        TickerDTO t01 = TickerDTO.builder().currencyPair(new CurrencyPairDTO(BTC, ETH)).bid(new BigDecimal("0.001")).create();
        assertFalse(p.shouldBeClosed(t01));

        // New ticker for the right currency pair but with a loss of 50%.
        TickerDTO t02 = TickerDTO.builder().currencyPair(cp).ask(new BigDecimal("0.012")).create();
        assertFalse(p.shouldBeClosed(t02));

        // New ticker for the right currency pair with a profit of 50% - should be closed.
        TickerDTO t03 = TickerDTO.builder().currencyPair(cp).ask(new BigDecimal("0.001")).create();
        assertTrue(p.shouldBeClosed(t03));
    }

    @Test
    @DisplayName("PositionDTO equalTo")
    public void equalTo() {
        PositionDTO p1 = new PositionDTO(1, "O000001", noRules);
        PositionDTO p1Bis = new PositionDTO(1, "O000001", noRules);
        PositionDTO p2 = new PositionDTO(2, "O000002", noRules);

        // Same position.
        assertEquals(p1, p1);
        assertEquals(p1, p1Bis);

        // Two different positions.
        assertNotEquals(p1, p2);

        // Status changed - for P1.
        p1.tradeUpdate(TradeDTO.builder().id("T000001").orderId("O000001").create());
        assertNotEquals(p1, p1Bis);
    }
}
