package tech.cassandre.trading.bot.test.domain;

import io.qase.api.annotation.CaseId;
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
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("schedule-disabled")
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
    @CaseId(35)
    @DisplayName("Check load trade from database")
    public void checkLoadTradeFromDatabase() {
        // =============================================================================================================
        // Check that positions, orders and trades in database doesn't trigger strategy events.
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());
        assertTrue(strategy.getOrdersUpdateReceived().isEmpty());

        // =============================================================================================================
        // Check trade 01.
        Optional<TradeDTO> trade = strategy.getTradeById("BACKUP_TRADE_01");
        assertTrue(trade.isPresent());
        assertEquals(1, trade.get().getId());
        assertEquals("BACKUP_TRADE_01", trade.get().getTradeId());
        assertEquals(BID, trade.get().getType());
        assertEquals("BACKUP_OPENING_ORDER_02", trade.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(trade.get().getAmount().getValue()));
        assertEquals(BTC, trade.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("10").compareTo(trade.get().getPrice().getValue()));
        assertEquals(USDT, trade.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("1").compareTo(trade.get().getFee().getValue()));
        assertEquals(USDT, trade.get().getFee().getCurrency());
        assertEquals("Trade 01", trade.get().getUserReference());
        assertEquals(createZonedDateTime("01-08-2020"), trade.get().getTimestamp());

        // =============================================================================================================
        // Check trade 02.
        trade = strategy.getTradeById("BACKUP_TRADE_02");
        assertTrue(trade.isPresent());
        assertEquals(2, trade.get().getId());
        assertEquals("BACKUP_TRADE_02", trade.get().getTradeId());
        assertEquals(BID, trade.get().getType());
        assertEquals("BACKUP_OPENING_ORDER_03", trade.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(trade.get().getAmount().getValue()));
        assertEquals(BTC, trade.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("20").compareTo(trade.get().getPrice().getValue()));
        assertEquals(USDT, trade.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("2").compareTo(trade.get().getFee().getValue()));
        assertEquals(USDT, trade.get().getFee().getCurrency());
        assertEquals("Trade 02", trade.get().getUserReference());
        assertEquals(createZonedDateTime("02-08-2020"), trade.get().getTimestamp());

        // =============================================================================================================
        // Check trade 03.
        trade = strategy.getTradeById("BACKUP_TRADE_03");
        assertTrue(trade.isPresent());
        assertEquals(3, trade.get().getId());
        assertEquals("BACKUP_TRADE_03", trade.get().getTradeId());
        assertEquals(BID, trade.get().getType());
        assertEquals("BACKUP_OPENING_ORDER_04", trade.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(trade.get().getAmount().getValue()));
        assertEquals(BTC, trade.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("30").compareTo(trade.get().getPrice().getValue()));
        assertEquals(USDT, trade.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("3").compareTo(trade.get().getFee().getValue()));
        assertEquals(USDT, trade.get().getFee().getCurrency());
        assertEquals("Trade 03", trade.get().getUserReference());
        assertEquals(createZonedDateTime("03-08-2020"), trade.get().getTimestamp());

        // =============================================================================================================
        // Check trade 04.
        trade = strategy.getTradeById("BACKUP_TRADE_04");
        assertTrue(trade.isPresent());
        assertEquals(4, trade.get().getId());
        assertEquals("BACKUP_TRADE_04", trade.get().getTradeId());
        assertEquals(ASK, trade.get().getType());
        assertEquals("BACKUP_CLOSING_ORDER_01", trade.get().getOrderId());
        assertEquals(new CurrencyPairDTO(BTC, USDT), trade.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(trade.get().getAmount().getValue()));
        assertEquals(BTC, trade.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("40").compareTo(trade.get().getPrice().getValue()));
        assertEquals(USDT, trade.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("4").compareTo(trade.get().getFee().getValue()));
        assertEquals(USDT, trade.get().getFee().getCurrency());
        assertEquals("Trade 04", trade.get().getUserReference());
        assertEquals(createZonedDateTime("04-08-2020"), trade.get().getTimestamp());

        // =============================================================================================================
        // Check trade 05.
        trade = strategy.getTradeById("BACKUP_TRADE_05");
        assertTrue(trade.isPresent());
        assertEquals(5, trade.get().getId());
        assertEquals("BACKUP_TRADE_05", trade.get().getTradeId());
        assertEquals(ASK, trade.get().getType());
        assertEquals("BACKUP_CLOSING_ORDER_02", trade.get().getOrderId());
        assertEquals(new CurrencyPairDTO(ETH, USD), trade.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("50").compareTo(trade.get().getAmount().getValue()));
        assertEquals(ETH, trade.get().getAmount().getCurrency());
        assertEquals(0, new BigDecimal("50").compareTo(trade.get().getPrice().getValue()));
        assertEquals(USD, trade.get().getPrice().getCurrency());
        assertEquals(0, new BigDecimal("5").compareTo(trade.get().getFee().getValue()));
        assertEquals(USD, trade.get().getFee().getCurrency());
        assertEquals("Trade 05", trade.get().getUserReference());
        assertEquals(createZonedDateTime("05-08-2020"), trade.get().getTimestamp());
    }

    @Test
    @CaseId(36)
    @DisplayName("Check save trade in database")
    public void checkSaveTradeInDatabase() {
        // =============================================================================================================
        // Check that positions, orders and trades in database doesn't trigger strategy events.
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());
        assertTrue(strategy.getTradesUpdateReceived().isEmpty());
        assertTrue(strategy.getOrdersUpdateReceived().isEmpty());

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
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("1.100001", cp1.getBaseCurrency()))
                .price(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()))
                .fee(new CurrencyAmountDTO(new BigDecimal("3.300003"), BTC))
                .userReference("My reference !")
                .timestamp(createZonedDateTime("01-09-2020"))
                .build();
        tradeFlux.emitValue(t1);
        await().untilAsserted(() -> assertEquals(1, strategy.getTradesUpdateReceived().size()));

        // =============================================================================================================
        // Trade - Check created order (domain).
        Optional<Trade> tradeInDatabase = tradeRepository.findByTradeId("BACKUP_TRADE_11");
        assertTrue(tradeInDatabase.isPresent());
        assertEquals(11, tradeInDatabase.get().getId());
        assertEquals("BACKUP_TRADE_11", tradeInDatabase.get().getTradeId());
        assertEquals(BID, tradeInDatabase.get().getType());
        assertEquals("BACKUP_ORDER_01", tradeInDatabase.get().getOrderId());
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
        assertEquals(cp1, tradeDTO.getCurrencyPair());
        assertEquals(cp1.getBaseCurrency(), tradeDTO.getAmount().getCurrency());
        assertEquals(0, tradeDTO.getAmount().getValue().compareTo(new BigDecimal("1.100001")));
        assertEquals(cp1.getBaseCurrency(), tradeDTO.getAmount().getCurrency());
        assertEquals(0, tradeDTO.getPrice().getValue().compareTo(new BigDecimal("2.200002")));
        assertEquals(cp1.getQuoteCurrency(), tradeDTO.getPrice().getCurrency());
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
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("1.100002", cp1.getBaseCurrency()))
                .price(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()))
                .fee(new CurrencyAmountDTO(new BigDecimal("3.300003"), BTC))
                .userReference("Updated reference")
                .timestamp(createZonedDateTime("01-09-2020"))
                .build());
        await().untilAsserted(() -> assertEquals(2, strategy.getTradesUpdateReceived().size()));
        await().untilAsserted(() -> assertNotNull(tradeRepository.findByTradeId("BACKUP_TRADE_11").get().getUpdatedOn()));
        assertEquals(createdOn, tradeRepository.findByTradeId("BACKUP_TRADE_11").get().getCreatedOn());
        ZonedDateTime updatedOn = tradeInDatabase.get().getCreatedOn();

        // =============================================================================================================
        // Updating the order - second time.
        tradeFlux.emitValue(TradeDTO.builder()
                .tradeId("BACKUP_TRADE_11")
                .orderId("BACKUP_ORDER_01")
                .type(BID)
                .currencyPair(cp1)
                .amount(new CurrencyAmountDTO("1.100003", cp1.getBaseCurrency()))
                .price(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()))
                .timestamp(createZonedDateTime("01-09-2020"))
                .fee(new CurrencyAmountDTO(new BigDecimal("3.300003"), BTC))
                .build());
        // TODO Remove warning from optional without isPresent.
        await().untilAsserted(() -> assertTrue(updatedOn.isBefore(tradeRepository.findByTradeId("BACKUP_TRADE_11").get().getUpdatedOn())));
        assertEquals(createdOn, tradeRepository.findByTradeId("BACKUP_TRADE_11").get().getCreatedOn());
        // We check if we still have the strategy set.
        final Optional<TradeDTO> optionalTrade = strategy.getTradeById("BACKUP_TRADE_11");
        assertTrue(optionalTrade.isPresent());
    }

    @Test
    @CaseId(37)
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
                .amount(new CurrencyAmountDTO("1.00001", cp1.getBaseCurrency()))
                .currencyPair(cp1)
                .userReference("MY_REF_3")
                .timestamp(createZonedDateTime("01-01-2020"))
                .status(NEW)
                .cumulativeAmount(new CurrencyAmountDTO("1.00002", cp1.getBaseCurrency()))
                .averagePrice(new CurrencyAmountDTO("1.00003", cp1.getQuoteCurrency()))
                .leverage("leverage3")
                .limitPrice(new CurrencyAmountDTO("1.00005", cp1.getQuoteCurrency()))
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
                .amount(new CurrencyAmountDTO("1.100003", cp1.getBaseCurrency()))
                .currencyPair(cp1)
                .price(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()))
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
