package tech.cassandre.trading.bot.test.batch;

import liquibase.pro.packaged.B;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static java.math.BigDecimal.ZERO;
import static org.knowm.xchange.dto.Order.OrderStatus.FILLED;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class OrderFluxTestMock extends BaseMock {

    @Override
    public org.knowm.xchange.service.trade.TradeService getXChangeTradeServiceMock() throws IOException {
        org.knowm.xchange.service.trade.TradeService tradeServiceMock = mock(org.knowm.xchange.service.trade.TradeService.class);

        // =============================================================================================================
        // Order creation mock.

        // Order ORDER_000001 (ASK, 1, ETH/BTC).
        MarketOrder m = new MarketOrder(Order.OrderType.ASK, new BigDecimal("1"), xChanceCP1);
        given(tradeServiceMock.placeMarketOrder(m)).willReturn("ORDER_000001");
        // Order ORDER_000002 (BID, 2, ETH/USDT).
        m = new MarketOrder(Order.OrderType.BID, new BigDecimal("2"), xChanceCP2);
        given(tradeServiceMock.placeMarketOrder(m)).willReturn("ORDER_000002");
        // Order ORDER_000003 (ASK, 3, ETH/BTC).
        m = new MarketOrder(Order.OrderType.ASK, new BigDecimal("3"), xChanceCP1);
        given(tradeServiceMock.placeMarketOrder(m)).willReturn("ORDER_000003");
        // Order ORDER_000004 (BID, 4, ETH/USDT).
        m = new MarketOrder(Order.OrderType.BID, new BigDecimal("4"), xChanceCP2);
        given(tradeServiceMock.placeMarketOrder(m)).willReturn("ORDER_000004");

        // =============================================================================================================
        // Order creation mock.

        given(tradeServiceMock.getOpenOrders()).willReturn(
                // =====================================================================================================
                // Reply 01.
                // - Order ORDER_000001.
                // - Order ORDER_000002.
                // - Order ORDER_000003.
                getReply01(),
                // =====================================================================================================
                // Reply 02.
                // - Order ORDER_000001 : no changes.
                // - Order ORDER_000002 : no changes.
                // - Order ORDER_000003 : the original amount changed.
                // - Order ORDER_000004 : new order (but not yet created in database).
                getReply02(),
                // =====================================================================================================
                // Reply 03.
                // - Order ORDER_000002 : original amount changed.
                // - Order ORDER_000004 : original Amount changed.
                getReply03()
        );

        return tradeServiceMock;
    }

    /**
     * Reply 01.
     * First call : 3 orders.
     * - Order ORDER_000001.
     * - Order ORDER_000002.
     * - Order ORDER_000003.
     *
     * @return reply
     */
    private OpenOrders getReply01() {
        LimitOrder order000001 = new LimitOrder(
                Order.OrderType.ASK,        // Type.
                new BigDecimal("11"),   // OriginalAmount.
                xChanceCP1,                 // Instrument.
                "ORDER_000001",             // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("1"),    // Average price.
                new BigDecimal("111"),  // Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "My reference"  // Reference.
        );

        LimitOrder order000002 = new LimitOrder(
                Order.OrderType.BID,        // Type.
                new BigDecimal("22"),   // OriginalAmount.
                xChanceCP2,                 // Instrument.
                "ORDER_000002",          // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("1"),    // Average price.
                new BigDecimal("222"),  // Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "My reference"  // Reference.
        );

        LimitOrder order000003 = new LimitOrder(
                Order.OrderType.ASK,        // Type.
                new BigDecimal("33"),   // OriginalAmount.
                xChanceCP1,                 // Instrument.
                "ORDER_000003",          // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("1"),    // Average price.
                new BigDecimal("333"),  // Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "My reference"  // Reference.
        );

        return new OpenOrders(Arrays.asList(order000001,
                order000002,
                order000003));
    }

    /**
     * Reply 02.
     * First call : 3 orders.
     * - Order ORDER_000001 : no changes.
     * - Order ORDER_000002 : no changes.
     * - Order ORDER_000003 : the original amount changed.
     * - Order ORDER_000004 : new order (but not yet created in database).
     *
     * @return reply
     */
    private OpenOrders getReply02() {
        LimitOrder order000001 = new LimitOrder(
                Order.OrderType.ASK,        // Type.
                new BigDecimal("11"),   // OriginalAmount.
                xChanceCP1,                 // Instrument.
                "ORDER_000001",         // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("1"),    // Average price.
                new BigDecimal("111"),  // Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "My reference"  // Reference.
        );

        LimitOrder order000002 = new LimitOrder(
                Order.OrderType.BID,        // Type.
                new BigDecimal("22"),   // OriginalAmount.
                xChanceCP2,                 // Instrument.
                "ORDER_000002",          // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("1"),    // Average price.
                new BigDecimal("222"),  // Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "My reference"  // Reference.
        );

        LimitOrder order000003 = new LimitOrder(
                Order.OrderType.ASK,        // Type.
                new BigDecimal("3333"), // OriginalAmount.
                xChanceCP1,                 // Instrument.
                "ORDER_000003",             // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("1"),    // Average price.
                new BigDecimal("33333"),// Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "My reference"              // Reference.
        );

        LimitOrder order000004 = new LimitOrder(
                Order.OrderType.ASK,        // Type.
                new BigDecimal("444"),  // OriginalAmount.
                xChanceCP1,                 // Instrument.
                "ORDER_000004",          // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("1"),    // Average price.
                new BigDecimal("4444"), // Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "My reference"  // Reference.
        );

        return new OpenOrders(Arrays.asList(order000001,
                order000002,
                order000003,
                order000004));
    }

    /**
     * Reply 03.
     * First call : 3 orders.
     * - Order ORDER_000001 : no change.
     * - Order ORDER_000002 : average price changed.
     * - Order ORDER_000003 : no change.
     * - Order ORDER_000004 : average price changed.
     *
     * @return reply
     */
    private OpenOrders getReply03() {
        LimitOrder order000001 = new LimitOrder(
                Order.OrderType.ASK,        // Type.
                new BigDecimal("11"),   // OriginalAmount.
                xChanceCP1,                 // Instrument.
                "ORDER_000001",         // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("1"),    // Average price.
                new BigDecimal("111"),  // Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "My reference"  // Reference.
        );

        LimitOrder order000002 = new LimitOrder(
                Order.OrderType.BID,        // Type.
                new BigDecimal("22"),   // OriginalAmount.
                xChanceCP2,                 // Instrument.
                "ORDER_000002",          // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("2"),    // Average price.
                new BigDecimal("222"),  // Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "My reference"  // Reference.
        );

        LimitOrder order000003 = new LimitOrder(
                Order.OrderType.ASK,        // Type.
                new BigDecimal("3333"), // OriginalAmount.
                xChanceCP1,                 // Instrument.
                "ORDER_000003",             // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("1"),    // Average price.
                new BigDecimal("33333"),// Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "My reference"  // Reference.
        );

        LimitOrder order000004 = new LimitOrder(
                Order.OrderType.ASK,        // Type.
                new BigDecimal("444"),  // OriginalAmount.
                xChanceCP1,                 // Instrument.
                "ORDER_000004",          // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("2"),    // Average price.
                new BigDecimal("4444"), // Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "My reference"  // Reference.
        );

        return new OpenOrders(Arrays.asList(order000001,
                order000002,
                order000003,
                order000004));
    }

}
