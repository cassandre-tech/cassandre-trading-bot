package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
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

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DisplayName("Batch - Trade flux")
@Configuration({
        @Property(key = "TEST_NAME", value = "Batch - Trade flux")
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Import(TradeFluxTestMock.class)
public class TradeFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TradeService tradeService;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        final int numberOfTradeExpected = 7;
        final int numberOfTradeServiceCalls = 5;

        // Waiting for the trade service to have been called with all the test data.
        await().untilAsserted(() -> verify(tradeService, atLeast(numberOfTradeServiceCalls)).getTrades());

        // Checking that somme tickers have already been treated (to verify we work on a single thread).
        assertTrue(strategy.getTradesUpdateReceived().size() <= numberOfTradeExpected);
        assertTrue(strategy.getTradesUpdateReceived().size() > 0);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertTrue(strategy.getTradesUpdateReceived().size() >= numberOfTradeExpected));

        // Test all values received.
        final Iterator<TradeDTO> iterator = strategy.getTradesUpdateReceived().iterator();

        assertEquals("0000001", iterator.next().getId());
        assertEquals("0000002", iterator.next().getId());
        assertEquals("0000003", iterator.next().getId());
        assertEquals("0000004", iterator.next().getId());
        assertEquals("0000005", iterator.next().getId());
        assertEquals("0000006", iterator.next().getId());
        assertEquals("0000008", iterator.next().getId());
    }

}
