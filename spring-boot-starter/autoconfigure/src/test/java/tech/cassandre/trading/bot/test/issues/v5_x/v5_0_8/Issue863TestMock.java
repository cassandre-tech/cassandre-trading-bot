package tech.cassandre.trading.bot.test.issues.v5_x.v5_0_8;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.trade.TradeService;
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
@SuppressWarnings("unused")
public class Issue863TestMock extends BaseMock {

    @Override
    public TradeService getXChangeTradeServiceMock() throws IOException {
        TradeService mock = mock(TradeService.class);

        // No trades returned - they will be directly emitted by the test.
        given(mock.getTradeHistory(any())).willReturn(new UserTrades(Collections.emptyList(), SortByTimestamp));

        // Position 1 opening & closing reply (order ORDER00010).
        MarketOrder m = new MarketOrder(Order.OrderType.BID, new BigDecimal("0.0001"), XCHANGE_ETH_BTC, null, null);
        given(mock.placeMarketOrder(m)).willReturn("ORDER00010");

        // Position 2 opening & closing reply (order ORDER00020).
        m = new MarketOrder(Order.OrderType.BID, new BigDecimal("0.0002"), XCHANGE_ETH_BTC, null, null);
        given(mock.placeMarketOrder(m)).willReturn("ORDER00020");

        return mock;
    }

}
