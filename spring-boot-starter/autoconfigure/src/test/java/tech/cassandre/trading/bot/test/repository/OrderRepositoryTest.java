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
        Order o = orders.next();
        assertEquals("BACKUP_ORDER_01", o.getId());
        assertEquals(ASK, o.getType());
        assertEquals(0, new BigDecimal("0.000005").compareTo(o.getOriginalAmount()));
        assertEquals("ETH/BTC", o.getCurrencyPair());
        assertEquals("My reference 1", o.getUserReference());
        assertEquals(createZonedDateTime("18-11-2020"), o.getTimestamp());
        assertEquals(NEW, o.getStatus());
        assertEquals(0, new BigDecimal("0.000004").compareTo(o.getCumulativeAmount()));
        assertEquals(0, new BigDecimal("0.000003").compareTo(o.getAveragePrice()));
        assertEquals(0, new BigDecimal("0.000002").compareTo(o.getFee()));
        assertEquals("LEVERAGE_1", o.getLeverage());
        assertEquals(0, new BigDecimal("0.000001").compareTo(o.getLimitPrice()));
        // Order 2.
        o = orders.next();
        assertEquals("BACKUP_ORDER_02", o.getId());
        assertEquals(BID, o.getType());
        assertEquals(0, new BigDecimal("0.000015").compareTo(o.getOriginalAmount()));
        assertEquals("USDT/BTC", o.getCurrencyPair());
        assertEquals("My reference 2", o.getUserReference());
        assertEquals(createZonedDateTime("19-11-2020"), o.getTimestamp());
        assertEquals(PENDING_NEW, o.getStatus());
        assertEquals(0, new BigDecimal("0.000014").compareTo(o.getCumulativeAmount()));
        assertEquals(0, new BigDecimal("0.000013").compareTo(o.getAveragePrice()));
        assertEquals(0, new BigDecimal("0.000012").compareTo(o.getFee()));
        assertEquals("LEVERAGE_2", o.getLeverage());
        assertEquals(0, new BigDecimal("0.000011").compareTo(o.getLimitPrice()));
    }

}
