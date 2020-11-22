package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.test.batch.mocks.TradeFluxTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.dto.trade.OrderTypeDTO.BID;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_TRADE;

@SpringBootTest
@DisplayName("Batch - Trade flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_RATE_TRADE, value = "100")
})
@DirtiesContext(classMode = AFTER_CLASS)
@Import(TradeFluxTestMock.class)
public class TradeFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TradeService tradeService;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
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

        // Waiting for the trade service to have been called with all the test data.
        await().untilAsserted(() -> verify(tradeService, atLeast(numberOfServiceCallsExpected)).getTrades());

        // Checking that somme data have already been treated.
        // but not all as the flux should be asynchronous and single thread and strategy method method waits 1 second.
        assertTrue(strategy.getTradesUpdateReceived().size() > 0);
        assertTrue(strategy.getTradesUpdateReceived().size() < numberOfUpdatesExpected);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertTrue(strategy.getTradesUpdateReceived().size() >= numberOfUpdatesExpected));

        // Test all values received by the strategy with update methods.
        final Iterator<TradeDTO> iterator = strategy.getTradesUpdateReceived().iterator();
        assertEquals("0000001", iterator.next().getId());
        assertEquals("0000002", iterator.next().getId());
        assertEquals("0000003", iterator.next().getId());
        assertEquals("0000004", iterator.next().getId());
        assertEquals("0000005", iterator.next().getId());
        assertEquals("0000006", iterator.next().getId());
        assertEquals("0000003", iterator.next().getId());
        assertEquals("0000008", iterator.next().getId());

        // Check data we have in strategy.
        final Map<String, TradeDTO> strategyTrades = strategy.getTrades();
        assertEquals(7, strategyTrades.size());
        // Trade 1.
        final TradeDTO trade1 = strategyTrades.get("0000001");
        assertNotNull(trade1);
        assertEquals("0000001", trade1.getId());
        assertEquals(BID, trade1.getType());
        assertEquals(cp1, trade1.getCurrencyPair());
        // Trade 2.
        final TradeDTO trade2 = strategyTrades.get("0000002");
        assertNotNull(trade2);
        assertEquals("0000002", trade2.getId());
        assertEquals(BID, trade2.getType());
        assertEquals(cp1, trade2.getCurrencyPair());
        // Trade 3 - The trade 3 was received two times so data have been updated.
        final TradeDTO trade3 = strategyTrades.get("0000003");
        assertNotNull(trade3);
        assertEquals("0000003", trade3.getId());
        assertEquals(BID, trade3.getType());
        assertEquals(cp2, trade3.getCurrencyPair());
        assertEquals("EMPTY!", trade3.getOrderId());
        assertEquals(0, new BigDecimal("1.110001").compareTo(trade3.getOriginalAmount()));
        assertEquals(0, new BigDecimal("2.220002").compareTo(trade3.getPrice()));
        assertTrue(createZonedDateTime("02-09-2020").isEqual(trade3.getTimestamp()));
        assertEquals(0, new BigDecimal("3.330003").compareTo(trade3.getFee().getValue()));
        assertEquals(BTC, trade3.getFee().getCurrency());
        // Trade 4.
        final TradeDTO trade4 = strategyTrades.get("0000004");
        assertNotNull(trade4);
        assertEquals("0000004", trade4.getId());
        assertEquals(BID, trade4.getType());
        assertEquals(cp1, trade4.getCurrencyPair());
        // Trade 5.
        final TradeDTO trade5 = strategyTrades.get("0000005");
        assertNotNull(trade5);
        assertEquals("0000005", trade5.getId());
        assertEquals(BID, trade5.getType());
        assertEquals(cp1, trade5.getCurrencyPair());
        // Trade 6.
        final TradeDTO trade6 = strategyTrades.get("0000006");
        assertNotNull(trade6);
        assertEquals("0000006", trade6.getId());
        assertEquals(BID, trade6.getType());
        assertEquals(cp2, trade6.getCurrencyPair());
        // Trade 7.
        final TradeDTO trade8 = strategyTrades.get("0000008");
        assertNotNull(trade8);
        assertEquals("0000008", trade8.getId());
        assertEquals(BID, trade8.getType());
        assertEquals(cp1, trade8.getCurrencyPair());
    }

}
