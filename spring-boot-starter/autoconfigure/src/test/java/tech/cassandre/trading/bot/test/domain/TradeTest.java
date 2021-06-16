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
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.StrategyRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
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
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.ASK;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SpringBootTest
@DisplayName("Domain - Trade")
@Configuration({
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/backup.yaml")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class TradeTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private StrategyRepository strategyRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TradeFlux tradeFlux;

    @Autowired
    private OrderFlux orderFlux;

    @Test
    @DisplayName("Check load trade from database")
    public void checkLoadTradeFromDatabase() {
        // =============================================================================================================
        // Check that positions, orders and trades in database doesn't trigger strategy events.
        assertTrue(strategy.getTradesUpdatesReceived().isEmpty());
        assertTrue(strategy.getOrdersUpdatesReceived().isEmpty());

        // =============================================================================================================
        // Check trade 01.
        Optional<TradeDTO> t = strategy.getTradeByTradeId("BACKUP_TRADE_01");
        assertTrue(t.isPresent());
        assertEquals(1, t.get().getId());
        assertEquals("BACKUP_TRADE_01", t.get().getTradeId());
        assertEquals(BID, t.get().getType());
        assertEquals("BACKUP_OPENING_ORDER_02", t.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(t.get().getAmount().getValue()));
        assertEquals(BTC, t.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("10").compareTo(t.get().getPrice().getValue()));
        assertEquals(USDT, t.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("1").compareTo(t.get().getFee().getValue()));
        assertEquals(USDT, t.get().getFee().getCurrency());
        assertEquals("Trade 01", t.get().getUserReference());
        assertEquals(createZonedDateTime("01-08-2020"), t.get().getTimestamp());

        // Test equals.
        Optional<TradeDTO> tBis = strategy.getTradeByTradeId("BACKUP_TRADE_01");
        assertTrue(tBis.isPresent());
        assertEquals(t.get(), tBis.get());

        // =============================================================================================================
        // Check trade 02.
        t = strategy.getTradeByTradeId("BACKUP_TRADE_02");
        assertTrue(t.isPresent());
        assertEquals(2, t.get().getId());
        assertEquals("BACKUP_TRADE_02", t.get().getTradeId());
        assertEquals(BID, t.get().getType());
        assertEquals("BACKUP_OPENING_ORDER_03", t.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(t.get().getAmount().getValue()));
        assertEquals(BTC, t.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("20").compareTo(t.get().getPrice().getValue()));
        assertEquals(USDT, t.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("2").compareTo(t.get().getFee().getValue()));
        assertEquals(USDT, t.get().getFee().getCurrency());
        assertEquals("Trade 02", t.get().getUserReference());
        assertEquals(createZonedDateTime("02-08-2020"), t.get().getTimestamp());

        // =============================================================================================================
        // Check trade 03.
        t = strategy.getTradeByTradeId("BACKUP_TRADE_03");
        assertTrue(t.isPresent());
        assertEquals(3, t.get().getId());
        assertEquals("BACKUP_TRADE_03", t.get().getTradeId());
        assertEquals(BID, t.get().getType());
        assertEquals("BACKUP_OPENING_ORDER_04", t.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(t.get().getAmount().getValue()));
        assertEquals(BTC, t.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("30").compareTo(t.get().getPrice().getValue()));
        assertEquals(USDT, t.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("3").compareTo(t.get().getFee().getValue()));
        assertEquals(USDT, t.get().getFee().getCurrency());
        assertEquals("Trade 03", t.get().getUserReference());
        assertEquals(createZonedDateTime("03-08-2020"), t.get().getTimestamp());

        // =============================================================================================================
        // Check trade 04.
        t = strategy.getTradeByTradeId("BACKUP_TRADE_04");
        assertTrue(t.isPresent());
        assertEquals(4, t.get().getId());
        assertEquals("BACKUP_TRADE_04", t.get().getTradeId());
        assertEquals(ASK, t.get().getType());
        assertEquals("BACKUP_CLOSING_ORDER_01", t.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), t.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(t.get().getAmount().getValue()));
        assertEquals(BTC, t.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("40").compareTo(t.get().getPrice().getValue()));
        assertEquals(USDT, t.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("4").compareTo(t.get().getFee().getValue()));
        assertEquals(USDT, t.get().getFee().getCurrency());
        assertEquals("Trade 04", t.get().getUserReference());
        assertEquals(createZonedDateTime("04-08-2020"), t.get().getTimestamp());

        // =============================================================================================================
        // Check trade 05.
        t = strategy.getTradeByTradeId("BACKUP_TRADE_05");
        assertTrue(t.isPresent());
        assertEquals(5, t.get().getId());
        assertEquals("BACKUP_TRADE_05", t.get().getTradeId());
        assertEquals(ASK, t.get().getType());
        assertEquals("BACKUP_CLOSING_ORDER_02", t.get().getOrderId());
        assertEquals(new CurrencyPairDTO(ETH, USD), t.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(t.get().getAmount().getValue()));
        assertEquals(ETH, t.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("40").compareTo(t.get().getPrice().getValue()));
        assertEquals(USD, t.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("5").compareTo(t.get().getFee().getValue()));
        assertEquals(USD, t.get().getFee().getCurrency());
        assertEquals("Trade 05", t.get().getUserReference());
        assertEquals(createZonedDateTime("05-08-2020"), t.get().getTimestamp());
    }

    @Test
    @DisplayName("Check save trade in database")
    public void checkSaveTradeInDatabase() {
        // =============================================================================================================
        // Check that positions, orders and trades in database doesn't trigger strategy events.
        assertEquals(1, strategy.getPositionsUpdatesReceived().size());
        assertTrue(strategy.getTradesUpdatesReceived().isEmpty());
        assertTrue(strategy.getOrdersUpdatesReceived().isEmpty());

        // =============================================================================================================
        // Loading strategy.
        final Optional<Strategy> optionalStrategy = strategyRepository.findByStrategyId("01");
        assertTrue(optionalStrategy.isPresent());

        // =============================================================================================================
        // Add a trade and check that it's correctly saved in database.
        TradeDTO t1 = TradeDTO.builder()
                .tradeId("BACKUP_TRADE_11")
                .orderId("BACKUP_ORDER_01")
                .type(BID)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1.100001", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()))
                .fee(new CurrencyAmountDTO(new BigDecimal("3.300003"), BTC))
                .userReference("My reference !")
                .timestamp(createZonedDateTime("01-09-2020"))
                .build();
        tradeFlux.emitValue(t1);
        await().untilAsserted(() -> assertEquals(1, strategy.getTradesUpdatesReceived().size()));

        // =============================================================================================================
        // Trade - Check created order (domain).
        Optional<Trade> tradeInDatabase = tradeRepository.findByTradeId("BACKUP_TRADE_11");
        assertTrue(tradeInDatabase.isPresent());
        assertEquals(11, tradeInDatabase.get().getId());
        assertEquals("BACKUP_TRADE_11", tradeInDatabase.get().getTradeId());
        assertEquals(BID, tradeInDatabase.get().getType());
        assertEquals("BACKUP_ORDER_01", tradeInDatabase.get().getOrder().getOrderId());
        assertEquals("ETH/BTC", tradeInDatabase.get().getCurrencyPair());
        assertEquals(0, tradeInDatabase.get().getAmount().getValue().compareTo(new BigDecimal("1.100001")));
        assertEquals("ETH", tradeInDatabase.get().getAmount().getCurrency());
        assertEquals(0, tradeInDatabase.get().getPrice().getValue().compareTo(new BigDecimal("2.200002")));
        assertEquals("BTC", tradeInDatabase.get().getPrice().getCurrency());
        assertEquals(0, tradeInDatabase.get().getFee().getValue().compareTo(new BigDecimal("3.300003")));
        assertEquals("BTC", tradeInDatabase.get().getFee().getCurrency());
        assertEquals("My reference !", tradeInDatabase.get().getUserReference());
        assertEquals(createZonedDateTime("01-09-2020"), tradeInDatabase.get().getTimestamp());

        // Tests for created on and updated on fields.
        ZonedDateTime createdOn = tradeInDatabase.get().getCreatedOn();
        assertNotNull(createdOn);
        assertNull(tradeInDatabase.get().getUpdatedOn());

        // =============================================================================================================
        // TradeDTO - Check created trade (dto).
        final TradeDTO tradeDTO = strategy.getTrades().get("BACKUP_TRADE_11");
        assertNotNull(tradeDTO);
        assertEquals(11, tradeDTO.getId());
        assertEquals("BACKUP_TRADE_11", tradeDTO.getTradeId());
        assertEquals(BID, tradeDTO.getType());
        assertEquals("BACKUP_ORDER_01", tradeDTO.getOrderId());
        assertEquals("BACKUP_ORDER_01", tradeDTO.getOrder().getOrderId());
        assertEquals(ETH_BTC, tradeDTO.getCurrencyPair());
        assertEquals(ETH_BTC.getBaseCurrency(), tradeDTO.getAmount().getCurrency());
        assertEquals(0, tradeDTO.getAmount().getValue().compareTo(new BigDecimal("1.100001")));
        assertEquals(ETH_BTC.getBaseCurrency(), tradeDTO.getAmount().getCurrency());
        assertEquals(0, tradeDTO.getPrice().getValue().compareTo(new BigDecimal("2.200002")));
        assertEquals(ETH_BTC.getQuoteCurrency(), tradeDTO.getPrice().getCurrency());
        assertEquals(0, tradeDTO.getFee().getValue().compareTo(new BigDecimal("3.300003")));
        assertEquals(BTC, tradeDTO.getFee().getCurrency());
        assertEquals("My reference !", tradeDTO.getUserReference());
        assertEquals(createZonedDateTime("01-09-2020"), tradeDTO.getTimestamp());

        // =============================================================================================================
        // Updating the trade - first time.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("BACKUP_TRADE_11")
                .type(BID)
                .orderId("BACKUP_ORDER_01")
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1.100002", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()))
                .fee(new CurrencyAmountDTO(new BigDecimal("3.300003"), BTC))
                .userReference("Updated reference")
                .timestamp(createZonedDateTime("01-09-2020"))
                .build());
        await().untilAsserted(() -> assertEquals(2, strategy.getTradesUpdatesReceived().size()));
        await().untilAsserted(() -> assertNotNull(tradeRepository.findByTradeId("BACKUP_TRADE_11").get().getUpdatedOn()));
        assertEquals(createdOn, tradeRepository.findByTradeId("BACKUP_TRADE_11").get().getCreatedOn());
        ZonedDateTime updatedOn = tradeInDatabase.get().getCreatedOn();

        // =============================================================================================================
        // Updating the order - second time.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("BACKUP_TRADE_11")
                .orderId("BACKUP_ORDER_01")
                .type(BID)
                .currencyPair(ETH_BTC)
                .amount(new CurrencyAmountDTO("1.100003", ETH_BTC.getBaseCurrency()))
                .price(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("01-09-2020"))
                .fee(new CurrencyAmountDTO(new BigDecimal("3.300003"), BTC))
                .build());
        await().untilAsserted(() -> assertTrue(updatedOn.isBefore(tradeRepository.findByTradeId("BACKUP_TRADE_11").get().getUpdatedOn())));
        assertEquals(createdOn, tradeRepository.findByTradeId("BACKUP_TRADE_11").get().getCreatedOn());
        // We check if we still have the strategy set.
        final Optional<TradeDTO> optionalTrade = strategy.getTradeByTradeId("BACKUP_TRADE_11");
        assertTrue(optionalTrade.isPresent());
    }

    @Test
    @DisplayName("Check link between order and trade")
    public void checkStrategyValueInTrade() {
        // =============================================================================================================
        // Loading strategy.
        StrategyDTO strategyDTO = StrategyDTO.builder().id(1L).strategyId("01").build();

        // =============================================================================================================
        // First, we have an order (NEW_ORDER) that arrives with a strategy.
        long orderCount = orderRepository.count();
        orderFlux.emitValue(OrderDTO.builder()
                .orderId("NEW_ORDER")
                .type(ASK)
                .amount(new CurrencyAmountDTO("1.00001", ETH_BTC.getBaseCurrency()))
                .currencyPair(ETH_BTC)
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("1.00002", ETH_BTC.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("1.00003", ETH_BTC.getQuoteCurrency()))
                .leverage("leverage3")
                .limitPrice(new CurrencyAmountDTO("1.00005", ETH_BTC.getQuoteCurrency()))
                .strategy(strategyDTO)
                .build());
        await().untilAsserted(() -> assertEquals(orderCount + 1, orderRepository.count()));

        // =============================================================================================================
        // Then a new trade arrives for order NEW_ORDER and we check that the strategy is set.
        long tradeCount = tradeRepository.count();
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("NEW_TRADE")
                .orderId("NEW_ORDER")
                .type(BID)
                .amount(new CurrencyAmountDTO("1.100003", ETH_BTC.getBaseCurrency()))
                .currencyPair(ETH_BTC)
                .price(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()))
                .timestamp(createZonedDateTime("01-09-2020"))
                .fee(new CurrencyAmountDTO(new BigDecimal("3.300003"), BTC))
                .build());
        await().untilAsserted(() -> assertEquals(tradeCount + 1, tradeRepository.count()));

        // Check link between order and trade.
        final Optional<Trade> t = tradeRepository.findByTradeId("NEW_TRADE");
        assertTrue(t.isPresent());
        final Optional<Order> o = orderRepository.findByOrderId("NEW_ORDER");
        assertTrue(o.isPresent());
        assertEquals(1, o.get().getTrades().size());
        assertEquals("NEW_TRADE", o.get().getTrades().iterator().next().getTradeId());
    }

}
