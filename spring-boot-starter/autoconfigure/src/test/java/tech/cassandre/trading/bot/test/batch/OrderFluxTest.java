package tech.cassandre.trading.bot.test.batch;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.mock.batch.OrderFluxTestMock;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
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
    @CaseId(3)
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // =============================================================================================================
        // Test asynchronous flux.

        // We will call the service 4 times with the second reply empty.
        // First call : 3 orders.
        // - Order ORDER_000001.
        // - Order ORDER_000002.
        // - Order ORDER_000003.
        // Second call : no data.
        // Third call : 4 orders.
        // - Order ORDER_000001 : no changes.
        // - Order ORDER_000002 : no changes.
        // - Order ORDER_000003 : the original amount changed.
        // - Order ORDER_000004 : new order.
        // Fourth call : 4 orders
        // - Order ORDER_000001 : no changes.
        // - Order ORDER_000002 : no changes.
        // - Order ORDER_000002 : average prince changed.
        // - Order ORDER_000004 : leverage changed.
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

        // Check update 1.
        OrderDTO o = orders.next();
        assertNull(o.getId());
        assertEquals("ORDER_000001", o.getOrderId());
        assertEquals(ASK, o.getType());
        assertEquals(1, o.getStrategy().getId());
        assertEquals("01", o.getStrategy().getStrategyId());
        assertEquals(cp1, o.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", cp1.getBaseCurrency()), o.getAmount());
        assertEquals(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()), o.getAveragePrice());
        assertEquals(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()), o.getLimitPrice());
        assertEquals("leverage1", o.getLeverage());
        assertEquals(NEW, o.getStatus());
        assertEquals(new CurrencyAmountDTO("2", cp1.getBaseCurrency()), o.getCumulativeAmount());
        assertEquals("MY_REF_1", o.getUserReference());
        assertTrue(createDate(1).isEqual(o.getTimestamp()));

        // Check update 2.
        o = orders.next();
        assertNull(o.getId());
        assertEquals("ORDER_000002", o.getOrderId());
        assertEquals(BID, o.getType());
        assertEquals(1, o.getStrategy().getId());
        assertEquals("01", o.getStrategy().getStrategyId());
        assertEquals(cp2, o.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("11", cp2.getBaseCurrency()), o.getAmount());
        assertEquals(new CurrencyAmountDTO("13", cp2.getQuoteCurrency()), o.getAveragePrice());
        assertEquals(new CurrencyAmountDTO("15", cp2.getQuoteCurrency()), o.getLimitPrice());
        assertEquals("leverage2", o.getLeverage());
        assertEquals(NEW, o.getStatus());
        assertEquals(new CurrencyAmountDTO("12", cp2.getBaseCurrency()), o.getCumulativeAmount());
        assertEquals("MY_REF_2", o.getUserReference());
        assertTrue(createDate(2).isEqual(o.getTimestamp()));

        // Check update 3.
        o = orders.next();
        assertNull(o.getId());
        assertEquals("ORDER_000003", o.getOrderId());
        assertEquals(ASK, o.getType());
        assertEquals(1, o.getStrategy().getId());
        assertEquals("01", o.getStrategy().getStrategyId());
        assertEquals(cp1, o.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", cp1.getBaseCurrency()), o.getAmount());
        assertEquals(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()), o.getAveragePrice());
        assertEquals(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()), o.getLimitPrice());
        assertEquals("leverage1", o.getLeverage());
        assertEquals(NEW, o.getStatus());
        assertEquals(new CurrencyAmountDTO("2", cp1.getBaseCurrency()), o.getCumulativeAmount());
        assertEquals("MY_REF_1", o.getUserReference());
        assertTrue(createDate(1).isEqual(o.getTimestamp()));

        // Check update 3 : the amount changed on ORDER_000003.
        o = orders.next();
        assertNull(o.getId());
        assertEquals("ORDER_000003", o.getOrderId());
        assertEquals(ASK, o.getType());
        assertEquals(1, o.getStrategy().getId());
        assertEquals("01", o.getStrategy().getStrategyId());
        assertEquals(cp1, o.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("2", cp1.getBaseCurrency()), o.getAmount());
        assertEquals(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()), o.getAveragePrice());
        assertEquals(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()), o.getLimitPrice());
        assertEquals("leverage1", o.getLeverage());
        assertEquals(NEW, o.getStatus());
        assertEquals(new CurrencyAmountDTO("2", cp1.getBaseCurrency()), o.getCumulativeAmount());
        assertEquals("MY_REF_1", o.getUserReference());
        assertTrue(createDate(1).isEqual(o.getTimestamp()));

        // Check update 4 : ORDER_000004 is a new order.
        o = orders.next();
        assertNull(o.getId());
        assertEquals("ORDER_000004", o.getOrderId());
        assertEquals(ASK, o.getType());
        assertEquals(1, o.getStrategy().getId());
        assertEquals("01", o.getStrategy().getStrategyId());
        assertEquals(cp1, o.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", cp1.getBaseCurrency()), o.getAmount());
        assertEquals(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()), o.getAveragePrice());
        assertEquals(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()), o.getLimitPrice());
        assertEquals("leverage1", o.getLeverage());
        assertEquals(NEW, o.getStatus());
        assertEquals(new CurrencyAmountDTO("2", cp1.getBaseCurrency()), o.getCumulativeAmount());
        assertEquals("MY_REF_1", o.getUserReference());
        assertTrue(createDate(1).isEqual(o.getTimestamp()));

        // Check update 5 : average price changed on ORDER_000002.
        o = orders.next();
        assertNull(o.getId());
        assertEquals("ORDER_000002", o.getOrderId());
        assertEquals(BID, o.getType());
        assertEquals(1, o.getStrategy().getId());
        assertEquals("01", o.getStrategy().getStrategyId());
        assertEquals(cp2, o.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("11", cp2.getBaseCurrency()), o.getAmount());
        assertEquals(new CurrencyAmountDTO("1", cp2.getQuoteCurrency()), o.getAveragePrice());
        assertEquals(new CurrencyAmountDTO("15", cp2.getQuoteCurrency()), o.getLimitPrice());
        assertEquals("leverage2", o.getLeverage());
        assertEquals(NEW, o.getStatus());
        assertEquals(new CurrencyAmountDTO("12", cp2.getBaseCurrency()), o.getCumulativeAmount());
        assertEquals("MY_REF_2", o.getUserReference());
        assertTrue(createDate(2).isEqual(o.getTimestamp()));

        // Check update 6 : leverage changed on ORDER_000004.
        o = orders.next();
        assertNull(o.getId());
        assertEquals("ORDER_000004", o.getOrderId());
        assertEquals(ASK, o.getType());
        assertEquals(1, o.getStrategy().getId());
        assertEquals("01", o.getStrategy().getStrategyId());
        assertEquals(cp1, o.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", cp1.getBaseCurrency()), o.getAmount());
        assertEquals(new CurrencyAmountDTO("3", cp1.getQuoteCurrency()), o.getAveragePrice());
        assertEquals(new CurrencyAmountDTO("5", cp1.getQuoteCurrency()), o.getLimitPrice());
        assertEquals("leverage2", o.getLeverage());
        assertEquals(NEW, o.getStatus());
        assertEquals(new CurrencyAmountDTO("2", cp1.getBaseCurrency()), o.getCumulativeAmount());
        assertEquals("MY_REF_1", o.getUserReference());
        assertTrue(createDate(1).isEqual(o.getTimestamp()));

        // =============================================================================================================
        // Check data we have in strategy & database.
        assertEquals(4, orderRepository.count());
        final Map<String, OrderDTO> strategyOrders = strategy.getOrders();
        assertEquals(4, strategyOrders.size());
        assertNotNull(strategyOrders.get("ORDER_000001"));
        assertNotNull(strategyOrders.get("ORDER_000002"));
        assertNotNull(strategyOrders.get("ORDER_000003"));
        assertNotNull(strategyOrders.get("ORDER_000004"));

        // Order ORDER_000001.
        final Optional<OrderDTO> o1 = strategy.getOrderByOrderId("ORDER_000001");
        assertTrue(o1.isPresent());
        assertEquals(1, o1.get().getId());
        assertEquals("ORDER_000001", o1.get().getOrderId());
        assertEquals(ASK, o1.get().getType());
        assertEquals(1, o1.get().getStrategy().getId());
        assertEquals("01", o1.get().getStrategy().getStrategyId());
        assertEquals(cp1, o1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("1").compareTo(o1.get().getAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), o1.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("3").compareTo(o1.get().getAveragePrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), o1.get().getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("5").compareTo(o1.get().getLimitPrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), o1.get().getLimitPrice().getCurrency());
        assertEquals("leverage1", o1.get().getLeverage());
        assertEquals(NEW, o1.get().getStatus());
        assertEquals(0, new BigDecimal("2").compareTo(o1.get().getCumulativeAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), o1.get().getCumulativeAmount().getCurrency());
        assertEquals("MY_REF_1", o1.get().getUserReference());
        assertTrue(createDate(1).isEqual(o1.get().getTimestamp()));

        // Order ORDER_000002.
        final Optional<OrderDTO> o2 = strategy.getOrderByOrderId("ORDER_000002");
        assertTrue(o2.isPresent());
        assertEquals(2, o2.get().getId());
        assertEquals("ORDER_000002", o2.get().getOrderId());
        assertEquals(BID, o2.get().getType());
        assertEquals(1, o2.get().getStrategy().getId());
        assertEquals("01", o2.get().getStrategy().getStrategyId());
        assertEquals(cp2, o2.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("11").compareTo(o2.get().getAmount().getValue()));
        assertEquals(cp2.getBaseCurrency(), o2.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("1").compareTo(o2.get().getAveragePrice().getValue()));
        assertEquals(cp2.getQuoteCurrency(), o2.get().getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("15").compareTo(o2.get().getLimitPrice().getValue()));
        assertEquals(cp2.getQuoteCurrency(), o2.get().getLimitPrice().getCurrency());
        assertEquals("leverage2", o2.get().getLeverage());
        assertEquals(NEW, o2.get().getStatus());
        assertEquals(0, new BigDecimal("12").compareTo(o2.get().getCumulativeAmount().getValue()));
        assertEquals(cp2.getBaseCurrency(), o2.get().getCumulativeAmount().getCurrency());
        assertEquals("MY_REF_2", o2.get().getUserReference());
        assertTrue(createDate(2).isEqual(o2.get().getTimestamp()));

        // Order ORDER_000003.
        final Optional<OrderDTO> o3 = strategy.getOrderByOrderId("ORDER_000003");
        assertTrue(o3.isPresent());
        assertEquals(3, o3.get().getId());
        assertEquals("ORDER_000003", o3.get().getOrderId());
        assertEquals(ASK, o3.get().getType());
        assertEquals(1, o3.get().getStrategy().getId());
        assertEquals("01", o3.get().getStrategy().getStrategyId());
        assertEquals(cp1, o3.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("2").compareTo(o3.get().getAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), o3.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("3").compareTo(o3.get().getAveragePrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), o3.get().getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("5").compareTo(o3.get().getLimitPrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), o3.get().getLimitPrice().getCurrency());
        assertEquals("leverage1", o3.get().getLeverage());
        assertEquals(NEW, o3.get().getStatus());
        assertEquals(0, new BigDecimal("2").compareTo(o3.get().getCumulativeAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), o3.get().getCumulativeAmount().getCurrency());
        assertEquals("MY_REF_1", o3.get().getUserReference());
        assertTrue(createDate(1).isEqual(o3.get().getTimestamp()));

        // Order ORDER_000004.
        final Optional<OrderDTO> o4 = strategy.getOrderByOrderId("ORDER_000004");
        assertTrue(o4.isPresent());
        assertEquals(4, o4.get().getId());
        assertEquals("ORDER_000004", o4.get().getOrderId());
        assertEquals(ASK, o4.get().getType());
        assertEquals(1, o4.get().getStrategy().getId());
        assertEquals("01", o4.get().getStrategy().getStrategyId());
        assertEquals(cp1, o4.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("1").compareTo(o4.get().getAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), o4.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("3").compareTo(o4.get().getAveragePrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), o4.get().getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("5").compareTo(o4.get().getLimitPrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), o4.get().getLimitPrice().getCurrency());
        assertEquals("leverage2", o4.get().getLeverage());
        assertEquals(NEW, o4.get().getStatus());
        assertEquals(0, new BigDecimal("2").compareTo(o4.get().getCumulativeAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), o4.get().getCumulativeAmount().getCurrency());
        assertEquals("MY_REF_1", o4.get().getUserReference());
        assertTrue(createDate(1).isEqual(o4.get().getTimestamp()));
    }

}
