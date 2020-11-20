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

import java.util.Iterator;
import java.util.Map;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
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
    @Tag("notReviewed")
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // We will call the service 5 times with the first and third reply empty.
        // We receive:
        // First reply: 0000001, 0000002.
        // Second reply: empty.
        // Third reply: empty.
        // Fourth reply: 0000003, 0000004, 0000005.
        // Fifth reply: 0000006, 0000003, 0000008.
        final int numberOfUpdatesExpected = 7;
        final int numberOfServiceCallsExpected = 5;

        // Waiting for the trade service to have been called with all the test data.
        await().untilAsserted(() -> verify(tradeService, atLeast(numberOfServiceCallsExpected)).getTrades());

        // Checking that somme data have already been treated.
        // but not all as the flux should be asynchronous and single thread and strategy method method waits 1 second.
        assertTrue(strategy.getTradesUpdateReceived().size() > 0);
        assertTrue(strategy.getTradesUpdateReceived().size() < numberOfUpdatesExpected);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertTrue(strategy.getTradesUpdateReceived().size() >= numberOfUpdatesExpected));

        // Test all values received.
        final Iterator<TradeDTO> iterator = strategy.getTradesUpdateReceived().iterator();
        assertEquals("0000001", iterator.next().getId());
        assertEquals("0000002", iterator.next().getId());
        assertEquals("0000003", iterator.next().getId());
        assertEquals("0000004", iterator.next().getId());
        assertEquals("0000005", iterator.next().getId());
        assertEquals("0000006", iterator.next().getId());
        assertEquals("0000008", iterator.next().getId());

        // Check data we have in strategy.
        final Map<String, TradeDTO> strategyTrades = strategy.getTrades();
        strategyTrades.forEach((s, tradeDTO) -> System.out.println("=> " + tradeDTO));
        assertEquals(7, strategyTrades.size());
    }

}
