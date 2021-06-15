package tech.cassandre.trading.bot.test.batch.mocks;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrades;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static java.math.BigDecimal.ZERO;
import static org.knowm.xchange.dto.Order.OrderStatus.FILLED;
import static org.knowm.xchange.dto.marketdata.Trades.TradeSortType.SortByTimestamp;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class OrderFluxTestMock extends BaseMock {

    @Override
    public org.knowm.xchange.service.trade.TradeService getXChangeTradeServiceMock() throws IOException {
        org.knowm.xchange.service.trade.TradeService mock = mock(org.knowm.xchange.service.trade.TradeService.class);

        given(mock.getTradeHistory(any())).willReturn(new UserTrades(Collections.emptyList(), SortByTimestamp));

        // =============================================================================================================
        // Order creation mock.

        // Order ORDER_000001 (ASK, 1, ETH/BTC).
        MarketOrder m = new MarketOrder(Order.OrderType.ASK, new BigDecimal("1"), XCHANGE_ETH_BTC);
        given(mock.placeMarketOrder(m)).willReturn("ORDER_000001");
        // Order ORDER_000002 (BID, 2, ETH/USDT).
        m = new MarketOrder(Order.OrderType.BID, new BigDecimal("2"), XCHANGE_ETH_USDT);
        given(mock.placeMarketOrder(m)).willReturn("ORDER_000002");
        // Order ORDER_000003 (ASK, 3, ETH/BTC).
        m = new MarketOrder(Order.OrderType.ASK, new BigDecimal("3"), XCHANGE_ETH_BTC);
        given(mock.placeMarketOrder(m)).willReturn("ORDER_000003");
        // Order ORDER_000004 (BID, 4, ETH/USDT).
        m = new MarketOrder(Order.OrderType.BID, new BigDecimal("4"), XCHANGE_ETH_USDT);
        given(mock.placeMarketOrder(m)).willReturn("ORDER_000004");

        // =============================================================================================================
        // Order creation mock.

        given(mock.getOpenOrders()).willReturn(
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

        return mock;
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
                XCHANGE_ETH_BTC,            // Instrument.
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
                XCHANGE_ETH_USDT,           // Instrument.
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
                XCHANGE_ETH_BTC,            // Instrument.
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
                XCHANGE_ETH_BTC,                 // Instrument.
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
                XCHANGE_ETH_USDT,                 // Instrument.
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
                XCHANGE_ETH_BTC,                 // Instrument.
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
                XCHANGE_ETH_BTC,                 // Instrument.
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
                XCHANGE_ETH_BTC,                 // Instrument.
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
                XCHANGE_ETH_USDT,                 // Instrument.
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
                XCHANGE_ETH_BTC,                 // Instrument.
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
                XCHANGE_ETH_BTC,                 // Instrument.
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
