package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.mock.batch.TradeFluxTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
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
    private OrderRepository orderRepository;

    @Autowired
    private OrderFlux orderFlux;

    @Autowired
    private TradeService tradeService;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // =============================================================================================================
        // Test asynchronous flux.

        // We will call the service 5 times with the first and third reply empty.
        // We receive:
        // First reply: 0000001, 0000002.
        // Second reply: empty.
        // Third reply: empty.
        // Fourth reply: 0000003, 0000004, 0000005.
        // Fifth reply: 0000006, 0000002 ,0000003, 0000008.
        // 0000003 is an update. 0000002 is the same.
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
        final Iterator<TradeDTO> iterator = strategy.getTradesUpdateReceived().iterator();
        assertEquals("0000001", iterator.next().getTradeId());
        assertEquals("0000002", iterator.next().getTradeId());
        assertEquals("0000003", iterator.next().getTradeId());
        assertEquals("0000004", iterator.next().getTradeId());
        assertEquals("0000005", iterator.next().getTradeId());
        assertEquals("0000006", iterator.next().getTradeId());
        assertEquals("0000003", iterator.next().getTradeId());
        assertEquals("0000008", iterator.next().getTradeId());

        // =============================================================================================================
        // Check data we have in strategy.
        final Map<String, TradeDTO> strategyTrades = strategy.getTrades();
        assertEquals(7, strategyTrades.size());
        // Trade 1.
        final TradeDTO trade1 = strategyTrades.get("0000001");
        assertNotNull(trade1);
        assertNotNull(strategy.getTradeById("0000001"));
        assertEquals("0000001", trade1.getTradeId());
        assertEquals(BID, trade1.getType());
        assertEquals(cp1, trade1.getCurrencyPair());
        // Trade 2.
        final TradeDTO trade2 = strategyTrades.get("0000002");
        assertNotNull(trade2);
        assertNotNull(strategy.getTradeById("0000002"));
        assertEquals("0000002", trade2.getTradeId());
        assertEquals(BID, trade2.getType());
        assertEquals(cp1, trade2.getCurrencyPair());
        // Trade 3 - The trade 3 was received two times so data have been updated.
        final TradeDTO trade3 = strategyTrades.get("0000003");
        assertNotNull(trade3);
        assertNotNull(strategy.getTradeById("0000003"));
        assertEquals("0000003", trade3.getTradeId());
        assertEquals(BID, trade3.getType());
        assertEquals(cp2, trade3.getCurrencyPair());
        assertEquals("ORDER00002", trade3.getOrderId());
        assertEquals(0, new BigDecimal("1.110001").compareTo(trade3.getAmount().getValue()));
        assertEquals(cp2.getBaseCurrency(), trade3.getAmount().getCurrency());
        assertEquals(0, new BigDecimal("2.220002").compareTo(trade3.getPrice().getValue()));
        // TODO Check why there was an error
        // assertEquals(cp2.getQuoteCurrency(), trade3.getPrice().getCurrency());
        assertTrue(createZonedDateTime("02-09-2021").isEqual(trade3.getTimestamp()));
        assertEquals(0, new BigDecimal("3.330003").compareTo(trade3.getFee().getValue()));
        assertEquals(BTC, trade3.getFee().getCurrency());
        // Trade 4.
        final TradeDTO trade4 = strategyTrades.get("0000004");
        assertNotNull(trade4);
        assertNotNull(strategy.getTradeById("0000004"));
        assertEquals("0000004", trade4.getTradeId());
        assertEquals(BID, trade4.getType());
        assertEquals(cp1, trade4.getCurrencyPair());
        // Trade 5.
        final TradeDTO trade5 = strategyTrades.get("0000005");
        assertNotNull(trade5);
        assertNotNull(strategy.getTradeById("0000005"));
        assertEquals("0000005", trade5.getTradeId());
        assertEquals(BID, trade5.getType());
        assertEquals(cp1, trade5.getCurrencyPair());
        // Trade 6.
        final TradeDTO trade6 = strategyTrades.get("0000006");
        assertNotNull(trade6);
        assertNotNull(strategy.getTradeById("0000006"));
        assertEquals("0000006", trade6.getTradeId());
        assertEquals(BID, trade6.getType());
        assertEquals(cp2, trade6.getCurrencyPair());
        // Trade 7.
        final TradeDTO trade8 = strategyTrades.get("0000008");
        assertNotNull(trade8);
        assertNotNull(strategy.getTradeById("0000008"));
        assertEquals("0000008", trade8.getTradeId());
        assertEquals(BID, trade8.getType());
        assertEquals(cp1, trade8.getCurrencyPair());

        // =============================================================================================================
        // Check if all is ok in database.
        final Optional<Order> order00001 = orderRepository.findByOrderId("ORDER00001");
        assertTrue(order00001.isPresent());
        assertEquals(6 , order00001.get().getTrades().size());
        final Optional<Order> order00002 = orderRepository.findByOrderId("ORDER00002");
        assertTrue(order00002.isPresent());
        assertEquals(1 , order00002.get().getTrades().size());
    }

}
