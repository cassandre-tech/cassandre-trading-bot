package tech.cassandre.trading.bot.test.batch.mocks;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.trade.TradeService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.knowm.xchange.currency.Currency.BTC;
import static org.knowm.xchange.dto.marketdata.Trades.TradeSortType.SortByTimestamp;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TradeFluxTestMock extends BaseMock {

    @Override
    public TradeService getXChangeTradeServiceMock() throws IOException {
        TradeService mock = mock(TradeService.class);

        given(mock.getOpenOrders()).willReturn(new OpenOrders(Collections.emptyList()));
        // =============================================================================================================
        // Trade mock.

        given(mock.getTradeHistory(any())).willReturn(
                // =====================================================================================================
                // Reply 01.
                // - TRADE_0000001.
                // - TRADE_0000002.
                getReply01(),
                // =====================================================================================================
                // Reply 02.
                // - TRADE_0000001 : no changes.
                // - TRADE_0000002 : no changes.
                // - TRADE_0000003 : new
                // - TRADE_0000004 : new.
                // - TRADE_0000005 : new.
                getReply02(),
                // =====================================================================================================
                // Reply 03.
                // - TRADE_0000006 : new.
                // - TRADE_0000002 : no change.
                // - TRADE_0000003 : amount changed.
                // - TRADE_0000008 : new.
                getReply03()
        );

        return mock;
    }

    /**
     * Reply 01.
     * - TRADE_0000001.
     * - TRADE_0000002.
     *
     * @return reply
     */
    private UserTrades getReply01() {
        UserTrade trade0000001 = new UserTrade(
                Order.OrderType.BID,                        // Order type.
                new BigDecimal("1.100001"),             // Original amount.
                XCHANGE_ETH_BTC,                                 // Instrument.
                new BigDecimal("2.200002"),             // Price.
                Date.from(createZonedDateTime(1).toInstant()),   // Date.
                "TRADE_0000001",                         // Trade id.
                "ORDER_0000001",                     // Order id.
                new BigDecimal("3.300003"),             // fee.
                XCHANGE_ETH_BTC.counter,                         // fee currency.
                "Ref TRADE_0000001"
        );

        UserTrade trade0000002 = new UserTrade(
                Order.OrderType.BID,                        // Order type.
                new BigDecimal("1.100001"),             // Original amount.
                XCHANGE_ETH_BTC,                                 // Instrument.
                new BigDecimal("2.200002"),             // Price.
                Date.from(createZonedDateTime("01-09-2020").toInstant()),   // Date.
                "TRADE_0000002",                         // Trade id.
                "ORDER_0000001",                     // Order id.
                new BigDecimal("3.300003"),             // fee.
                XCHANGE_ETH_BTC.counter,                         // fee currency.
                "Ref TRADE_0000002"
        );

        return new UserTrades(Arrays.asList(trade0000001, trade0000002), SortByTimestamp);
    }

    /**
     * Reply 02.
     * - TRADE_0000001 : no changes.
     * - TRADE_0000002 : no changes.
     * - TRADE_0000003 : new.
     * - TRADE_0000004 : new.
     * - TRADE_0000005 : new.
     *
     * @return reply
     */
    private UserTrades getReply02() {
        UserTrade trade0000001 = new UserTrade(
                Order.OrderType.BID,                                // Order type.
                new BigDecimal("1.100001"),                     // Original amount.
                XCHANGE_ETH_BTC,                                    // Instrument.
                new BigDecimal("2.200002"),                     // Price.
                Date.from(createZonedDateTime(1).toInstant()),  // Date.
                "TRADE_0000001",                                // Trade id.
                "ORDER_0000001",                     // Order id.
                new BigDecimal("3.300003"),             // fee.
                XCHANGE_ETH_BTC.counter,                         // fee currency.
                "Ref TRADE_0000001"
        );

        UserTrade trade0000002 = new UserTrade(
                Order.OrderType.BID,                        // Order type.
                new BigDecimal("1.100001"),             // Original amount.
                XCHANGE_ETH_BTC,                                 // Instrument.
                new BigDecimal("2.200002"),             // Price.
                Date.from(createZonedDateTime("01-09-2020").toInstant()),   // Date.
                "TRADE_0000002",                         // Trade id.
                "ORDER_0000001",                     // Order id.
                new BigDecimal("3.300003"),             // fee.
                XCHANGE_ETH_BTC.counter,                         // fee currency.
                "Ref TRADE_0000002"
        );

        UserTrade trade0000003 = new UserTrade(
                Order.OrderType.BID,                        // Order type.
                new BigDecimal("1.100001"),             // Original amount.
                XCHANGE_ETH_USDT,                                 // Instrument.
                new BigDecimal("2.200002"),             // Price.
                Date.from(createZonedDateTime("01-09-2020").toInstant()),   // Date.
                "TRADE_0000003",                         // Trade id.
                "ORDER_0000002",                     // Order id.
                new BigDecimal("3.300003"),             // fee.
                XCHANGE_ETH_USDT.base,                            // fee currency.
                "Ref TRADE_0000003"
        );

        UserTrade trade0000004 = new UserTrade(
                Order.OrderType.BID,                        // Order type.
                new BigDecimal("1"),                    // Original amount.
                XCHANGE_ETH_BTC,                                 // Instrument.
                new BigDecimal("2.200002"),             // Price.
                Date.from(createZonedDateTime("01-09-2020").toInstant()),   // Date.
                "TRADE_0000004",                         // Trade id.
                "ORDER_0000001",                     // Order id.
                new BigDecimal("3.300003"),             // fee.
                XCHANGE_ETH_BTC.counter,                         // fee currency.
                "Ref TRADE_0000004"
        );

        UserTrade trade0000005 = new UserTrade(
                Order.OrderType.BID,                        // Order type.
                new BigDecimal("1"),                    // Original amount.
                XCHANGE_ETH_BTC,                                 // Instrument.
                new BigDecimal("2.200002"),              // Price.
                Date.from(createZonedDateTime("01-09-2020").toInstant()),   // Date.
                "TRADE_0000005",                         // Trade id.
                "ORDER_0000001",                     // Order id.
                new BigDecimal("3.300003"),             // fee.
                XCHANGE_ETH_BTC.counter,                         // fee currency.
                "Ref TRADE_0000005"
        );

        return new UserTrades(Arrays.asList(trade0000001,
                trade0000002,
                trade0000003,
                trade0000004,
                trade0000005),
                SortByTimestamp);
    }

    /**
     * Reply 03.
     * - TRADE_0000006 : new.
     * - TRADE_0000002 : no change.
     * - TRADE_0000003 : amount changed.
     * - TRADE_0000008 : new.
     *
     * @return reply
     */
    private UserTrades getReply03() {
        UserTrade trade0000006 = new UserTrade(
                Order.OrderType.BID,                        // Order type.
                new BigDecimal("1.100001"),             // Original amount.
                XCHANGE_ETH_USDT,                                 // Instrument.
                new BigDecimal("2.200002"),             // Price.
                Date.from(createZonedDateTime("01-08-2018").toInstant()),   // Date.
                "TRADE_0000006",                         // Trade id.
                "ORDER_0000001",                     // Order id.
                new BigDecimal("3.300003"),             // fee.
                XCHANGE_ETH_USDT.counter,                         // fee currency.
                "Ref TRADE_0000006"
        );

        UserTrade trade0000002 = new UserTrade(
                Order.OrderType.BID,                        // Order type.
                new BigDecimal("1.100001"),             // Original amount.
                XCHANGE_ETH_BTC,                                 // Instrument.
                new BigDecimal("2.200002"),             // Price.
                Date.from(createZonedDateTime("01-09-2020").toInstant()),   // Date.
                "TRADE_0000002",                         // Trade id.
                "ORDER_0000001",                     // Order id.
                new BigDecimal("3.300003"),             // fee.
                XCHANGE_ETH_BTC.counter,                         // fee currency.
                "Ref TRADE_0000002"
        );

        UserTrade trade0000003 = new UserTrade(
                Order.OrderType.BID,                        // Order type.
                new BigDecimal("1.110001"),             // Original amount.
                XCHANGE_ETH_USDT,                                 // Instrument.
                new BigDecimal("2.220002"),             // Price.
                Date.from(createZonedDateTime("01-09-2021").toInstant()),   // Date.
                "TRADE_0000003",                         // Trade id.
                "ORDER_0000002",                     // Order id.
                new BigDecimal("3.330003"),             // fee.
                BTC,                                       // fee currency.
                "Ref TRADE_0000003"
        );

        UserTrade trade0000008 = new UserTrade(
                Order.OrderType.BID,                        // Order type.
                new BigDecimal("1.100001"),             // Original amount.
                XCHANGE_ETH_BTC,                                 // Instrument.
                new BigDecimal("2.200002"),             // Price.
                Date.from(createZonedDateTime("02-09-2020").toInstant()),   // Date.
                "TRADE_0000008",                         // Trade id.
                "ORDER_0000001",                     // Order id.
                new BigDecimal("3.300003"),             // fee.
                XCHANGE_ETH_BTC.counter,                         // fee currency.
                "Ref TRADE_0000008"
        );

        return new UserTrades(Arrays.asList(trade0000006,
                trade0000002,
                trade0000003,
                trade0000008),
                SortByTimestamp);
    }

}
