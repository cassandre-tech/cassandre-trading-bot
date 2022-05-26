package tech.cassandre.trading.bot.test.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderStatusDTO.PENDING_NEW;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.KCS;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Domain - Order")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true"),
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/test/core/backup.yaml"),
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class OrderTest extends BaseTest {

    @Autowired
    private OrderFlux orderFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check load order from database")
    public void checkLoadOrderFromDatabase() {
        // =============================================================================================================
        // Check order 1.
        Optional<OrderDTO> order1 = strategy.getOrderByOrderId("BACKUP_ORDER_01");
        assertTrue(order1.isPresent());
        assertEquals(1, order1.get().getUid());
        assertEquals("BACKUP_ORDER_01", order1.get().getOrderId());
        assertEquals(ASK, order1.get().getType());
        assertNotNull(order1.get().getStrategy());
        assertEquals(1, order1.get().getStrategy().getUid());
        assertEquals("01", order1.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, order1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("0.000005").compareTo(order1.get().getAmount().getValue()));
        assertEquals(ETH, order1.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("0.000003").compareTo(order1.get().getAveragePrice().getValue()));
        assertEquals(BTC, order1.get().getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("0.000001").compareTo(order1.get().getLimitPrice().getValue()));
        assertEquals(BTC, order1.get().getLimitPrice().getCurrency());
        assertEquals(0, new BigDecimal("0.000033").compareTo(order1.get().getMarketPrice().getValue()));
        assertEquals(KCS, order1.get().getMarketPrice().getCurrency());
        assertEquals("LEVERAGE_1", order1.get().getLeverage());
        assertEquals(NEW, order1.get().getStatus());
        assertEquals(0, new BigDecimal("0.000004").compareTo(order1.get().getCumulativeAmount().getValue()));
        assertEquals(ETH, order1.get().getCumulativeAmount().getCurrency());
        assertEquals("My reference 1", order1.get().getUserReference());
        assertTrue(createZonedDateTime("18-11-2020").isEqual(order1.get().getTimestamp()));
        assertEquals(0, order1.get().getTrades().size());

        // Test equals.
        Optional<OrderDTO> order1Bis = strategy.getOrderByOrderId("BACKUP_ORDER_01");
        assertTrue(order1Bis.isPresent());
        assertEquals(order1.get(), order1Bis.get());

        // =============================================================================================================
        // Check order 2.
        Optional<OrderDTO> order2 = strategy.getOrderByOrderId("BACKUP_ORDER_02");
        assertTrue(order2.isPresent());
        assertEquals(2, order2.get().getUid());
        assertEquals("BACKUP_ORDER_02", order2.get().getOrderId());
        assertEquals(BID, order2.get().getType());
        assertNotNull(order2.get().getStrategy());
        assertEquals(1, order2.get().getStrategy().getUid());
        assertEquals("01", order2.get().getStrategy().getStrategyId());
        assertEquals(new CurrencyPairDTO("USDT/BTC"), order2.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("0.000015").compareTo(order2.get().getAmount().getValue()));
        assertEquals(USDT, order2.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("0.000013").compareTo(order2.get().getAveragePrice().getValue()));
        assertEquals(BTC, order2.get().getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("0.000011").compareTo(order2.get().getLimitPrice().getValue()));
        assertEquals(BTC, order2.get().getLimitPrice().getCurrency());
        assertEquals("LEVERAGE_2", order2.get().getLeverage());
        assertEquals(PENDING_NEW, order2.get().getStatus());
        assertEquals(0, new BigDecimal("0.000014").compareTo(order2.get().getCumulativeAmount().getValue()));
        assertEquals(USDT, order2.get().getCumulativeAmount().getCurrency());
        assertEquals("My reference 2", order2.get().getUserReference());
        assertTrue(createZonedDateTime("19-11-2020").isEqual(order2.get().getTimestamp()));
        assertEquals(0, order2.get().getTrades().size());

        // =============================================================================================================
        // Check order's trades.
        Optional<OrderDTO> order3 = strategy.getOrderByOrderId("BACKUP_CLOSING_ORDER_03");
        assertTrue(order3.isPresent());
        assertEquals(3, order3.get().getTrades().size());
        Iterator<TradeDTO> order3Trades = order3.get().getTrades().iterator();
        assertEquals("BACKUP_TRADE_08", order3Trades.next().getTradeId());
        assertEquals("BACKUP_TRADE_09", order3Trades.next().getTradeId());
        assertEquals("BACKUP_TRADE_10", order3Trades.next().getTradeId());

        Optional<OrderDTO> order4 = strategy.getOrderByOrderId("BACKUP_OPENING_ORDER_05");
        assertTrue(order4.isPresent());
        assertEquals(2, order4.get().getTrades().size());
        Iterator<TradeDTO> order4Trades = order4.get().getTrades().iterator();
        assertEquals("BACKUP_TRADE_06", order4Trades.next().getTradeId());
        assertEquals("BACKUP_TRADE_07", order4Trades.next().getTradeId());
    }

    @Test
    @DisplayName("Check save order in database")
    public void checkSaveOrderInDatabase() {
        // =============================================================================================================
        // Emit an order to be saved in database.
        long orderCount = orderRepository.count();
        OrderDTO order01 = OrderDTO.builder()
                .orderId("BACKUP_ORDER_03")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1.00001", ETH_BTC.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("1.00003", ETH_BTC.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("1.00005", ETH_BTC.getQuoteCurrency()))
                .marketPrice(new CurrencyAmountDTO("1.00006", ETH_BTC.getBaseCurrency()))
                .leverage("leverage3")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("1.00002", ETH_BTC.getBaseCurrency()))
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .build();
        orderFlux.emitValue(order01);
        await().untilAsserted(() -> assertEquals(orderCount + 1, orderRepository.count()));

        // =============================================================================================================
        // Order - Check the order created previously (domain).
        final Optional<Order> orderInDatabase = orderRepository.findByOrderId("BACKUP_ORDER_03");
        assertTrue(orderInDatabase.isPresent());
        assertEquals(11, orderInDatabase.get().getUid());
        assertEquals("BACKUP_ORDER_03", orderInDatabase.get().getOrderId());
        assertEquals(ASK, orderInDatabase.get().getType());
        assertEquals(1, orderInDatabase.get().getStrategy().getUid());
        assertEquals("01", orderInDatabase.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC.toString(), orderInDatabase.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("1.00001").compareTo(orderInDatabase.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency().toString(), orderInDatabase.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("1.00003").compareTo(orderInDatabase.get().getAveragePrice().getValue()));
        assertEquals(ETH_BTC.getQuoteCurrency().toString(), orderInDatabase.get().getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("1.00005").compareTo(orderInDatabase.get().getLimitPrice().getValue()));
        assertEquals(ETH_BTC.getQuoteCurrency().toString(), orderInDatabase.get().getLimitPrice().getCurrency());
        assertEquals(0, new BigDecimal("1.00006").compareTo(orderInDatabase.get().getMarketPrice().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency().toString(), orderInDatabase.get().getMarketPrice().getCurrency());
        assertEquals("leverage3", orderInDatabase.get().getLeverage());
        assertEquals(NEW, orderInDatabase.get().getStatus());
        assertEquals(0, new BigDecimal("1.00002").compareTo(orderInDatabase.get().getCumulativeAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency().toString(), orderInDatabase.get().getCumulativeAmount().getCurrency());
        assertEquals("MY_REF_3", orderInDatabase.get().getUserReference());
        assertTrue(createZonedDateTime("01-01-2020").isEqual(orderInDatabase.get().getTimestamp()));
        ZonedDateTime createdOn = orderInDatabase.get().getCreatedOn();
        assertNotNull(createdOn);
        assertNull(orderInDatabase.get().getUpdatedOn());

        // =============================================================================================================
        // Order - Check the order created previously (dto).
        Optional<OrderDTO> order = this.strategy.getOrderByOrderId("BACKUP_ORDER_03");
        assertTrue(order.isPresent());
        assertEquals(11, order.get().getUid());
        assertEquals("BACKUP_ORDER_03", order.get().getOrderId());
        assertEquals(ASK, order.get().getType());
        assertEquals(1, order.get().getStrategy().getUid());
        assertEquals("01", order.get().getStrategy().getStrategyId());
        assertEquals(ETH_BTC, order.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("1.00001").compareTo(order.get().getAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), order.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("1.00003").compareTo(order.get().getAveragePrice().getValue()));
        assertEquals(ETH_BTC.getQuoteCurrency(), order.get().getAveragePrice().getCurrency());
        assertEquals(0, new BigDecimal("1.00005").compareTo(order.get().getLimitPrice().getValue()));
        assertEquals(ETH_BTC.getQuoteCurrency(), order.get().getLimitPrice().getCurrency());
        assertEquals(0, new BigDecimal("1.00006").compareTo(order.get().getMarketPrice().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), order.get().getMarketPrice().getCurrency());
        assertEquals("leverage3", order.get().getLeverage());
        assertEquals(NEW, order.get().getStatus());
        assertEquals(0, new BigDecimal("1.00002").compareTo(order.get().getCumulativeAmount().getValue()));
        assertEquals(ETH_BTC.getBaseCurrency(), order.get().getCumulativeAmount().getCurrency());
        assertEquals("MY_REF_3", order.get().getUserReference());
        assertTrue(createZonedDateTime("01-01-2020").isEqual(order.get().getTimestamp()));

        // =============================================================================================================
        // Updating the order and also adding a trade - first time.
        orderFlux.emitValue(OrderDTO.builder()
                .orderId("BACKUP_ORDER_03")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1.00002", ETH_BTC.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("1.00003", ETH_BTC.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("1.00005", ETH_BTC.getQuoteCurrency()))
                .leverage("leverage3")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("1.00002", ETH_BTC.getBaseCurrency()))
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .build());
        await().untilAsserted(() -> assertNotNull(getOrder("BACKUP_ORDER_03").getUpdatedOn()));
        assertEquals(createdOn, getOrder("BACKUP_ORDER_03").getCreatedOn());
        ZonedDateTime updatedOn = orderInDatabase.get().getCreatedOn();

        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("BACKUP_TRADE_11")
                .type(BID)
                .orderId("BACKUP_ORDER_03")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1.100001", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()))
                .fee(new CurrencyAmountDTO(new BigDecimal("3.300003"), BTC))
                .timestamp(createZonedDateTime("01-09-2020"))
                .userReference("TRADE MY_REF_3")
                .build());
        await().untilAsserted(() -> assertEquals(1, strategy.getTradesUpdatesReceived().size()));
        Optional<Order> backupOrder03 = orderRepository.findByOrderId("BACKUP_ORDER_03");
        assertTrue(backupOrder03.isPresent());
        assertEquals(1, backupOrder03.get().getTrades().size());
        Iterator<Trade> orderTrades = backupOrder03.get().getTrades().iterator();
        assertEquals("BACKUP_TRADE_11", orderTrades.next().getTradeId());

        // =============================================================================================================
        // Updating the order - second time.
        orderFlux.emitValue(OrderDTO.builder()
                .orderId("BACKUP_ORDER_03")
                .type(ASK)
                .strategy(strategyDTO)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1.00003", ETH_BTC.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("1.00003", ETH_BTC.getQuoteCurrency()))
                .limitPrice(new CurrencyAmountDTO("1.00005", ETH_BTC.getQuoteCurrency()))
                .leverage("leverage3")
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("1.00002", ETH_BTC.getBaseCurrency()))
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .build());
        await().untilAsserted(() -> assertTrue(updatedOn.isBefore(getOrder("BACKUP_ORDER_03").getUpdatedOn())));
        assertEquals(createdOn, getOrder("BACKUP_ORDER_03").getCreatedOn());

        // We check if we still have the strategy set.
        final Optional<OrderDTO> optionalOrder = strategy.getOrderByOrderId("BACKUP_ORDER_03");
        assertTrue(optionalOrder.isPresent());
        assertNotNull(optionalOrder.get().getStrategy());
        assertEquals("01", optionalOrder.get().getStrategy().getStrategyId());

        // We check if we still have the trade linked to the order.
        backupOrder03 = orderRepository.findByOrderId("BACKUP_ORDER_03");
        assertTrue(backupOrder03.isPresent());
        assertEquals(1, backupOrder03.get().getTrades().size());
        orderTrades = backupOrder03.get().getTrades().iterator();
        assertEquals("BACKUP_TRADE_11", orderTrades.next().getTradeId());
    }

    /**
     * Retrieve order from database.
     *
     * @param id order id
     * @return order
     */
    public Order getOrder(final String id) {
        final Optional<Order> order = orderRepository.findByOrderId(id);
        if (order.isPresent()) {
            return order.get();
        } else {
            fail("Order not found : " + id);
            return null;
        }
    }

}
