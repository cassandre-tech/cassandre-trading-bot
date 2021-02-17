package tech.cassandre.trading.bot.test.service.xchange;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.service.trade.TradeService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import static org.knowm.xchange.dto.marketdata.Trades.TradeSortType.SortByTimestamp;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

@TestConfiguration
public class PositionServiceTestMock extends BaseMock {

    @Override
    public TradeService getXChangeTradeServiceMock() throws IOException {
        org.knowm.xchange.service.trade.TradeService mock = mock(org.knowm.xchange.service.trade.TradeService.class);

        // No trades returned - they will be directly emitted by the test.
        given(mock.getTradeHistory(any())).willReturn(new UserTrades(Collections.emptyList(), SortByTimestamp));

        // Position 1 creation reply (order ORDER00010).
        MarketOrder m = new MarketOrder(Order.OrderType.BID, new BigDecimal("0.0001"), xChangeCP1);
        given(mock.placeMarketOrder(m)).willReturn("ORDER00010");

        // Position 2 creation reply (order ORDER00020).
        m = new MarketOrder(Order.OrderType.BID, new BigDecimal("0.0002"), xChangeCP2);
        given(mock.placeMarketOrder(m)).willReturn("ORDER00020");

        // Position 3 creation reply (order ORDER00030).
        m = new MarketOrder(Order.OrderType.BID, new BigDecimal("0.0003"), xChangeCP1);
        given(mock.placeMarketOrder(m)).willThrow(new RuntimeException("Error exception"));

        // For checkLowestHighestAndLatestGain().
        // Position 1.
        // Opening reply (order ORDER00010).
        m = new MarketOrder(Order.OrderType.BID, new BigDecimal("10"), xChangeCP1);
        given(mock.placeMarketOrder(m)).willReturn("ORDER00010");
        // Closing reply (order ORDER00011).
        m = new MarketOrder(Order.OrderType.ASK, new BigDecimal("10"), xChangeCP1);
        given(mock.placeMarketOrder(m)).willReturn("ORDER00011");

        return mock;
    }

}
