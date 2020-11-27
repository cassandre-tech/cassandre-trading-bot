package tech.cassandre.trading.bot.test.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

@SpringBootTest
@DisplayName("Domain - Order")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("schedule-disabled")
public class OrderTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderFlux orderFlux;

    @Test
    @DisplayName("Check load order from database")
    public void checkLoadOrderFromDatabase() {
        // =============================================================================================================
        // Check that positions, orders and trades in database doesn't trigger strategy events.
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());
        assertTrue(strategy.getOrdersUpdateReceived().isEmpty());

        // =============================================================================================================
        // Check order 1.
        OrderDTO order = strategy.getOrders().get("BACKUP_ORDER_01");
        assertNotNull(order);
        assertEquals("BACKUP_ORDER_01", order.getId());
        assertEquals(ASK, order.getType());
        assertEquals(0, new BigDecimal("0.000005").compareTo(order.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO("ETH/BTC"), order.getCurrencyPair());
        assertEquals("My reference 1", order.getUserReference());
        assertEquals(createZonedDateTime("18-11-2020"), order.getTimestamp());
        assertEquals(NEW, order.getStatus());
        assertEquals(0, new BigDecimal("0.000004").compareTo(order.getCumulativeAmount()));
        assertEquals(0, new BigDecimal("0.000003").compareTo(order.getAveragePrice()));
        assertEquals(0, new BigDecimal("0.000002").compareTo(order.getFee()));
        assertEquals("LEVERAGE_1", order.getLeverage());
        assertEquals(0, new BigDecimal("0.000001").compareTo(order.getLimitPrice()));

        // =============================================================================================================
        // Check order 2.
        order = strategy.getOrders().get("BACKUP_ORDER_02");
        assertNotNull(order);
        assertEquals("BACKUP_ORDER_02", order.getId());
        assertEquals(BID, order.getType());
        assertEquals(0, new BigDecimal("0.000015").compareTo(order.getOriginalAmount()));
        assertEquals(new CurrencyPairDTO("USDT/BTC"), order.getCurrencyPair());
        assertEquals("My reference 2", order.getUserReference());
        assertEquals(createZonedDateTime("19-11-2020"), order.getTimestamp());
        assertEquals(PENDING_NEW, order.getStatus());
        assertEquals(0, new BigDecimal("0.000014").compareTo(order.getCumulativeAmount()));
        assertEquals(0, new BigDecimal("0.000013").compareTo(order.getAveragePrice()));
        assertEquals(0, new BigDecimal("0.000012").compareTo(order.getFee()));
        assertEquals("LEVERAGE_2", order.getLeverage());
        assertEquals(0, new BigDecimal("0.000011").compareTo(order.getLimitPrice()));
    }

    @Test
    @DisplayName("Check save order in database")
    public void checkSaveOrderInDatabase() {
        // =============================================================================================================
        // Check that positions, orders and trades in database doesn't trigger strategy events.
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());
        assertTrue(strategy.getOrdersUpdateReceived().isEmpty());

        // =============================================================================================================
        // Add an order and check that it's correctly saved in database.
        long orderCount = orderRepository.count();
        OrderDTO order01 = OrderDTO.builder()
                .id("BACKUP_ORDER_03")
                .type(ASK)
                .originalAmount(new BigDecimal("1.00001"))
                .currencyPair(cp1)
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("1.00002"))
                .averagePrice(new BigDecimal("1.00003"))
                .fee(new BigDecimal("1.00004"))
                .leverage("leverage3")
                .limitPrice(new BigDecimal("1.00005"))
                .create();
        orderFlux.emitValue(order01);
        await().untilAsserted(() -> assertEquals(orderCount + 1, orderRepository.count()));

        // =============================================================================================================
        // Order - Check created order (domain).
        final Optional<Order> orderInDatabase = orderRepository.findById("BACKUP_ORDER_03");
        assertTrue(orderInDatabase.isPresent());
        assertEquals("BACKUP_ORDER_03", orderInDatabase.get().getId());
        assertEquals(ASK, orderInDatabase.get().getType());
        assertEquals(0, new BigDecimal("1.00001").compareTo(orderInDatabase.get().getOriginalAmount()));
        assertEquals(cp1.toString(), orderInDatabase.get().getCurrencyPair());
        assertEquals("MY_REF_3", orderInDatabase.get().getUserReference());
        assertEquals(createZonedDateTime("01-01-2020"), orderInDatabase.get().getTimestamp());
        assertEquals(NEW, orderInDatabase.get().getStatus());
        assertEquals(0, new BigDecimal("1.00002").compareTo(orderInDatabase.get().getCumulativeAmount()));
        assertEquals(0, new BigDecimal("1.00003").compareTo(orderInDatabase.get().getAveragePrice()));
        assertEquals(0, new BigDecimal("1.00004").compareTo(orderInDatabase.get().getFee()));
        assertEquals("leverage3", orderInDatabase.get().getLeverage());
        assertEquals(0, new BigDecimal("1.00005").compareTo(orderInDatabase.get().getLimitPrice()));
        // Tests for created on and updated on fields.
        ZonedDateTime createdOn = orderInDatabase.get().getCreatedOn();
        assertNotNull(createdOn);
        assertNull(orderInDatabase.get().getUpdatedOn());

        // =============================================================================================================
        // OrderDTO - Check created order (dto).
        OrderDTO order = strategy.getOrders().get("BACKUP_ORDER_03");
        assertNotNull(order);
        assertEquals("BACKUP_ORDER_03", order.getId());
        assertEquals(ASK, order.getType());
        assertEquals(0, new BigDecimal("1.00001").compareTo(order.getOriginalAmount()));
        assertEquals(cp1, order.getCurrencyPair());
        assertEquals("MY_REF_3", order.getUserReference());
        assertEquals(createZonedDateTime("01-01-2020"), order.getTimestamp());
        assertEquals(NEW, order.getStatus());
        assertEquals(0, new BigDecimal("1.00002").compareTo(order.getCumulativeAmount()));
        assertEquals(0, new BigDecimal("1.00003").compareTo(order.getAveragePrice()));
        assertEquals(0, new BigDecimal("1.00004").compareTo(order.getFee()));
        assertEquals("leverage3", order.getLeverage());
        assertEquals(0, new BigDecimal("1.00005").compareTo(order.getLimitPrice()));

        // =============================================================================================================
        // Updating the order - first time.
        orderFlux.emitValue(OrderDTO.builder()
                .id("BACKUP_ORDER_03")
                .type(ASK)
                .originalAmount(new BigDecimal("1.00002"))
                .currencyPair(cp1)
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("1.00002"))
                .averagePrice(new BigDecimal("1.00003"))
                .fee(new BigDecimal("1.00004"))
                .leverage("leverage3")
                .limitPrice(new BigDecimal("1.00005"))
                .create());
        await().untilAsserted(() -> assertNotNull(orderRepository.findById("BACKUP_ORDER_03").get().getUpdatedOn()));
        assertEquals(createdOn, orderRepository.findById("BACKUP_ORDER_03").get().getCreatedOn());
        ZonedDateTime updatedOn = orderInDatabase.get().getCreatedOn();

        // =============================================================================================================
        // Updating the order - second time.
        orderFlux.emitValue(OrderDTO.builder()
                .id("BACKUP_ORDER_03")
                .type(ASK)
                .originalAmount(new BigDecimal("1.00003"))
                .currencyPair(cp1)
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .status(NEW)
                .cumulativeAmount(new BigDecimal("1.00002"))
                .averagePrice(new BigDecimal("1.00003"))
                .fee(new BigDecimal("1.00004"))
                .leverage("leverage3")
                .limitPrice(new BigDecimal("1.00005"))
                .create());
        await().untilAsserted(() -> assertTrue(updatedOn.isBefore(orderRepository.findById("BACKUP_ORDER_03").get().getUpdatedOn())));
        assertEquals(createdOn, orderRepository.findById("BACKUP_ORDER_03").get().getCreatedOn());
    }

}
