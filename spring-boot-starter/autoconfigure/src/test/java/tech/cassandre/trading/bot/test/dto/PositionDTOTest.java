package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

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
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

@DisplayName("DTO - PositionDTO")
public class PositionDTOTest {

    private final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

    private final BigDecimal amount = new BigDecimal("0.0001");

    private final PositionRulesDTO noRules = PositionRulesDTO.builder().create();

    @Test
    @DisplayName("Check testing status change")
    public void checkStatusChange() {
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
    @DisplayName("Check that close order update limited to OPENED position")
    public void checkCloseOrderIdUpdate() {
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
    @DisplayName("Check that position should be closed (max gain rules)")
    public void checkShouldBeClosedWithGainRules() {
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
        TickerDTO t02 = TickerDTO.builder().currencyPair(cp).last(new BigDecimal("0.036")).create();
        assertFalse(p.shouldBeClosed(t02));

        // New ticker for the right currency pair with a profit of 100% - should be closed.
        TickerDTO t03 = TickerDTO.builder().currencyPair(cp).last(new BigDecimal("0.05")).create();
        assertTrue(p.shouldBeClosed(t03));
    }

    @Test
    @DisplayName("Check that position should be closed (max lost rules)")
    public void checkShouldBeClosedWithLostRules() {
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
        TickerDTO t01 = TickerDTO.builder().currencyPair(new CurrencyPairDTO(BTC, ETH)).last(new BigDecimal("0.001")).create();
        assertFalse(p.shouldBeClosed(t01));

        // New ticker for the right currency pair but with a loss of 50%.
        TickerDTO t02 = TickerDTO.builder().currencyPair(cp).last(new BigDecimal("0.012")).create();
        assertFalse(p.shouldBeClosed(t02));

        // New ticker for the right currency pair with a profit of 50% - should be closed.
        TickerDTO t03 = TickerDTO.builder().currencyPair(cp).last(new BigDecimal("0.001")).create();
        assertTrue(p.shouldBeClosed(t03));
    }

    @Test
    @DisplayName("Check equalTo")
    public void checkEqualTo() {
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

    @Test
    @DisplayName("Check get positive position gain")
    public void checkGetPositivePositionGain() {
        // Position 1.
        // Rule : 10% gain.
        PositionDTO p = new PositionDTO(1, "O000011", PositionRulesDTO.builder().stopGainPercentage(10).create());

        // Position opened with this trade.
        // BID ETH / BTC means I'm buying ETH by giving Bitcoins.
        // We bought 10 Ether with the price : 1 Ether = 5 Bitcoin.
        final TradeDTO trade01 = TradeDTO.builder().id("T000001")
                .orderId("O000011")                         // Closing opening order O000011
                .type(OrderTypeDTO.BID)                     // Buying.
                .currencyPair(cp)                           // ETH / BTC.
                .originalAmount(new BigDecimal(10))     // 10.
                .price(new BigDecimal("5"))             // Price 5.
                .feeAmount(new BigDecimal(1))           // Fee 1.
                .create();
        p.tradeUpdate(trade01);
        assertEquals(OPENED, p.getStatus());
        assertEquals("T000001", p.getOpenTrade().getId());
        assertEquals(0, p.getGain().getPercentage());
        assertEquals(BigDecimal.ZERO, p.getGain().getAmount().getValue());
        assertEquals(BigDecimal.ZERO, p.getGain().getFees().getValue());

        // We tell the position that it will be closed with order O000012.
        p.setCloseOrderId("O000012");

        // Position closed with this trade.
        final TradeDTO trade02 = TradeDTO.builder().id("T000002")
                .orderId("O000012")                         // Closing opening order O000011
                .type(OrderTypeDTO.BID)                     // Buying.
                .currencyPair(cp)                           // ETH / BTC.
                .originalAmount(new BigDecimal(10))     // 10.
                .price(new BigDecimal("6"))             // Price 6.
                .feeAmount(new BigDecimal(2))           // Fee 2.
                .create();
        p.tradeUpdate(trade02);
        assertEquals(CLOSED, p.getStatus());
        assertEquals("T000002", p.getCloseTrade().getId());
        GainDTO gainDTO = p.getGain();
        assertEquals(20, gainDTO.getPercentage());
        assertEquals(new BigDecimal("10"), gainDTO.getAmount().getValue());
        assertEquals(BTC, p.getGain().getAmount().getCurrency());
        assertEquals(new BigDecimal("3"), gainDTO.getFees().getValue());
        assertEquals(BTC, p.getGain().getFees().getCurrency());
    }

    @Test
    @DisplayName("Check get negative position gain")
    public void checkGetNegativePositionGain() {
        // Position 1.
        // Rule : 10% lost.
        PositionDTO p = new PositionDTO(1, "O000011", PositionRulesDTO.builder().stopLossPercentage(10).create());

        // Position opened with this trade.
        // BID ETH / BTC means I'm buying ETH by giving Bitcoins.
        // We bought 10 Ether with the price : 1 Ether = 5 Bitcoin.
        final TradeDTO trade01 = TradeDTO.builder().id("T000001")
                .orderId("O000011")                         // Closing opening order O000011
                .type(OrderTypeDTO.BID)                     // Buying.
                .currencyPair(cp)                           // ETH / BTC.
                .originalAmount(new BigDecimal(10))     // 10.
                .price(new BigDecimal("5"))             // Price 5.
                .feeAmount(new BigDecimal(1))           // Fee 1.
                .create();
        p.tradeUpdate(trade01);
        assertEquals(OPENED, p.getStatus());
        assertEquals("T000001", p.getOpenTrade().getId());
        assertEquals(0, p.getGain().getPercentage());
        assertEquals(BigDecimal.ZERO, p.getGain().getAmount().getValue());
        assertEquals(BigDecimal.ZERO, p.getGain().getFees().getValue());

        // We tell the position that it will be closed with order O000012.
        p.setCloseOrderId("O000012");

        // Position closed with this trade.
        final TradeDTO trade02 = TradeDTO.builder().id("T000002")
                .orderId("O000012")                         // Closing opening order O000011
                .type(OrderTypeDTO.BID)                     // Buying.
                .currencyPair(cp)                           // ETH / BTC.
                .originalAmount(new BigDecimal(10))     // 10.
                .price(new BigDecimal("4"))             // Price 6.
                .feeAmount(new BigDecimal(2))           // Fee 2.
                .create();
        p.tradeUpdate(trade02);
        assertEquals(CLOSED, p.getStatus());
        assertEquals("T000002", p.getCloseTrade().getId());
        GainDTO gainDTO = p.getGain();
        assertEquals(-20, gainDTO.getPercentage());
        assertEquals(new BigDecimal("-10"), gainDTO.getAmount().getValue());
        assertEquals(BTC, p.getGain().getAmount().getCurrency());
        assertEquals(new BigDecimal("3"), gainDTO.getFees().getValue());
        assertEquals(BTC, p.getGain().getFees().getCurrency());
    }

    @Test
    @DisplayName("Check toString method")
    public void checkToString() {
        // Position opening.
        PositionDTO p1 = new PositionDTO(1L,
                OPENING,
                PositionRulesDTO.builder().create(),
                "OPEN_ORDER_01",
                null,
                null,
                null,
                null,
                null);
        assertEquals(p1.toString(), "Position n°1 (no rules) - Opening - Waiting for the trade of order OPEN_ORDER_01");

        // Position opened - No ticker received.
        TradeDTO openTrade1 = TradeDTO.builder().id("0000001")
                .currencyPair(new CurrencyPairDTO(ETH, BTC))
                .feeAmount(new BigDecimal(1))
                .feeCurrency(BTC)
                .orderId("OPEN_ORDER_02")
                .originalAmount(new BigDecimal(1))
                .price(new BigDecimal(1))
                .timestamp(ZonedDateTime.now())
                .type(OrderTypeDTO.BID)
                .create();
        PositionDTO p2 = new PositionDTO(2L,
                OPENED,
                PositionRulesDTO.builder().stopLossPercentage(11).create(),
                "OPEN_ORDER_02",
                openTrade1,
                null,
                null,
                null,
                null);
        assertEquals(p2.toString(), "Position n°2 (11.0 % loss rule) on ETH/BTC - Opened");
        // A new ticker arrived.
        TickerDTO t1 = TickerDTO.builder().currencyPair(new CurrencyPairDTO(ETH, BTC)).last(new BigDecimal("2")).create();
        p2.shouldBeClosed(t1);
        assertEquals(p2.toString(), "Position n°2 (11.0 % loss rule) on ETH/BTC - Opened - Last gain calculated 100.0 %");

        // Position closing.
        PositionDTO p3 = new PositionDTO(3L,
                CLOSING,
                PositionRulesDTO.builder().stopGainPercentage(12).create(),
                "OPEN_ORDER_03",
                openTrade1,
                "OPEN_ORDER_04",
                null,
                null,
                null);
        assertEquals(p3.toString(), "Position n°3 (12.0 % gain rule) on ETH/BTC - Closing - Waiting for the trade of order OPEN_ORDER_04");

        // Position closed.
        TradeDTO openTrade4 = TradeDTO.builder().id("0000003")
                .currencyPair(new CurrencyPairDTO(ETH, BTC))
                .feeAmount(new BigDecimal(1))
                .feeCurrency(BTC)
                .orderId("OPEN_ORDER_04")
                .originalAmount(new BigDecimal(1))
                .price(new BigDecimal(1))
                .timestamp(ZonedDateTime.now())
                .type(OrderTypeDTO.BID)
                .create();
        TradeDTO closeTrade4 = TradeDTO.builder().id("0000004")
                .currencyPair(new CurrencyPairDTO(ETH, BTC))
                .feeAmount(new BigDecimal(1))
                .feeCurrency(BTC)
                .orderId("OPEN_ORDER_05")
                .originalAmount(new BigDecimal(1))
                .price(new BigDecimal(2))
                .timestamp(ZonedDateTime.now())
                .type(OrderTypeDTO.BID)
                .create();
        PositionDTO p4 = new PositionDTO(4L,
                CLOSED,
                PositionRulesDTO.builder().stopGainPercentage(12).stopLossPercentage(9L).create(),
                "OPEN_ORDER_04",
                openTrade4,
                "OPEN_ORDER_04",
                closeTrade4,
                null,
                null);
        p4.tradeUpdate(closeTrade4);
        assertEquals(p4.toString(), "Position n°4 (12.0 % gain rule / 9.0 % loss rule) on ETH/BTC - Closed - Gain : 100.0 %");
    }

}
