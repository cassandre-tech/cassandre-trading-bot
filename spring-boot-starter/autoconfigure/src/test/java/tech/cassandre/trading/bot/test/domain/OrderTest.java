package tech.cassandre.trading.bot.test.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.StrategyRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.KCS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

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
    private StrategyRepository strategyRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderFlux orderFlux;

    @Autowired
    private TradeFlux tradeFlux;

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
        assertEquals(0, new BigDecimal("0.000005").compareTo(order.getAmount().getValue()));
        assertEquals(ETH, order.getAmount().getCurrency());
        assertEquals(new CurrencyPairDTO("ETH/BTC"), order.getCurrencyPair());
        assertEquals("My reference 1", order.getUserReference());
        assertEquals(createZonedDateTime("18-11-2020"), order.getTimestamp());
        assertEquals(NEW, order.getStatus());
        assertEquals(0, new BigDecimal("0.000004").compareTo(order.getCumulativeAmount().getValue()));
        assertEquals(0, new BigDecimal("0.000003").compareTo(order.getAveragePrice().getValue()));
        assertEquals(0, new BigDecimal("0.000002").compareTo(order.getFee().getValue()));
        assertEquals(KCS, order.getFee().getCurrency());
        assertEquals("LEVERAGE_1", order.getLeverage());
        assertEquals(0, new BigDecimal("0.000001").compareTo(order.getLimitPrice().getValue()));
        assertEquals(BTC, order.getLimitPrice().getCurrency());
        assertNotNull(order.getStrategy());
        assertEquals("001", order.getStrategy().getId());
        assertEquals(0, order.getTrades().size());

        // =============================================================================================================
        // Check order 2.
        order = strategy.getOrders().get("BACKUP_ORDER_02");
        assertNotNull(order);
        assertEquals("BACKUP_ORDER_02", order.getId());
        assertEquals(BID, order.getType());
        assertEquals(0, new BigDecimal("0.000015").compareTo(order.getAmount().getValue()));
        assertEquals(USDT, order.getAmount().getCurrency());
        assertEquals(new CurrencyPairDTO("USDT/BTC"), order.getCurrencyPair());
        assertEquals("My reference 2", order.getUserReference());
        assertEquals(createZonedDateTime("19-11-2020"), order.getTimestamp());
        assertEquals(PENDING_NEW, order.getStatus());
        assertEquals(0, new BigDecimal("0.000014").compareTo(order.getCumulativeAmount().getValue()));
        assertEquals(0, new BigDecimal("0.000013").compareTo(order.getAveragePrice().getValue()));
        assertEquals(0, new BigDecimal("0.000012").compareTo(order.getFee().getValue()));
        assertEquals(KCS, order.getFee().getCurrency());
        assertEquals("LEVERAGE_2", order.getLeverage());
        assertEquals(0, new BigDecimal("0.000011").compareTo(order.getLimitPrice().getValue()));
        assertEquals(BTC, order.getLimitPrice().getCurrency());
        assertNotNull(order.getStrategy());
        assertEquals("001", order.getStrategy().getId());
        assertEquals(0, order.getTrades().size());

        // Check trades of orders.
        order = strategy.getOrders().get("BACKUP_OPENING_ORDER_05");
        assertNotNull(order);
        assertEquals(2, order.getTrades().size());
        order = strategy.getOrders().get("BACKUP_CLOSING_ORDER_03");
        assertNotNull(order);
        assertEquals(3, order.getTrades().size());
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
        // Loading strategy.
        StrategyDTO strategyDTO = StrategyDTO.builder().id("1").build();
        StrategyDTO wrongStrategyDTO = StrategyDTO.builder().id("2").build();

        // =============================================================================================================
        // Add an order and check that it's correctly saved in database.
        long orderCount = orderRepository.count();
        OrderDTO order01 = OrderDTO.builder()
                .id("BACKUP_ORDER_03")
                .type(ASK)
                .amount(new CurrencyAmountDTO("1.00001", cp1.getBaseCurrency()))
                .currencyPair(cp1)
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("1.00002", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("1.00003", cp1.getQuoteCurrency()))
                .fee(new CurrencyAmountDTO("1.00004", KCS))
                .leverage("leverage3")
                .limitPrice(new CurrencyAmountDTO("1.00005", cp1.getQuoteCurrency()))
                .strategy(strategyDTO)
                .build();
        orderFlux.emitValue(order01);
        await().untilAsserted(() -> assertEquals(orderCount + 1, orderRepository.count()));

        // =============================================================================================================
        // Order - Check created order (domain).
        final Optional<Order> orderInDatabase = orderRepository.findById("BACKUP_ORDER_03");
        assertTrue(orderInDatabase.isPresent());
        assertEquals("BACKUP_ORDER_03", orderInDatabase.get().getId());
        assertEquals(ASK, orderInDatabase.get().getType());
        assertEquals(0, new BigDecimal("1.00001").compareTo(orderInDatabase.get().getAmount()));
        assertEquals(cp1.toString(), orderInDatabase.get().getCurrencyPair());
        assertEquals("MY_REF_3", orderInDatabase.get().getUserReference());
        assertEquals(createZonedDateTime("01-01-2020"), orderInDatabase.get().getTimestamp());
        assertEquals(NEW, orderInDatabase.get().getStatus());
        assertEquals(0, new BigDecimal("1.00002").compareTo(orderInDatabase.get().getCumulativeAmount()));
        assertEquals(0, new BigDecimal("1.00003").compareTo(orderInDatabase.get().getAveragePrice()));
        assertEquals(0, new BigDecimal("1.00004").compareTo(orderInDatabase.get().getFee()));
        assertEquals("leverage3", orderInDatabase.get().getLeverage());
        assertEquals(0, new BigDecimal("1.00005").compareTo(orderInDatabase.get().getLimitPrice()));
        assertNotNull(orderInDatabase.get().getStrategy());
        assertEquals("1", orderInDatabase.get().getStrategy().getId());
        // Tests for created on and updated on fields.
        ZonedDateTime createdOn = orderInDatabase.get().getCreatedOn();
        assertNotNull(createdOn);
        // TODO Strange behavior - An update is made just after insert - So the update field is set.
        // It seems it comes from Updated field and it's ZonedDateTime value.
        assertNotNull(orderInDatabase.get().getUpdatedOn());

        // =============================================================================================================
        // OrderDTO - Check created order (dto).
        OrderDTO order = this.strategy.getOrders().get("BACKUP_ORDER_03");
        assertNotNull(order);
        assertEquals("BACKUP_ORDER_03", order.getId());
        assertEquals(ASK, order.getType());
        assertEquals(0, new BigDecimal("1.00001").compareTo(order.getAmount().getValue()));
        assertEquals(cp1.getBaseCurrency(), order.getAmount().getCurrency());
        assertEquals(cp1, order.getCurrencyPair());
        assertEquals("MY_REF_3", order.getUserReference());
        assertEquals(createZonedDateTime("01-01-2020"), order.getTimestamp());
        assertEquals(NEW, order.getStatus());
        assertEquals(0, new BigDecimal("1.00002").compareTo(order.getCumulativeAmount().getValue()));
        assertEquals(0, new BigDecimal("1.00003").compareTo(order.getAveragePrice().getValue()));
        assertEquals(0, new BigDecimal("1.00004").compareTo(order.getFee().getValue()));
        assertEquals(KCS, order.getFee().getCurrency());
        assertEquals("leverage3", order.getLeverage());
        assertEquals(0, new BigDecimal("1.00005").compareTo(order.getLimitPrice().getValue()));
        assertEquals(cp1.getQuoteCurrency(), order.getLimitPrice().getCurrency());
        assertNotNull(order.getStrategy());
        assertEquals("1", order.getStrategy().getId());

        // =============================================================================================================
        // Updating the order and adding a trade - first time.
        orderFlux.emitValue(OrderDTO.builder()
                .id("BACKUP_ORDER_03")
                .type(ASK)
                .amount(new CurrencyAmountDTO("1.00002", cp1.getBaseCurrency()))
                .currencyPair(cp1)
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("1.00002", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("1.00003", cp1.getQuoteCurrency()))
                .fee(new CurrencyAmountDTO("1.00004", KCS))
                .leverage("leverage3")
                .limitPrice(new CurrencyAmountDTO("1.00005", cp1.getQuoteCurrency()))
                .strategy(wrongStrategyDTO)
                .build());
        await().untilAsserted(() -> assertNotNull(getOrder("BACKUP_ORDER_03").getUpdatedOn()));
        assertEquals(createdOn, getOrder("BACKUP_ORDER_03").getCreatedOn());
        ZonedDateTime updatedOn = orderInDatabase.get().getCreatedOn();
        tradeFlux.emitValue(TradeDTO.builder()
                .id("BACKUP_TRADE_11")
                .orderId("BACKUP_ORDER_03")
                .type(BID)
                .amount(new CurrencyAmountDTO("1.100001", cp1.getBaseCurrency()))
                .currencyPair(cp1)
                .price(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()))
                .timestamp(createZonedDateTime("01-09-2020"))
                .fee(new CurrencyAmountDTO(new BigDecimal("3.300003"), BTC))
                .build());
        await().untilAsserted(() -> assertEquals(1, strategy.getTradesUpdateReceived().size()));
        Optional<Order> backupOrder03 = orderRepository.findById("BACKUP_ORDER_03");
        assertTrue(backupOrder03.isPresent());
        assertEquals(1, backupOrder03.get().getTrades().size());

        // =============================================================================================================
        // Updating the order - second time.
        orderFlux.emitValue(OrderDTO.builder()
                .id("BACKUP_ORDER_03")
                .type(ASK)
                .amount(new CurrencyAmountDTO("1.00003", cp1.getBaseCurrency()))
                .currencyPair(cp1)
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("1.00002", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("1.00003", cp1.getQuoteCurrency()))
                .fee(new CurrencyAmountDTO("1.00004", KCS))
                .leverage("leverage3")
                .limitPrice(new CurrencyAmountDTO("1.00005", cp1.getQuoteCurrency()))
                .strategy(wrongStrategyDTO)
                .build());
        await().untilAsserted(() -> assertTrue(updatedOn.isBefore(getOrder("BACKUP_ORDER_03").getUpdatedOn())));
        assertEquals(createdOn, getOrder("BACKUP_ORDER_03").getCreatedOn());

        // We check if we still have the strategy set.
        final Optional<OrderDTO> optionalOrder = strategy.getOrderById("BACKUP_ORDER_03");
        assertTrue(optionalOrder.isPresent());
        assertNotNull(optionalOrder.get().getStrategy());
        assertEquals("1", optionalOrder.get().getStrategy().getId());

        // We check if we still have the trade.
        backupOrder03 = orderRepository.findById("BACKUP_ORDER_03");
        assertTrue(backupOrder03.isPresent());
        assertEquals(1, backupOrder03.get().getTrades().size());
    }

    /**
     * Retrieve order from database.
     * @param id order id
     * @return order
     */
    public Order getOrder(final String id) {
        final Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return order.get();
        } else {
            throw new RuntimeException("Order not found : " + id);
        }
    }

}
