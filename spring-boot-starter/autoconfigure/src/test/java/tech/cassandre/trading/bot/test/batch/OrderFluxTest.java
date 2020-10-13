package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.batch.mocks.OrderFluxTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DisplayName("Batch - Order flux")
@Configuration({
        @Property(key = "TEST_NAME", value = "Batch - Order flux")
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(OrderFluxTestMock.class)
public class OrderFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TradeService tradeService;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        final int numberOfOrdersExpected = 7;
        final int numberOfTradeServiceCalls = 3;

        // Waiting for the trade service to have been called with all the test data.
        await().untilAsserted(() -> verify(tradeService, atLeast(numberOfTradeServiceCalls)).getOpenOrders());

        // Checking that somme tickers have already been treated (to verify we work on a single thread).
        assertTrue(strategy.getOrdersUpdateReceived().size() <= numberOfOrdersExpected);
        assertTrue(strategy.getOrdersUpdateReceived().size() > 0);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertTrue(strategy.getOrdersUpdateReceived().size() >= numberOfOrdersExpected));

        // Test all values received.
        final Iterator<OrderDTO> iterator = strategy.getOrdersUpdateReceived().iterator();

        // Value 1.
        OrderDTO order = iterator.next();
        assertEquals("000001", order.getId());

        // Value 2.
        order = iterator.next();
        assertEquals("000002", order.getId());

        // Value 3.
        order = iterator.next();
        assertEquals("000003", order.getId());

        // Value 3 : the original amount changed.
        order = iterator.next();
        assertEquals("000003", order.getId());
        assertEquals(0, new BigDecimal(2).compareTo(order.getOriginalAmount()));

        // Value 4 : new order.
        order = iterator.next();
        assertEquals("000004", order.getId());

        // Value 5 : average price changed.
        order = iterator.next();
        assertEquals("000002", order.getId());
        assertEquals(0, new BigDecimal(1).compareTo(order.getAveragePrice()));

        // Value 6 : fee changed.
        order = iterator.next();
        assertEquals("000004", order.getId());
        assertEquals(0, new BigDecimal(1).compareTo(order.getFee()));
    }

}
