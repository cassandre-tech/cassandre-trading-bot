package tech.cassandre.trading.bot.test.batch.mocks;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrades;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;

import static org.knowm.xchange.dto.marketdata.Trades.TradeSortType.SortByTimestamp;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class PositionShortFluxTestMock extends BaseMock {

    @Override
    public org.knowm.xchange.service.trade.TradeService getXChangeTradeServiceMock() throws IOException {
        org.knowm.xchange.service.trade.TradeService mock = mock(org.knowm.xchange.service.trade.TradeService.class);

        // No trades or orders returned - they will be directly emitted by the test.
        given(mock.getTradeHistory(any())).willReturn(new UserTrades(Collections.emptyList(), SortByTimestamp));
        given(mock.getOpenOrders()).willReturn(new OpenOrders(Collections.emptyList()));

        // Position 1.
        // Opening order creation result.
        MarketOrder m = new MarketOrder(Order.OrderType.ASK, new BigDecimal("10"), XCHANGE_ETH_BTC);
        given(mock.placeMarketOrder(m)).willReturn("ORDER00010");
        // Closing order creation result.
        m = new MarketOrder(Order.OrderType.BID, new BigDecimal("1000"), XCHANGE_ETH_BTC);
        given(mock.placeMarketOrder(m)).willReturn("ORDER00011");

        // Position 2.
        // Opening order creation result.
        m = new MarketOrder(Order.OrderType.ASK, new BigDecimal("0.0002"), XCHANGE_ETH_USDT);
        given(mock.placeMarketOrder(m)).willReturn("ORDER00020");
        // Closing order creation result.
        m = new MarketOrder(Order.OrderType.BID, new BigDecimal("0.0002"), XCHANGE_ETH_USDT);
        given(mock.placeMarketOrder(m)).willReturn("ORDER00021");

        return mock;
    }

}
