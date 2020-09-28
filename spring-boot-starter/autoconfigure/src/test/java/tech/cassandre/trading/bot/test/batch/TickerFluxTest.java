package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.test.batch.mocks.TickerFluxTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DisplayName("Batch - Ticker flux")
@Configuration({
        @Property(key = "TEST_NAME", value = "Batch - Ticker flux")
})
@Import(TickerFluxTestMock.class)
public class TickerFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private MarketService marketService;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        final int numberOfTickersExpected = 13;
        final int numberOfMarketServiceCalls = 16;

        // Currency pairs supported.
        final CurrencyPairDTO cp1 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);
        final CurrencyPairDTO cp2 = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.USDT);

        // Waiting for the market service to have been called with all the test data.
        await().untilAsserted(() -> verify(marketService, atLeast(numberOfMarketServiceCalls)).getTicker(any()));

        // Checking that somme tickers have already been treated (to verify we work on a single thread).
        assertTrue(strategy.getTickersUpdateReceived().size() <= numberOfTickersExpected);
        assertTrue(strategy.getTickersUpdateReceived().size() > 0);

        // Wait for the strategy to have received all the test values.
        await().untilAsserted(() -> assertTrue(strategy.getTickersUpdateReceived().size() >= numberOfTickersExpected));

        // Test all values received.
        final Iterator<TickerDTO> iterator = strategy.getTickersUpdateReceived().iterator();

        // First value cp1 - 1.
        TickerDTO t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("1").compareTo(t.getBid()));

        // Second value cp2 - 10.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(t.getBid()));

        // Third value cp1 - 2.
        t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("2").compareTo(t.getBid()));

        // Fourth value cp2 - 20.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(t.getBid()));

        // Fifth value cp1 - 3.
        t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("3").compareTo(t.getBid()));

        // Sixth value cp2 - 30.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(t.getBid()));

        // Seventh value cp2 - 40.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(t.getBid()));

        // Eighth value cp1 - 4.
        t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("4").compareTo(t.getBid()));

        // Ninth value cp2 - 50.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("50").compareTo(t.getBid()));

        // Tenth value cp1 - 5.
        t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("5").compareTo(t.getBid()));

        // Eleventh value cp2 - 50.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("60").compareTo(t.getBid()));

        // Twelfth value cp1 - 6.
        t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("6").compareTo(t.getBid()));

        // Thirteenth value cp2 - 70.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("70").compareTo(t.getBid()));
    }

}
