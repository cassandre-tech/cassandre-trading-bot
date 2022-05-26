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
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Domain - Trade")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "true"),
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/test/core/backup.yaml"),
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class TradeTest extends BaseTest {

    @Autowired
    private OrderFlux orderFlux;

    @Autowired
    private TradeFlux tradeFlux;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check load trade from database")
    public void checkLoadTradeFromDatabase() {
        // =============================================================================================================
        // Check trade 01.
        Optional<TradeDTO> trade1 = strategy.getTradeByTradeId("BACKUP_TRADE_01");
        assertTrue(trade1.isPresent());
        assertEquals(1, trade1.get().getUid());
        assertEquals("BACKUP_TRADE_01", trade1.get().getTradeId());
        assertEquals(BID, trade1.get().getType());
        assertEquals("BACKUP_OPENING_ORDER_02", trade1.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(trade1.get().getAmount().getValue()));
        assertEquals(BTC, trade1.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("10").compareTo(trade1.get().getPrice().getValue()));
        assertEquals(USDT, trade1.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("1").compareTo(trade1.get().getFee().getValue()));
        assertEquals(USDT, trade1.get().getFee().getCurrency());
        assertEquals("Trade 01", trade1.get().getUserReference());
        assertTrue(createZonedDateTime("01-08-2020").isEqual(trade1.get().getTimestamp()));

        // Test equals.
        Optional<TradeDTO> trade1Bis = strategy.getTradeByTradeId("BACKUP_TRADE_01");
        assertTrue(trade1Bis.isPresent());
        assertEquals(trade1.get(), trade1Bis.get());

        // =============================================================================================================
        // Check trade 02.
        Optional<TradeDTO> trade2 = strategy.getTradeByTradeId("BACKUP_TRADE_02");
        assertTrue(trade2.isPresent());
        assertEquals(2, trade2.get().getUid());
        assertEquals("BACKUP_TRADE_02", trade2.get().getTradeId());
        assertEquals(BID, trade2.get().getType());
        assertEquals("BACKUP_OPENING_ORDER_03", trade2.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade2.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(trade2.get().getAmount().getValue()));
        assertEquals(BTC, trade2.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("20").compareTo(trade2.get().getPrice().getValue()));
        assertEquals(USDT, trade2.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("2").compareTo(trade2.get().getFee().getValue()));
        assertEquals(USDT, trade2.get().getFee().getCurrency());
        assertEquals("Trade 02", trade2.get().getUserReference());
        assertTrue(createZonedDateTime("02-08-2020").isEqual(trade2.get().getTimestamp()));

        // =============================================================================================================
        // Check trade 03.
        Optional<TradeDTO> trade3 = strategy.getTradeByTradeId("BACKUP_TRADE_03");
        assertTrue(trade3.isPresent());
        assertEquals(3, trade3.get().getUid());
        assertEquals("BACKUP_TRADE_03", trade3.get().getTradeId());
        assertEquals(BID, trade3.get().getType());
        assertEquals("BACKUP_OPENING_ORDER_04", trade3.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade3.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(trade3.get().getAmount().getValue()));
        assertEquals(BTC, trade3.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("30").compareTo(trade3.get().getPrice().getValue()));
        assertEquals(USDT, trade3.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("3").compareTo(trade3.get().getFee().getValue()));
        assertEquals(USDT, trade3.get().getFee().getCurrency());
        assertEquals("Trade 03", trade3.get().getUserReference());
        assertTrue(createZonedDateTime("03-08-2020").isEqual(trade3.get().getTimestamp()));

        // =============================================================================================================
        // Check trade 04.
        Optional<TradeDTO> trade4 = strategy.getTradeByTradeId("BACKUP_TRADE_04");
        assertTrue(trade4.isPresent());
        assertEquals(4, trade4.get().getUid());
        assertEquals("BACKUP_TRADE_04", trade4.get().getTradeId());
        assertEquals(ASK, trade4.get().getType());
        assertEquals("BACKUP_CLOSING_ORDER_01", trade4.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade4.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(trade4.get().getAmount().getValue()));
        assertEquals(BTC, trade4.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("40").compareTo(trade4.get().getPrice().getValue()));
        assertEquals(USDT, trade4.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("4").compareTo(trade4.get().getFee().getValue()));
        assertEquals(USDT, trade4.get().getFee().getCurrency());
        assertEquals("Trade 04", trade4.get().getUserReference());
        assertTrue(createZonedDateTime("04-08-2020").isEqual(trade4.get().getTimestamp()));

        // =============================================================================================================
        // Check trade 05.
        Optional<TradeDTO> trade5 = strategy.getTradeByTradeId("BACKUP_TRADE_05");
        assertTrue(trade5.isPresent());
        assertEquals(5, trade5.get().getUid());
        assertEquals("BACKUP_TRADE_05", trade5.get().getTradeId());
        assertEquals(ASK, trade5.get().getType());
        assertEquals("BACKUP_CLOSING_ORDER_02", trade5.get().getOrderId());
        assertEquals(new CurrencyPairDTO(ETH, USD), trade5.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(trade5.get().getAmount().getValue()));
        assertEquals(ETH, trade5.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("40").compareTo(trade5.get().getPrice().getValue()));
        assertEquals(USD, trade5.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("5").compareTo(trade5.get().getFee().getValue()));
        assertEquals(USD, trade5.get().getFee().getCurrency());
        assertEquals("Trade 05", trade5.get().getUserReference());
        assertTrue(createZonedDateTime("05-08-2020").isEqual(trade5.get().getTimestamp()));
    }

    @Test
    @DisplayName("Check save trade in database")
    public void checkSaveTradeInDatabase() {
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
        assertEquals(11, tradeInDatabase.get().getUid());
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
        assertTrue(createZonedDateTime("01-09-2020").isEqual(tradeInDatabase.get().getTimestamp()));

        // Tests for created on and updated on fields.
        ZonedDateTime createdOn = tradeInDatabase.get().getCreatedOn();
        assertNotNull(createdOn);
        assertNull(tradeInDatabase.get().getUpdatedOn());

        // =============================================================================================================
        // TradeDTO - Check created trade (dto).
        final TradeDTO tradeDTO = strategy.getTrades().get("BACKUP_TRADE_11");
        assertNotNull(tradeDTO);
        assertEquals(11, tradeDTO.getUid());
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
        assertTrue(createZonedDateTime("01-09-2020").isEqual(tradeDTO.getTimestamp()));

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
        Optional<Trade> trade11 = tradeRepository.findByTradeId("BACKUP_TRADE_11");
        assertTrue(trade11.isPresent());
        assertNotNull(trade11.get().getUpdatedOn());
        assertEquals(createdOn, trade11.get().getCreatedOn());
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
        await().untilAsserted(() -> assertEquals(3, strategy.getTradesUpdatesReceived().size()));
        trade11 = tradeRepository.findByTradeId("BACKUP_TRADE_11");
        assertTrue(trade11.isPresent());
        assertTrue(updatedOn.isBefore(trade11.get().getUpdatedOn()));
        assertEquals(createdOn, trade11.get().getCreatedOn());
        // We check if we still have the strategy set.
        final Optional<TradeDTO> optionalTrade = strategy.getTradeByTradeId("BACKUP_TRADE_11");
        assertTrue(optionalTrade.isPresent());
    }

    @Test
    @DisplayName("Check link between order and trade")
    public void checkLinkBetweenOrderAndTrade() {
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
        // Then a new trade arrives for order NEW_ORDER.
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

        // =============================================================================================================
        // We check the link between order and trade.
        final Optional<Trade> t = tradeRepository.findByTradeId("NEW_TRADE");
        assertTrue(t.isPresent());
        final Optional<Order> o = orderRepository.findByOrderId("NEW_ORDER");
        assertTrue(o.isPresent());
        assertEquals(1, o.get().getTrades().size());
        assertEquals("NEW_TRADE", o.get().getTrades().iterator().next().getTradeId());
    }

}
