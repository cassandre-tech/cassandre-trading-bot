package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.mock.batch.OrderFluxTestMock;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.KCS;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_TRADE;

@SpringBootTest
@DisplayName("Batch - Order flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_RATE_TRADE, value = "100")
})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Import(OrderFluxTestMock.class)
public class OrderFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TradeService tradeService;

    @Autowired
    private TradeFlux tradeFlux;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // =============================================================================================================
        // Test asynchronous flux.

        // We will call the service 4 times with the second reply empty.
        // First call : 3 orders.
        // - Order 000001.
        // - Order 000002.
        // - Order 000003.
        // Second call : 4 orders.
        // - Order 000001 : no changes.
        // - Order 000002 : no changes.
        // - Order 000003 : the original amount changed.
        // - Order 000004 : new order.
        // Third call : 4 orders
        // - Order 000001 : no changes.
        // - Order 000002 : no changes.
        // - Order 000002 : average prince changed.
        // - Order 000004 : fee changed.
        final int numberOfUpdatesExpected = 7;
        final int numberOfServiceCallsExpected = 4;

        // Waiting for the service to have been called with all the test data.
        await().untilAsserted(() -> verify(tradeService, atLeast(numberOfServiceCallsExpected)).getOrders());

        // Checking that somme data have already been treated.
        // but not all as the flux should be asynchronous and single thread and strategy method method waits 1 second.
        assertTrue(strategy.getOrdersUpdateReceived().size() > 0);
        assertTrue(strategy.getOrdersUpdateReceived().size() <= numberOfUpdatesExpected);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertTrue(strategy.getOrdersUpdateReceived().size() >= numberOfUpdatesExpected));
        final Iterator<OrderDTO> orders = strategy.getOrdersUpdateReceived().iterator();

        // =============================================================================================================
        // Test all values received by the strategy with update methods.

        // Value 1.
        OrderDTO o = orders.next();
        assertEquals("000001", o.getId());

        // Value 2.
        o = orders.next();
        assertEquals("000002", o.getId());

        // Value 3.
        o = orders.next();
        assertEquals("000003", o.getId());

        // Value 3 : the original amount changed.
        o = orders.next();
        assertEquals("000003", o.getId());
        assertEquals(0, new BigDecimal("2").compareTo(o.getAmount().getValue()));

        // Value 4 : new order.
        o = orders.next();
        assertEquals("000004", o.getId());

        // Value 5 : average price changed.
        o = orders.next();
        assertEquals("000002", o.getId());
        assertEquals(0, new BigDecimal(1).compareTo(o.getAveragePrice().getValue()));

        // Value 6 : fee changed.
        o = orders.next();
        assertEquals("000004", o.getId());
        // assertEquals(0, new BigDecimal(1).compareTo(o.getFee().getValue()));
        // assertEquals(KCS, o.getFee().getCurrency());

        // =============================================================================================================
        // Check data we have in strategy.
        final Map<String, OrderDTO> strategyOrders = strategy.getOrders();
        assertEquals(4, strategyOrders.size());

        // Order 000001.
        final OrderDTO order1DTO = strategyOrders.get("000001");
        assertNotNull(order1DTO);
        assertNotNull(strategy.getOrderById("000001"));
        assertEquals("000001", order1DTO.getId());
        assertEquals(ASK, order1DTO.getType());
        assertEquals(0, new BigDecimal("1").compareTo(order1DTO.getAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), order1DTO.getAmount().getCurrency());
        assertEquals(cp1, order1DTO.getCurrencyPair());
        assertEquals("MY_REF_1", order1DTO.getUserReference());
        assertTrue(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).isEqual(order1DTO.getTimestamp()));
        assertEquals(NEW, order1DTO.getStatus());
        assertEquals(0, new BigDecimal("2").compareTo(order1DTO.getCumulativeAmount().getValue()));
        assertEquals(0, new BigDecimal("3").compareTo(order1DTO.getAveragePrice().getValue()));
        assertEquals("leverage1", order1DTO.getLeverage());
        assertEquals(0, new BigDecimal("5").compareTo(order1DTO.getLimitPrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), order1DTO.getLimitPrice().getCurrency());
        // Order 000002.
        final OrderDTO order2DTO = strategyOrders.get("000002");
        assertNotNull(order2DTO);
        assertNotNull(strategy.getOrderById("000002"));
        assertEquals("000002", order2DTO.getId());
        assertEquals(ASK, order2DTO.getType());
        assertEquals(0, new BigDecimal("1").compareTo(order1DTO.getAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), order1DTO.getAmount().getCurrency());
        assertEquals(cp1, order2DTO.getCurrencyPair());
        assertEquals("MY_REF_1", order2DTO.getUserReference());
        assertTrue(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).isEqual(order2DTO.getTimestamp()));
        assertEquals(NEW, order2DTO.getStatus());
        assertEquals(0, new BigDecimal("2").compareTo(order2DTO.getCumulativeAmount().getValue()));
        assertEquals(0, new BigDecimal("1").compareTo(order2DTO.getAveragePrice().getValue()));
        assertEquals("leverage1", order2DTO.getLeverage());
        assertEquals(0, new BigDecimal("5").compareTo(order2DTO.getLimitPrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), order2DTO.getLimitPrice().getCurrency());
        // Order 000003.
        final OrderDTO order3DTO = strategyOrders.get("000003");
        assertNotNull(order3DTO);
        assertNotNull(strategy.getOrderById("000003"));
        assertEquals("000003", order3DTO.getId());
        assertEquals(ASK, order3DTO.getType());
        assertEquals(0, new BigDecimal("2").compareTo(order3DTO.getAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), order3DTO.getAmount().getCurrency());
        assertEquals(cp1, order3DTO.getCurrencyPair());
        assertEquals("MY_REF_1", order3DTO.getUserReference());
        assertTrue(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).isEqual(order3DTO.getTimestamp()));
        assertEquals(NEW, order3DTO.getStatus());
        assertEquals(0, new BigDecimal("2").compareTo(order3DTO.getCumulativeAmount().getValue()));
        assertEquals(0, new BigDecimal("3").compareTo(order3DTO.getAveragePrice().getValue()));
        assertEquals("leverage1", order3DTO.getLeverage());
        assertEquals(0, new BigDecimal("5").compareTo(order3DTO.getLimitPrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), order3DTO.getLimitPrice().getCurrency());
        // Order 000004.
        final OrderDTO order4DTO = strategyOrders.get("000004");
        assertNotNull(order4DTO);
        assertNotNull(strategy.getOrderById("000004"));
        assertEquals("000004", order4DTO.getId());
        assertEquals(ASK, order4DTO.getType());
        assertEquals(0, new BigDecimal("1").compareTo(order4DTO.getAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), order4DTO.getAmount().getCurrency());
        assertEquals(cp1, order4DTO.getCurrencyPair());
        assertEquals("MY_REF_1", order4DTO.getUserReference());
        assertTrue(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).isEqual(order4DTO.getTimestamp()));
        assertEquals(NEW, order4DTO.getStatus());
        assertEquals(0, new BigDecimal("2").compareTo(order4DTO.getCumulativeAmount().getValue()));
        assertEquals(0, new BigDecimal("3").compareTo(order4DTO.getAveragePrice().getValue()));
        assertEquals("leverage2", order4DTO.getLeverage());
        assertEquals(0, new BigDecimal("5").compareTo(order4DTO.getLimitPrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), order4DTO.getLimitPrice().getCurrency());

        // =============================================================================================================
        // Check data in database.
        assertEquals(4, orderRepository.count());
        assertTrue(orderRepository.findById("NON_EXISTING").isEmpty());

        // Order 000001.
        final Optional<Order> optionalOrder1 = orderRepository.findById("000001");
        assertTrue(optionalOrder1.isPresent());
        final Order order1 = optionalOrder1.get();
        assertEquals("000001", order1.getId());
        assertEquals(ASK, order1.getType());
        assertEquals(0, new BigDecimal("1").compareTo(order1.getAmount()));
        assertEquals(cp1.toString(), order1.getCurrencyPair());
        assertEquals("MY_REF_1", order1.getUserReference());
        assertTrue(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).isEqual(order1.getTimestamp()));
        assertEquals(NEW, order1.getStatus());
        assertEquals(0, new BigDecimal("2").compareTo(order1.getCumulativeAmount()));
        assertEquals(0, new BigDecimal("3").compareTo(order1.getAveragePrice()));
        assertEquals("leverage1", order1.getLeverage());
        assertEquals(0, new BigDecimal("5").compareTo(order1.getLimitPrice()));
        // Order 000002.
        final Optional<Order> optionalOrder2 = orderRepository.findById("000002");
        assertTrue(optionalOrder2.isPresent());
        final Order order2 = optionalOrder2.get();
        assertEquals("000002", order2.getId());
        assertEquals(ASK, order2.getType());
        assertEquals(0, new BigDecimal("1").compareTo(order2.getAmount()));
        assertEquals(cp1.toString(), order2.getCurrencyPair());
        assertEquals("MY_REF_1", order2.getUserReference());
        assertTrue(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).isEqual(order2.getTimestamp()));
        assertEquals(NEW, order2.getStatus());
        assertEquals(0, new BigDecimal("2").compareTo(order2.getCumulativeAmount()));
        assertEquals(0, new BigDecimal("1").compareTo(order2.getAveragePrice()));
        assertEquals("leverage1", order2.getLeverage());
        assertEquals(0, new BigDecimal("5").compareTo(order2.getLimitPrice()));
        // Order 000003.
        final Optional<Order> optionalOrder3 = orderRepository.findById("000003");
        assertTrue(optionalOrder3.isPresent());
        final Order order3 = optionalOrder3.get();
        assertEquals("000003", order3.getId());
        assertEquals(ASK, order3.getType());
        assertEquals(0, new BigDecimal("2").compareTo(order3.getAmount()));
        assertEquals(cp1.toString(), order3.getCurrencyPair());
        assertEquals("MY_REF_1", order3.getUserReference());
        assertTrue(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).isEqual(order3.getTimestamp()));
        assertEquals(NEW, order3.getStatus());
        assertEquals(0, new BigDecimal("2").compareTo(order3.getCumulativeAmount()));
        assertEquals(0, new BigDecimal("3").compareTo(order3.getAveragePrice()));
        assertEquals("leverage1", order3.getLeverage());
        assertEquals(0, new BigDecimal("5").compareTo(order3.getLimitPrice()));
        // Order 000004.
        final Optional<Order> optionalOrder4 = orderRepository.findById("000004");
        assertTrue(optionalOrder4.isPresent());
        final Order order4 = optionalOrder4.get();
        assertEquals("000004", order4.getId());
        assertEquals(ASK, order4.getType());
        assertEquals(0, new BigDecimal("1").compareTo(order4.getAmount()));
        assertEquals(cp1.toString(), order4.getCurrencyPair());
        assertEquals("MY_REF_1", order4.getUserReference());
        assertTrue(ZonedDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).isEqual(order4.getTimestamp()));
        assertEquals(NEW, order4.getStatus());
        assertEquals(0, new BigDecimal("2").compareTo(order4.getCumulativeAmount()));
        assertEquals(0, new BigDecimal("3").compareTo(order4.getAveragePrice()));
        assertEquals("leverage2", order4.getLeverage());
        assertEquals(0, new BigDecimal("5").compareTo(order4.getLimitPrice()));
    }

}
