package tech.cassandre.trading.bot.test.issues.v5_x.v5_0_7;

import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.service.trade.TradeService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static org.knowm.xchange.dto.Order.OrderStatus.FILLED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
@SuppressWarnings("unused")
public class Issue852TestMock extends BaseMock {

    @Override
    public TradeService getXChangeTradeServiceMock() throws IOException {
        final TradeService mock = mock(TradeService.class);

        // Usual getOpenOrders() doesn't work.
        given(mock.getOpenOrders()).willThrow(new NotAvailableFromExchangeException());

        // Using the getOpenOrders() that requires a parameter.
        given(mock.getOpenOrders(any())).willReturn(new OpenOrders(List.of(new LimitOrder(
                Order.OrderType.ASK,        // Type.
                new BigDecimal("11"),   // OriginalAmount.
                XCHANGE_ETH_BTC,            // Instrument.
                "ORDER_0000002",            // ID.
                new Date(),                 // Date.
                ZERO,                       // Limit price.
                new BigDecimal("1"),    // Average price.
                new BigDecimal("111"),  // Cumulative amount.
                new BigDecimal("1"),    // Fee.
                FILLED,                     // Status.
                "Updated order !"           // Reference.
        ))));

        return mock;
    }
}
