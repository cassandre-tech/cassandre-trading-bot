package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.test.batch.mocks.TradeFluxTestMock;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SpringBootTest
@DisplayName("Batch - Trade flux")
@Configuration({
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/trade-test.yaml")
})
@Import(TradeFluxTestMock.class)
public class TradeFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private org.knowm.xchange.service.trade.TradeService xChangeTradeService;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // =============================================================================================================
        // Test asynchronous flux.

        // We will call the service 5 times with the first and third reply empty.
        // We receive:
        // First reply: TRADE_0000001, TRADE_0000002.
        // Second reply: TRADE_0000003, TRADE_0000004, TRADE_0000005.
        // Third reply: TRADE_0000006, TRADE_0000002 ,TRADE_0000003, TRADE_0000008.
        // TRADE_0000003 is an update. TRADE_0000002 is the same.
        final int numberOfUpdatesExpected = 8;
        final int numberOfServiceCallsExpected = 3;

        // Waiting for the service to have been called with all the test data.
        await().untilAsserted(() -> verify(xChangeTradeService, atLeast(numberOfServiceCallsExpected)).getTradeHistory(any()));

        // Checking that somme data have already been treated.
        // but not all as the flux should be asynchronous and single thread and strategy method method waits 1 second.
        assertTrue(strategy.getTradesUpdatesReceived().size() > 0);
        assertTrue(strategy.getTradesUpdatesReceived().size() <= numberOfUpdatesExpected);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertTrue(strategy.getTradesUpdatesReceived().size() >= numberOfUpdatesExpected));

        // =============================================================================================================
        // Test all values received by the strategy with update methods.
        final Iterator<TradeDTO> trades = strategy.getTradesUpdatesReceived().iterator();

        // Check update 1.
        TradeDTO t = trades.next();
        assertEquals(1, t.getId());
        assertEquals("TRADE_0000001", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", ETH_BTC.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000001", t.getUserReference());
        assertTrue(createZonedDateTime(1).isEqual(t.getTimestamp()));

        // Check update 2.
        t = trades.next();
        assertEquals(2, t.getId());
        assertEquals("TRADE_0000002", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", ETH_BTC.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000002", t.getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t.getTimestamp()));

        // Check update 3.
        t = trades.next();
        assertEquals(3, t.getId());
        assertEquals("TRADE_0000003", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000002", t.getOrderId());
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", ETH_USDT.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_USDT.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", ETH), t.getFee());
        assertEquals("Ref TRADE_0000003", t.getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t.getTimestamp()));

        // Check update 4.
        t = trades.next();
        assertEquals(4, t.getId());
        assertEquals("TRADE_0000004", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", ETH_BTC.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000004", t.getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t.getTimestamp()));

        // Check update 5.
        t = trades.next();
        assertEquals(5, t.getId());
        assertEquals("TRADE_0000005", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", ETH_BTC.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000005", t.getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t.getTimestamp()));

        // Check update 6.
        t = trades.next();
        assertEquals(6, t.getId());
        assertEquals("TRADE_0000006", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", ETH_USDT.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_USDT.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", USDT), t.getFee());
        assertEquals("Ref TRADE_0000006", t.getUserReference());
        assertTrue(createZonedDateTime("01-08-2018").isEqual(t.getTimestamp()));

        // Check update 7.
        t = trades.next();
        assertEquals(7, t.getId());
        assertEquals("TRADE_0000008", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000001", t.getOrderId());
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", ETH_BTC.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000008", t.getUserReference());
        assertTrue(createZonedDateTime("02-09-2020").isEqual(t.getTimestamp()));

        // Check update 8.
        t = trades.next();
        assertEquals(3, t.getId());
        assertEquals("TRADE_0000003", t.getTradeId());
        assertEquals(BID, t.getType());
        assertEquals("ORDER_0000002", t.getOrderId());
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.110001", ETH_USDT.getBaseCurrency()), t.getAmount());
        assertEquals(new CurrencyAmountDTO("2.220002", ETH_USDT.getQuoteCurrency()), t.getPrice());
        assertEquals(new CurrencyAmountDTO("3.330003", BTC), t.getFee());
        assertEquals("Ref TRADE_0000003", t.getUserReference());
        assertTrue(createZonedDateTime("01-09-2021").isEqual(t.getTimestamp()));

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
        final Optional<TradeDTO> t1 = strategy.getTradeByTradeId("TRADE_0000001");
        assertTrue(t1.isPresent());
        assertEquals(1, t1.get().getId());
        assertEquals("TRADE_0000001", t1.get().getTradeId());
        assertEquals(BID, t1.get().getType());
        assertEquals("ORDER_0000001", t1.get().getOrderId());
        assertEquals(ETH_BTC, t1.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", ETH_BTC.getBaseCurrency()), t1.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()), t1.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t1.get().getFee());
        assertEquals("Ref TRADE_0000001", t1.get().getUserReference());
        assertTrue(createZonedDateTime(1).isEqual(t1.get().getTimestamp()));

        // Trade TRADE_0000002.
        final Optional<TradeDTO> t2 = strategy.getTradeByTradeId("TRADE_0000002");
        assertTrue(t2.isPresent());
        assertEquals(2, t2.get().getId());
        assertEquals("TRADE_0000002", t2.get().getTradeId());
        assertEquals(BID, t2.get().getType());
        assertEquals("ORDER_0000001", t2.get().getOrderId());
        assertEquals(ETH_BTC, t2.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", ETH_BTC.getBaseCurrency()), t2.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()), t2.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t2.get().getFee());
        assertEquals("Ref TRADE_0000002", t2.get().getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t2.get().getTimestamp()));

        // Trade TRADE_0000003 - The trade 3 was received two times so data have been updated.
        final Optional<TradeDTO> t3 = strategy.getTradeByTradeId("TRADE_0000003");
        assertTrue(t3.isPresent());
        assertEquals(3, t3.get().getId());
        assertEquals("TRADE_0000003", t3.get().getTradeId());
        assertEquals(BID, t3.get().getType());
        assertEquals("ORDER_0000002", t3.get().getOrderId());
        assertEquals(ETH_USDT, t3.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.110001", ETH_USDT.getBaseCurrency()), t3.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.220002", ETH_USDT.getQuoteCurrency()), t3.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.330003", BTC), t3.get().getFee());
        assertEquals("Ref TRADE_0000003", t3.get().getUserReference());
        assertTrue(createZonedDateTime("01-09-2021").isEqual(t3.get().getTimestamp()));

        // Trade TRADE_0000004.
        final Optional<TradeDTO> t4 = strategy.getTradeByTradeId("TRADE_0000004");
        assertTrue(t4.isPresent());
        assertEquals(4, t4.get().getId());
        assertEquals("TRADE_0000004", t4.get().getTradeId());
        assertEquals(BID, t4.get().getType());
        assertEquals("ORDER_0000001", t4.get().getOrderId());
        assertEquals(ETH_BTC, t4.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", ETH_BTC.getBaseCurrency()), t4.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()), t4.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t4.get().getFee());
        assertEquals("Ref TRADE_0000004", t4.get().getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t4.get().getTimestamp()));

        // Trade TRADE_0000005.
        final Optional<TradeDTO> t5 = strategy.getTradeByTradeId("TRADE_0000005");
        assertTrue(t5.isPresent());
        assertEquals(5, t5.get().getId());
        assertEquals("TRADE_0000005", t5.get().getTradeId());
        assertEquals(BID, t5.get().getType());
        assertEquals("ORDER_0000001", t5.get().getOrderId());
        assertEquals(ETH_BTC, t5.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1", ETH_BTC.getBaseCurrency()), t5.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()), t5.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t5.get().getFee());
        assertEquals("Ref TRADE_0000005", t5.get().getUserReference());
        assertTrue(createZonedDateTime("01-09-2020").isEqual(t5.get().getTimestamp()));

        // Trade TRADE_0000006.
        final Optional<TradeDTO> t6 = strategy.getTradeByTradeId("TRADE_0000006");
        assertTrue(t6.isPresent());
        assertEquals(6, t6.get().getId());
        assertEquals("TRADE_0000006", t6.get().getTradeId());
        assertEquals(BID, t6.get().getType());
        assertEquals("ORDER_0000001", t6.get().getOrderId());
        assertEquals(ETH_USDT, t6.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", ETH_USDT.getBaseCurrency()), t6.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_USDT.getQuoteCurrency()), t6.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", USDT), t6.get().getFee());
        assertEquals("Ref TRADE_0000006", t6.get().getUserReference());
        assertTrue(createZonedDateTime("01-08-2018").isEqual(t6.get().getTimestamp()));

        // Trade TRADE_0000008.
        final Optional<TradeDTO> t8 = strategy.getTradeByTradeId("TRADE_0000008");
        assertTrue(t8.isPresent());
        assertEquals(7, t8.get().getId());
        assertEquals("TRADE_0000008", t8.get().getTradeId());
        assertEquals(BID, t8.get().getType());
        assertEquals("ORDER_0000001", t8.get().getOrderId());
        assertEquals(ETH_BTC, t8.get().getCurrencyPair());
        assertEquals(new CurrencyAmountDTO("1.100001", ETH_BTC.getBaseCurrency()), t8.get().getAmount());
        assertEquals(new CurrencyAmountDTO("2.200002", ETH_BTC.getQuoteCurrency()), t8.get().getPrice());
        assertEquals(new CurrencyAmountDTO("3.300003", BTC), t8.get().getFee());
        assertEquals("Ref TRADE_0000008", t8.get().getUserReference());
        assertTrue(createZonedDateTime("02-09-2020").isEqual(t8.get().getTimestamp()));

        // =============================================================================================================
        // Check if all is ok with order links.
        final Optional<OrderDTO> ORDER_0000001 = strategy.getOrderByOrderId("ORDER_0000001");
        assertTrue(ORDER_0000001.isPresent());
        assertEquals(6, ORDER_0000001.get().getTrades().size());
        final Optional<OrderDTO> ORDER_0000002 = strategy.getOrderByOrderId("ORDER_0000002");
        assertTrue(ORDER_0000002.isPresent());
        assertEquals(1, ORDER_0000002.get().getTrades().size());
    }

}
