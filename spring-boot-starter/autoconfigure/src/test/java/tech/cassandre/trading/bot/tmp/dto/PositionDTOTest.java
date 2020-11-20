package tech.cassandre.trading.bot.tmp.dto;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

@DisplayName("DTO - PositionDTO")
@Disabled
public class PositionDTOTest {

    private final CurrencyPairDTO cp1 = new CurrencyPairDTO(ETH, BTC);

    private final BigDecimal amount = new BigDecimal("0.0001");

    private final PositionRulesDTO noRules = PositionRulesDTO.builder().create();

    @Test
    @Tag("notReviewed")
    @DisplayName("Check status change")
    public void checkStatusChange() {
        // We create a position that was opened with the order O000001.
        PositionDTO p = new PositionDTO(1, cp1, amount, "O000001", noRules);

        // After creation, the position state should be OPENING
        assertEquals(OPENING, p.getStatus());
        assertTrue(p.getOpenTrades().isEmpty());
        assertTrue(p.getCloseTrades().isEmpty());

        // A first trade arrives for another order, nothing should change.
        p.tradeUpdate(TradeDTO.builder().id("T000001").orderId("O000000").create());
        assertEquals(OPENING, p.getStatus());
        assertTrue(p.getOpenTrades().isEmpty());
        assertTrue(p.getCloseTrades().isEmpty());

        // A second trade arrives for the order O000001 and should change the state to OPENED.
        p.tradeUpdate(TradeDTO.builder().id("T000002").type(BID).orderId("O000001").originalAmount(amount).create());
        assertEquals(OPENED, p.getStatus());
        assertEquals(1, p.getOpenTrades().size());
        assertTrue(p.getTrade("T000002").isPresent());
        assertTrue(p.getCloseTrades().isEmpty());

        // A close order is set, the status should now be closing.
        p.setCloseOrderId("O000002");
        assertEquals(CLOSING, p.getStatus());
        assertEquals(1, p.getOpenTrades().size());
        assertTrue(p.getTrade("T000002").isPresent());
        assertTrue(p.getCloseTrades().isEmpty());

        // Previous trades arrives, nothing should change.
        p.tradeUpdate(TradeDTO.builder().id("T000003").orderId("O000000").create());
        p.tradeUpdate(TradeDTO.builder().id("T000004").orderId("O000001").create());
        assertEquals(CLOSING, p.getStatus());
        assertEquals(1, p.getOpenTrades().size());
        assertTrue(p.getTrade("T000002").isPresent());
        assertTrue(p.getCloseTrades().isEmpty());

        // A trade arrives for another order, nothing should change.
        p.tradeUpdate(TradeDTO.builder().id("T000005").orderId("O000003").currencyPair(cp1).originalAmount(amount).create());
        assertEquals(CLOSING, p.getStatus());
        assertEquals(1, p.getOpenTrades().size());
        assertTrue(p.getTrade("T000002").isPresent());
        assertTrue(p.getCloseTrades().isEmpty());

        // A trade arrives for the closing order
        p.tradeUpdate(TradeDTO.builder().id("T000006").type(ASK).orderId("O000002").currencyPair(cp1).originalAmount(amount).create());
        assertEquals(CLOSED, p.getStatus());
        assertEquals(1, p.getOpenTrades().size());
        assertTrue(p.getTrade("T000002").isPresent());
        assertEquals(1, p.getCloseTrades().size());
        assertTrue(p.getTrade("T000006").isPresent());
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check status change with multiple trades")
    public void checkStatusWithMultipleTrade() {
        // We create a position that was opened with the order O000001.
        PositionDTO p = new PositionDTO(1, cp1, new BigDecimal("10"), "O000001", noRules);

        // After creation, the position state should be OPENING.
        assertEquals(OPENING, p.getStatus());
        assertTrue(p.getOpenTrades().isEmpty());
        assertTrue(p.getCloseTrades().isEmpty());

        // A first trade arrives for the order O000001 but only half of the amount.
        p.tradeUpdate(TradeDTO.builder().id("T000001").type(BID).orderId("O000001").originalAmount(new BigDecimal("5")).create());
        assertEquals(OPENING, p.getStatus());
        assertEquals(1, p.getOpenTrades().size());
        assertTrue(p.getTrade("T000001").isPresent());
        assertTrue(p.getCloseTrades().isEmpty());

        // A second trade arrives for the order O000001 - amount is complete. Should now be opened.
        p.tradeUpdate(TradeDTO.builder().id("T000002").type(BID).orderId("O000001").originalAmount(new BigDecimal("5")).create());
        assertEquals(OPENED, p.getStatus());
        assertEquals(2, p.getOpenTrades().size());
        assertTrue(p.getTrade("T000001").isPresent());
        assertTrue(p.getTrade("T000002").isPresent());
        assertTrue(p.getCloseTrades().isEmpty());

        // A close order is set, the status should now be closing.
        p.setCloseOrderId("O000002");
        assertEquals(CLOSING, p.getStatus());
        assertEquals(2, p.getOpenTrades().size());
        assertTrue(p.getCloseTrades().isEmpty());

        // A trade arrives for another order, nothing should change.
        p.tradeUpdate(TradeDTO.builder().id("T000005").orderId("O000003").type(ASK).currencyPair(cp1).originalAmount(amount).create());
        assertEquals(CLOSING, p.getStatus());
        assertEquals(2, p.getOpenTrades().size());
        assertTrue(p.getCloseTrades().isEmpty());

        // A first trade arrives for the closing order.
        p.tradeUpdate(TradeDTO.builder().id("T000006").orderId("O000002").type(ASK).currencyPair(cp1).originalAmount(new BigDecimal(2)).create());
        assertEquals(CLOSING, p.getStatus());
        assertEquals(2, p.getOpenTrades().size());
        assertTrue(p.getTrade("T000001").isPresent());
        assertTrue(p.getTrade("T000002").isPresent());
        assertEquals(1, p.getCloseTrades().size());
        assertTrue(p.getTrade("T000006").isPresent());

        // A second trade arrives for the closing order.
        p.tradeUpdate(TradeDTO.builder().id("T000007").orderId("O000002").type(ASK).currencyPair(cp1).originalAmount(new BigDecimal(3)).create());
        assertEquals(CLOSING, p.getStatus());
        assertEquals(2, p.getOpenTrades().size());
        assertTrue(p.getTrade("T000001").isPresent());
        assertTrue(p.getTrade("T000002").isPresent());
        assertEquals(2, p.getCloseTrades().size());
        assertTrue(p.getTrade("T000006").isPresent());
        assertTrue(p.getTrade("T000007").isPresent());

        // A third trade arrives for the closing order.
        p.tradeUpdate(TradeDTO.builder().id("T000008").orderId("O000002").type(ASK).currencyPair(cp1).originalAmount(new BigDecimal(5)).create());
        assertEquals(CLOSED, p.getStatus());
        // Open trades.
        assertEquals(2, p.getOpenTrades().size());
        assertTrue(p.getTrade("T000001").isPresent());
        assertEquals("T000001", p.getTrade("T000001").get().getId());
        assertTrue(p.getTrade("T000002").isPresent());
        assertEquals("T000002", p.getTrade("T000002").get().getId());
        // Close trades.
        assertEquals(3, p.getCloseTrades().size());
        assertTrue(p.getTrade("T000006").isPresent());
        assertEquals("T000006", p.getTrade("T000006").get().getId());
        assertTrue(p.getTrade("T000007").isPresent());
        assertEquals("T000007", p.getTrade("T000007").get().getId());
        assertTrue(p.getTrade("T000008").isPresent());
        assertEquals("T000008", p.getTrade("T000008").get().getId());
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check that close order update limited to OPENED position")
    public void checkCloseOrderIdUpdate() {
        // We create a position that was opened with the order O000001.
        PositionDTO p = new PositionDTO(1, cp1, amount, "O000001", noRules);

        // We are in OPENING status and we try to call setCloseOrderId.
        assertEquals(OPENING, p.getStatus());
        assertThrows(RuntimeException.class, () -> p.setCloseOrderId("O000002"));

        // We move to OPENED status and we try to call setCloseOrderId.
        p.tradeUpdate(TradeDTO.builder().id("T000001").type(BID).orderId("O000001").originalAmount(amount).create());
        assertEquals(OPENED, p.getStatus());

        // We are in OPENED, we should now be able to setCloseOrderId.
        p.setCloseOrderId("O000002");
        assertEquals(CLOSING, p.getStatus());

        // We are in CLOSING, we should not be able to setCloseOrderId.
        assertThrows(RuntimeException.class, () -> p.setCloseOrderId("O000002"));

        // We move to CLOSED.
        p.tradeUpdate(TradeDTO.builder().id("T000001").type(ASK).orderId("O000002").originalAmount(amount).create());
        assertEquals(CLOSED, p.getStatus());
        assertThrows(RuntimeException.class, () -> p.setCloseOrderId("O000002"));
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check that position should be closed (max gain rules)")
    public void checkShouldBeClosedWithGainRules() {
        // Position 1.
        // Rule : 70% gain.
        PositionDTO p = new PositionDTO(1, cp1, amount, "O000011", PositionRulesDTO.builder().stopGainPercentage(70f).create());

        // Position opened with this trade.
        // BID ETH / BTC means I'm buying ETH by giving Bitcoins.
        // We bought 0.0001 Ether with the price : 1 Ether = 0,024972 Bitcoin.
        final TradeDTO trade01 = TradeDTO.builder().id("T000001")
                .orderId("O000011")                         // Closing opening order O000011
                .type(BID)                                  // Buying.
                .currencyPair(cp1)                           // ETH / BTC.
                .originalAmount(amount)                     // 0.0001.
                .price(new BigDecimal("0.024972"))      // Price 0.025972.
                .create();
        p.tradeUpdate(trade01);
        assertEquals(OPENED, p.getStatus());
        assertTrue(p.getTrade("T000001").isPresent());

        // New ticker for a currency pair that is not the one of T000001.
        TickerDTO t01 = TickerDTO.builder().currencyPair(new CurrencyPairDTO(BTC, ETH)).bid(new BigDecimal("0.05")).create();
        assertFalse(p.shouldBeClosed(t01));

        // New ticker for the right currency pair but with a profit of 50%.
        TickerDTO t02 = TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.036")).create();
        assertFalse(p.shouldBeClosed(t02));

        // New ticker for the right currency pair with a profit of 100% - should be closed.
        TickerDTO t03 = TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.05")).create();
        assertTrue(p.shouldBeClosed(t03));
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check that position should be closed (max lost rules)")
    public void checkShouldBeClosedWithLostRules() {
        // Position 1.
        // Rule : 70% loss.
        PositionDTO p = new PositionDTO(1, cp1, amount, "O000011", PositionRulesDTO.builder().stopLossPercentage(70f).create());

        // Position opened with this trade.
        // BID ETH / BTC means I'm buying ETH by giving Bitcoins.
        // We bought 0.0001 Ether with the price : 1 Ether = 0,024972 Bitcoin.
        final TradeDTO trade01 = TradeDTO.builder().id("T000001")
                .orderId("O000011")                         // Closing opening order O000011
                .type(BID)                     // Buying.
                .currencyPair(cp1)                           // ETH / BTC.
                .originalAmount(amount)                     // 0.0001.
                .price(new BigDecimal("0.024972"))      // Price 0.025972.
                .create();
        p.tradeUpdate(trade01);
        assertEquals(OPENED, p.getStatus());
        assertTrue(p.getTrade("T000001").isPresent());

        // New ticker for a currency pair that is not the one of T000001.
        TickerDTO t01 = TickerDTO.builder().currencyPair(new CurrencyPairDTO(BTC, ETH)).last(new BigDecimal("0.001")).create();
        assertFalse(p.shouldBeClosed(t01));

        // New ticker for the right currency pair but with a loss of 50%.
        TickerDTO t02 = TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.012")).create();
        assertFalse(p.shouldBeClosed(t02));

        // New ticker for the right currency pair with a profit of 50% - should be closed.
        TickerDTO t03 = TickerDTO.builder().currencyPair(cp1).last(new BigDecimal("0.001")).create();
        assertTrue(p.shouldBeClosed(t03));
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check equalTo")
    public void checkEqualTo() {
        PositionDTO p1 = new PositionDTO(1, cp1, amount, "O000001", noRules);
        PositionDTO p1Bis = new PositionDTO(1, cp1, amount, "O000001", noRules);
        PositionDTO p2 = new PositionDTO(2, cp1, amount, "O000002", noRules);

        // Same position.
        assertEquals(p1, p1);
        assertEquals(p1, p1Bis);

        // Two different positions.
        assertNotEquals(p1, p2);

        // Status changed - for P1.
        p1.tradeUpdate(TradeDTO.builder().id("T000001").type(BID).currencyPair(cp1).originalAmount(amount).orderId("O000001").create());
        assertNotEquals(p1, p1Bis);
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check get positive position gain")
    public void checkGetPositivePositionGain() {
        // Position 1.
        // Rule : 10% gain.
        PositionDTO p = new PositionDTO(1, cp1, amount, "O000011", PositionRulesDTO.builder().stopGainPercentage(10f).create());

        // Position opened with this trade.
        // BID ETH / BTC means I'm buying ETH by giving Bitcoins.
        // We bought 10 Ether with the price : 1 Ether = 5 Bitcoin.
        final TradeDTO trade01 = TradeDTO.builder().id("T000001")
                .orderId("O000011")                         // Closing opening order O000011
                .type(BID)                                  // Buying.
                .currencyPair(cp1)                          // ETH / BTC.
                .originalAmount(amount)                     // 0.0001.
                .price(new BigDecimal("5"))             // Price 5.
                .feeAmount(new BigDecimal(1))           // Fee 1.
                .create();
        p.tradeUpdate(trade01);
        assertEquals(OPENED, p.getStatus());
        assertTrue(p.getTrade("T000001").isPresent());
        assertEquals(0, p.getGain().getPercentage());
        assertEquals(BigDecimal.ZERO, p.getGain().getAmount().getValue());
        assertEquals(BigDecimal.ZERO, p.getGain().getFees().getValue());

        // We tell the position that it will be closed with order O000012.
        p.setCloseOrderId("O000012");

        // Position closed with this trade.
        final TradeDTO trade02 = TradeDTO.builder().id("T000002")
                .orderId("O000012")                         // Closing opening order O000011
                .type(ASK)                                  // Buying.
                .currencyPair(cp1)                          // ETH / BTC.
                .originalAmount(amount)                     // 0.0001.
                .price(new BigDecimal("6"))             // Price 6.
                .feeAmount(new BigDecimal(2))           // Fee 2.
                .create();
        p.tradeUpdate(trade02);
        assertEquals(CLOSED, p.getStatus());
        assertTrue(p.getTrade("T000002").isPresent());
        GainDTO gainDTO = p.getGain();
        assertEquals(20, gainDTO.getPercentage());
        assertEquals(new BigDecimal("0.0001"), gainDTO.getAmount().getValue());
        assertEquals(BTC, p.getGain().getAmount().getCurrency());
        assertEquals(new BigDecimal("3"), gainDTO.getFees().getValue());
        assertEquals(BTC, p.getGain().getFees().getCurrency());
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check get negative position gain")
    public void checkGetNegativePositionGain() {
        // Position 1.
        // Rule : 10% lost.
        PositionDTO p = new PositionDTO(1, cp1, amount, "O000011", PositionRulesDTO.builder().stopLossPercentage(10f).create());

        // Position opened with this trade.
        // BID ETH / BTC means I'm buying ETH by giving Bitcoins.
        // We bought 10 Ether with the price : 1 Ether = 5 Bitcoin.
        final TradeDTO trade01 = TradeDTO.builder().id("T000001")
                .orderId("O000011")                         // Closing opening order O000011
                .type(BID)                                  // Buying.
                .currencyPair(cp1)                          // ETH / BTC.
                .originalAmount(amount)                     // 10.
                .price(new BigDecimal("5"))             // Price 5.
                .feeAmount(new BigDecimal(1))           // Fee 1.
                .create();
        p.tradeUpdate(trade01);
        assertEquals(OPENED, p.getStatus());
        assertTrue(p.getTrade("T000001").isPresent());
        assertEquals(0, p.getGain().getPercentage());
        assertEquals(BigDecimal.ZERO, p.getGain().getAmount().getValue());
        assertEquals(BigDecimal.ZERO, p.getGain().getFees().getValue());

        // We tell the position that it will be closed with order O000012.
        p.setCloseOrderId("O000012");

        // Position closed with this trade.
        final TradeDTO trade02 = TradeDTO.builder().id("T000002")
                .orderId("O000012")                         // Closing opening order O000011
                .type(ASK)                                  // Buying.
                .currencyPair(cp1)                          // ETH / BTC.
                .originalAmount(amount)                     // 10.
                .price(new BigDecimal("4"))             // Price 4.
                .feeAmount(new BigDecimal(2))           // Fee 2.
                .create();
        p.tradeUpdate(trade02);
        assertEquals(CLOSED, p.getStatus());
        assertTrue(p.getTrade("T000002").isPresent());
        GainDTO gainDTO = p.getGain();
        assertEquals(-20, gainDTO.getPercentage());
        assertEquals(new BigDecimal("-0.0001"), gainDTO.getAmount().getValue());
        assertEquals(BTC, p.getGain().getAmount().getCurrency());
        assertEquals(new BigDecimal("3"), gainDTO.getFees().getValue());
        assertEquals(BTC, p.getGain().getFees().getCurrency());
    }

    @Test
    @Tag("notReviewed")
    @DisplayName("Check toString method")
    public void checkToString() {
        // Position opening.
        PositionDTO p1 = PositionDTO.builder()
                .id(1L)
                .status(OPENING)
                .currencyPair(new CurrencyPairDTO(ETH, BTC))
                .amount(new BigDecimal("1"))
                .openOrderId("OPEN_ORDER_01")
                .create();
        assertEquals(p1.toString(), "Position n°1 (no rules) - Opening - Waiting for the trade of order OPEN_ORDER_01");

        // Position opened - No ticker received.
        TradeDTO openTrade1 = TradeDTO.builder().id("0000001")
                .currencyPair(new CurrencyPairDTO(ETH, BTC))
                .feeAmount(new BigDecimal("1"))
                .feeCurrency(BTC)
                .orderId("OPEN_ORDER_02")
                .originalAmount(new BigDecimal(1))
                .price(new BigDecimal(1))
                .timestamp(ZonedDateTime.now())
                .type(BID)
                .create();
        PositionDTO p2 = PositionDTO.builder()
                .id(2L)
                .status(OPENED)
                .currencyPair(new CurrencyPairDTO(ETH, BTC))
                .amount(new BigDecimal("1"))
                .stopLossPercentageRule(11F)
                .openOrderId("OPEN_ORDER_02")
                .trades(Collections.singleton(openTrade1))
                .create();
        assertEquals(p2.toString(), "Position n°2 (11.0 % loss rule) on ETH/BTC - Opened");
        // A new ticker arrived.
        TickerDTO t1 = TickerDTO.builder().currencyPair(new CurrencyPairDTO(ETH, BTC)).last(new BigDecimal("2")).create();
        p2.shouldBeClosed(t1);
        assertEquals(p2.toString(), "Position n°2 (11.0 % loss rule) on ETH/BTC - Opened - Last gain calculated 100 %");

        // Position closing.
        PositionDTO p3 = PositionDTO.builder()
                .id(3L)
                .status(CLOSING)
                .currencyPair(new CurrencyPairDTO(ETH, BTC))
                .amount(new BigDecimal("1"))
                .stopGainPercentageRule(12f)
                .openOrderId("OPEN_ORDER_03")
                .closeOrderId("OPEN_ORDER_04")
                .trades(Collections.singleton(openTrade1))
                .create();
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
                .type(BID)
                .create();
        TradeDTO closeTrade4 = TradeDTO.builder().id("0000004")
                .currencyPair(new CurrencyPairDTO(ETH, BTC))
                .feeAmount(new BigDecimal(1))
                .feeCurrency(BTC)
                .orderId("OPEN_ORDER_05")
                .originalAmount(new BigDecimal(1))
                .price(new BigDecimal(2))
                .timestamp(ZonedDateTime.now())
                .type(ASK)
                .create();
        final Set<TradeDTO> tradesP4 = new LinkedHashSet<>();
        tradesP4.add(openTrade4);
        tradesP4.add(closeTrade4);
        PositionDTO p4 = PositionDTO.builder()
                .id(4L)
                .status(CLOSED)
                .currencyPair(new CurrencyPairDTO(ETH, BTC))
                .amount(new BigDecimal(1))
                .stopGainPercentageRule(12F)
                .stopLossPercentageRule(9F)
                .openOrderId("OPEN_ORDER_04")
                .closeOrderId("OPEN_ORDER_04")
                .trades(tradesP4)
                .lowestPrice(new BigDecimal(1))
                .highestPrice(new BigDecimal(2))
                .create();
        p4.tradeUpdate(closeTrade4);
        assertEquals(p4.toString(), "Position n°4 (12.0 % gain rule / 9.0 % loss rule) on ETH/BTC - Closed - Gain : 100 %");
    }

}
