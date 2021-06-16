package tech.cassandre.trading.bot.test.repository;

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
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/backup.yaml")
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
        Order o = orders.next();
        assertEquals(1, o.getId());
        assertEquals("BACKUP_ORDER_01", o.getOrderId());
        assertEquals(ASK, o.getType());
        assertNotNull(o.getStrategy());
        assertEquals(1, o.getStrategy().getId());
        assertEquals("01", o.getStrategy().getStrategyId());
        assertEquals("ETH/BTC", o.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.000005").compareTo(o.getAmount().getValue()));
        assertEquals("ETH", o.getAmount().getCurrency());
        assertEquals(0, new BigDecimal("0.000003").compareTo(o.getAveragePrice().getValue()));
        assertEquals("BTC", o.getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("0.000001").compareTo(o.getLimitPrice().getValue()));
        assertEquals("BTC", o.getLimitPrice().getCurrency());
        assertEquals(0, new BigDecimal("0.000033").compareTo(o.getMarketPrice().getValue()));
        assertEquals("KCS", o.getMarketPrice().getCurrency());
        assertEquals("LEVERAGE_1", o.getLeverage());
        assertEquals(NEW, o.getStatus());
        assertEquals(0, new BigDecimal("0.000004").compareTo(o.getCumulativeAmount().getValue()));
        assertEquals("My reference 1", o.getUserReference());
        assertEquals(createZonedDateTime("18-11-2020"), o.getTimestamp());

        // Retrieving order 1 with findByOrderId().
        Optional<Order> oBis = orderRepository.findByOrderId("BACKUP_ORDER_01");
        assertTrue(oBis.isPresent());
        assertEquals(o, oBis.get());

        // Order 2.
        o = orders.next();
        assertEquals(2, o.getId());
        assertEquals("BACKUP_ORDER_02", o.getOrderId());
        assertEquals(BID, o.getType());
        assertNotNull(o.getStrategy());
        assertEquals(1, o.getStrategy().getId());
        assertEquals("01", o.getStrategy().getStrategyId());
        assertEquals("USDT/BTC", o.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.000015").compareTo(o.getAmount().getValue()));
        assertEquals("USDT", o.getAmount().getCurrency());
        assertEquals(0, new BigDecimal("0.000013").compareTo(o.getAveragePrice().getValue()));
        assertEquals("BTC", o.getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("0.000011").compareTo(o.getLimitPrice().getValue()));
        assertEquals("BTC", o.getLimitPrice().getCurrency());
        assertEquals("LEVERAGE_2", o.getLeverage());
        assertEquals(PENDING_NEW, o.getStatus());
        assertEquals(0, new BigDecimal("0.000014").compareTo(o.getCumulativeAmount().getValue()));
        assertEquals("My reference 2", o.getUserReference());
        assertEquals(createZonedDateTime("19-11-2020"), o.getTimestamp());

        // Retrieving order 2 with findByOrderId().
        oBis = orderRepository.findByOrderId("BACKUP_ORDER_02");
        assertTrue(oBis.isPresent());
        assertEquals(o, oBis.get());
    }

}
