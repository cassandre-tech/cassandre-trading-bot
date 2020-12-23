package tech.cassandre.trading.bot.test.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;

@SpringBootTest
@DisplayName("Repository - Order")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
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
        Order order = orders.next();
        assertEquals("BACKUP_ORDER_01", order.getId());
        assertEquals(ASK, order.getType());
        assertEquals(0, new BigDecimal("0.000005").compareTo(order.getAmount()));
        assertEquals("ETH/BTC", order.getCurrencyPair());
        assertEquals("My reference 1", order.getUserReference());
        assertEquals(createZonedDateTime("18-11-2020"), order.getTimestamp());
        assertEquals(NEW, order.getStatus());
        assertEquals(0, new BigDecimal("0.000004").compareTo(order.getCumulativeAmount()));
        assertEquals(0, new BigDecimal("0.000003").compareTo(order.getAveragePrice()));
        assertEquals(0, new BigDecimal("0.000002").compareTo(order.getFee()));
        assertEquals("LEVERAGE_1", order.getLeverage());
        assertEquals(0, new BigDecimal("0.000001").compareTo(order.getLimitPrice()));
        assertNotNull(order.getStrategy());
        assertEquals("001", order.getStrategy().getId());
        // Order 2.
        order = orders.next();
        assertEquals("BACKUP_ORDER_02", order.getId());
        assertEquals(BID, order.getType());
        assertEquals(0, new BigDecimal("0.000015").compareTo(order.getAmount()));
        assertEquals("USDT/BTC", order.getCurrencyPair());
        assertEquals("My reference 2", order.getUserReference());
        assertEquals(createZonedDateTime("19-11-2020"), order.getTimestamp());
        assertEquals(PENDING_NEW, order.getStatus());
        assertEquals(0, new BigDecimal("0.000014").compareTo(order.getCumulativeAmount()));
        assertEquals(0, new BigDecimal("0.000013").compareTo(order.getAveragePrice()));
        assertEquals(0, new BigDecimal("0.000012").compareTo(order.getFee()));
        assertEquals("LEVERAGE_2", order.getLeverage());
        assertEquals(0, new BigDecimal("0.000011").compareTo(order.getLimitPrice()));
        assertNotNull(order.getStrategy());
        assertEquals("001", order.getStrategy().getId());
    }

}
