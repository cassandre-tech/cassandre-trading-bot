package tech.cassandre.trading.bot.test.core.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

@SpringBootTest
@DisplayName("Repository - Order")
@Configuration({
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/test/core/backup.yaml")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class OrderRepositoryTest extends BaseTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("Check imported data")
    public void checkImportedOrders() {
        // Orders.
        final Iterator<Order> orders = orderRepository.findByOrderByTimestampAsc().iterator();
        assertEquals(10, orderRepository.count());

        // Order 1.
        Order order1 = orders.next();
        assertEquals(1, order1.getUid());
        assertEquals("BACKUP_ORDER_01", order1.getOrderId());
        assertEquals(ASK, order1.getType());
        assertNotNull(order1.getStrategy());
        assertEquals(1, order1.getStrategy().getUid());
        assertEquals("01", order1.getStrategy().getStrategyId());
        assertEquals("ETH/BTC", order1.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.000005").compareTo(order1.getAmount().getValue()));
        assertEquals("ETH", order1.getAmount().getCurrency());
        assertEquals(0, new BigDecimal("0.000003").compareTo(order1.getAveragePrice().getValue()));
        assertEquals("BTC", order1.getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("0.000001").compareTo(order1.getLimitPrice().getValue()));
        assertEquals("BTC", order1.getLimitPrice().getCurrency());
        assertEquals(0, new BigDecimal("0.000033").compareTo(order1.getMarketPrice().getValue()));
        assertEquals("KCS", order1.getMarketPrice().getCurrency());
        assertEquals("LEVERAGE_1", order1.getLeverage());
        assertEquals(NEW, order1.getStatus());
        assertEquals(0, new BigDecimal("0.000004").compareTo(order1.getCumulativeAmount().getValue()));
        assertEquals("My reference 1", order1.getUserReference());
        assertTrue(createZonedDateTime("18-11-2020").isEqual(order1.getTimestamp()));

        // Retrieving order 1 with findByOrderId().
        Optional<Order> order1Bis = orderRepository.findByOrderId("BACKUP_ORDER_01");
        assertTrue(order1Bis.isPresent());
        assertEquals(order1, order1Bis.get());

        // Order 2.
        Order order2 = orders.next();
        assertEquals(2, order2.getUid());
        assertEquals("BACKUP_ORDER_02", order2.getOrderId());
        assertEquals(BID, order2.getType());
        assertNotNull(order2.getStrategy());
        assertEquals(1, order2.getStrategy().getUid());
        assertEquals("01", order2.getStrategy().getStrategyId());
        assertEquals("USDT/BTC", order2.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.000015").compareTo(order2.getAmount().getValue()));
        assertEquals("USDT", order2.getAmount().getCurrency());
        assertEquals(0, new BigDecimal("0.000013").compareTo(order2.getAveragePrice().getValue()));
        assertEquals("BTC", order2.getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("0.000011").compareTo(order2.getLimitPrice().getValue()));
        assertEquals("BTC", order2.getLimitPrice().getCurrency());
        assertEquals("LEVERAGE_2", order2.getLeverage());
        assertEquals(PENDING_NEW, order2.getStatus());
        assertEquals(0, new BigDecimal("0.000014").compareTo(order2.getCumulativeAmount().getValue()));
        assertEquals("My reference 2", order2.getUserReference());
        assertTrue(createZonedDateTime("19-11-2020").isEqual(order2.getTimestamp()));

        // Retrieving order 2 with findByOrderId().
        Optional<Order> order2Bis = orderRepository.findByOrderId("BACKUP_ORDER_02");
        assertTrue(order2Bis.isPresent());
        assertEquals(order2, order2Bis.get());
    }

}
