package tech.cassandre.trading.bot.test.batch;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.mock.batch.TradeFluxTestMock;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_TRADE;

@SpringBootTest
@DisplayName("Batch - Trade flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_RATE_TRADE, value = "100"),
        @Property(key = "spring.datasource.data", value = "classpath:/trade-test.sql")
})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Import(TradeFluxTestMock.class)
public class TradeFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderFlux orderFlux;

    @Autowired
    private TradeService tradeService;

    @Test
    @CaseId(6)
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // =============================================================================================================
        // Test asynchronous flux.

        // We will call the service 5 times with the first and third reply empty.
        // We receive:
        // First reply: TRADE_0000001, TRADE_0000002.
        // Second reply: empty.
        // Third reply: empty.
        // Fourth reply: TRADE_0000003, TRADE_0000004, TRADE_0000005.
        // Fifth reply: TRADE_0000006, TRADE_0000002 ,TRADE_0000003, TRADE_0000008.
        // TRADE_0000003 is an update. TRADE_0000002 is the same.
        final int numberOfUpdatesExpected = 8;
        final int numberOfServiceCallsExpected = 5;

        // Waiting for the service to have been called with all the test data.
        await().untilAsserted(() -> verify(tradeService, atLeast(numberOfServiceCallsExpected)).getTrades());

        // Checking that somme data have already been treated.
        // but not all as the flux should be asynchronous and single thread and strategy method method waits 1 second.
        assertTrue(strategy.getTradesUpdateReceived().size() > 0);
        assertTrue(strategy.getTradesUpdateReceived().size() <= numberOfUpdatesExpected);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertTrue(strategy.getTradesUpdateReceived().size() >= numberOfUpdatesExpected));

        // =============================================================================================================
        // Test all values received by the strategy with update methods.
        final Iterator<TradeDTO> trades = strategy.getTradesUpdateReceived().iterator();

        // Check update 1.
        TradeDTO t = trades.next();
        assertNull(t.getId());
        assertEquals("TRADE_0000001", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", cp1.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000001", t.getUserReference());
        assertTrue(createDate(1).isEqual(t.getTimestamp()));

        // Check update 2.
        t = trades.next();
        assertNull(t.getId());
        assertEquals("TRADE_0000002", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", cp1.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000002", t.getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t.getTimestamp()));

        // Check update 3.
        t = trades.next();
        assertNull(t.getId());
        assertEquals("TRADE_0000003", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000002", t.getOrderId());
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", cp2.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp2.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000003", t.getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t.getTimestamp()));

        // Check update 4.
        t = trades.next();
        assertNull(t.getId());
        assertEquals("TRADE_0000004", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", cp1.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000004", t.getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t.getTimestamp()));

        // Check update 5.
        t = trades.next();
        assertNull(t.getId());
        assertEquals("TRADE_0000005", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", cp1.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000005", t.getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t.getTimestamp()));

        // Check update 6.
        t = trades.next();
        assertNull(t.getId());
        assertEquals("TRADE_0000006", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", cp2.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp2.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000006", t.getUserReference());
        assertTrue(createZonedDateTime("02-09-2020").isEqual(t.getTimestamp()));

        // Check update 7.
        t = trades.next();
        assertNull(t.getId());
        assertEquals("TRADE_0000003", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000002", t.getOrderId());
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.110001", cp2.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.220002", cp2.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.330003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000003", t.getUserReference());
        assertTrue(createZonedDateTime("02-09-2021").isEqual(t.getTimestamp()));

        // Check update 8.
        t = trades.next();
        assertNull(t.getId());
        assertEquals("TRADE_0000008", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", cp1.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000008", t.getUserReference());
        assertTrue(createZonedDateTime("02-09-2020").isEqual(t.getTimestamp()));

        // =============================================================================================================
        // Check data we have in strategy & database.
        assertEquals(7, tradeRepository.count());
        final Map<String, TradeDTO> strategyTrades = strategy.getTrades();
        assertEquals(7, strategyTrades.size());
        assertNotNull(strategyTrades.get("TRADE_0000001"));
        assertNotNull(strategyTrades.get("TRADE_0000002"));
        assertNotNull(strategyTrades.get("TRADE_0000003"));
        assertNotNull(strategyTrades.get("TRADE_0000004"));
        assertNotNull(strategyTrades.get("TRADE_0000005"));
        assertNotNull(strategyTrades.get("TRADE_0000006"));
        assertNotNull(strategyTrades.get("TRADE_0000008"));

        // Trade TRADE_0000001.
        final Optional<TradeDTO> t1 = strategy.getTradeById("TRADE_0000001");
        assertTrue(t1.isPresent());
        assertEquals(1, t1.get().getId());
        assertEquals("TRADE_0000001", t1.get().getTradeId());
        assertEquals(BID, t1.get().getType());
        assertEquals("ORDER_0000001", t1.get().getOrderId());
        assertEquals(cp1, t1.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", cp1.getBaseCurrency()), t1.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()), t1.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t1.get().getFee());
        assertEquals("Ref TRADE_0000001", t1.get().getUserReference());
        assertTrue(createDate(1).isEqual(t1.get().getTimestamp()));

        // Trade TRADE_0000002.
        final Optional<TradeDTO> t2 = strategy.getTradeById("TRADE_0000002");
        assertTrue(t2.isPresent());
        assertEquals(2, t2.get().getId());
        assertEquals("TRADE_0000002", t2.get().getTradeId());
        assertEquals(BID, t2.get().getType());
        assertEquals("ORDER_0000001", t2.get().getOrderId());
        assertEquals(cp1, t2.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", cp1.getBaseCurrency()), t2.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()), t2.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t2.get().getFee());
        assertEquals("Ref TRADE_0000002", t2.get().getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t2.get().getTimestamp()));

        // Trade TRADE_0000003 - The trade 3 was received two times so data have been updated.
        final Optional<TradeDTO> t3 = strategy.getTradeById("TRADE_0000003");
        assertTrue(t3.isPresent());
        assertEquals(3, t3.get().getId());
        assertEquals("TRADE_0000003", t3.get().getTradeId());
        assertEquals(BID, t3.get().getType());
        assertEquals("ORDER_0000002", t3.get().getOrderId());
        assertEquals(cp2, t3.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.110001", cp2.getBaseCurrency()), t3.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.220002", cp2.getQuoteCurrency()), t3.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.330003", BTC), t3.get().getFee());
        assertEquals("Ref TRADE_0000003", t3.get().getUserReference());
        assertTrue(createZonedDateTime("02-09-2021").isEqual(t3.get().getTimestamp()));

        // Trade TRADE_0000004.
        final Optional<TradeDTO> t4 = strategy.getTradeById("TRADE_0000004");
        assertTrue(t4.isPresent());
        assertEquals(4, t4.get().getId());
        assertEquals("TRADE_0000004", t4.get().getTradeId());
        assertEquals(BID, t4.get().getType());
        assertEquals("ORDER_0000001", t4.get().getOrderId());
        assertEquals(cp1, t4.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", cp1.getBaseCurrency()), t4.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()), t4.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t4.get().getFee());
        assertEquals("Ref TRADE_0000004", t4.get().getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t4.get().getTimestamp()));

        // Trade TRADE_0000005.
        final Optional<TradeDTO> t5 = strategy.getTradeById("TRADE_0000005");
        assertTrue(t5.isPresent());
        assertEquals(5, t5.get().getId());
        assertEquals("TRADE_0000005", t5.get().getTradeId());
        assertEquals(BID, t5.get().getType());
        assertEquals("ORDER_0000001", t5.get().getOrderId());
        assertEquals(cp1, t5.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", cp1.getBaseCurrency()), t5.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()), t5.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t5.get().getFee());
        assertEquals("Ref TRADE_0000005", t5.get().getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t5.get().getTimestamp()));

        // Trade TRADE_0000006.
        final Optional<TradeDTO> t6 = strategy.getTradeById("TRADE_0000006");
        assertTrue(t6.isPresent());
        assertEquals(6, t6.get().getId());
        assertEquals("TRADE_0000006", t6.get().getTradeId());
        assertEquals(BID, t6.get().getType());
        assertEquals("ORDER_0000001", t6.get().getOrderId());
        assertEquals(cp2, t6.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", cp2.getBaseCurrency()), t6.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp2.getQuoteCurrency()), t6.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t6.get().getFee());
        assertEquals("Ref TRADE_0000006", t6.get().getUserReference());
        assertTrue(createZonedDateTime("02-09-2020").isEqual(t6.get().getTimestamp()));

        // Trade TRADE_0000008.
        final Optional<TradeDTO> t8 = strategy.getTradeById("TRADE_0000008");
        assertTrue(t8.isPresent());
        assertEquals(7, t8.get().getId());
        assertEquals("TRADE_0000008", t8.get().getTradeId());
        assertEquals(BID, t8.get().getType());
        assertEquals("ORDER_0000001", t8.get().getOrderId());
        assertEquals(cp1, t8.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", cp1.getBaseCurrency()), t8.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", cp1.getQuoteCurrency()), t8.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t8.get().getFee());
        assertEquals("Ref TRADE_0000008", t8.get().getUserReference());
        assertTrue(createZonedDateTime("02-09-2020").isEqual(t8.get().getTimestamp()));

        // =============================================================================================================
        // Check if all is ok with order links.
        final Optional<OrderDTO> ORDER_0000001 = strategy.getOrderById("ORDER_0000001");
        assertTrue(ORDER_0000001.isPresent());
        assertEquals(6, ORDER_0000001.get().getTrades().size());
        final Optional<OrderDTO> ORDER_0000002 = strategy.getOrderById("ORDER_0000002");
        assertTrue(ORDER_0000002.isPresent());
        assertEquals(1, ORDER_0000002.get().getTrades().size());
    }

}
