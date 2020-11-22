package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.test.batch.mocks.TickerFluxTestMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static tech.cassandre.trading.bot.util.parameters.ExchangeParameters.Rates.PARAMETER_EXCHANGE_RATE_TICKER;

@SpringBootTest
@DisplayName("Batch - Ticker flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_RATE_TICKER, value = "100")
})
@DirtiesContext(classMode = AFTER_CLASS)
@Import(TickerFluxTestMock.class)
public class TickerFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private MarketService marketService;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // 14 tickers are sent via the mocked service.
        // - 7 for cp1 ETH/BTC.
        // - 7 for cp2 ETH/USDT.
        // Two tickers for cp1 are identical so we expect to receive 13 tickers in the strategy.
        // we will call the service 17 times as some replies ill be empty.
        final int numberOfUpdatesExpected = 13;
        final int numberOfServiceCallsExpected = 17;

        // Waiting for the market service to have been called with all the test data (16).
        await().untilAsserted(() -> verify(marketService, atLeast(numberOfServiceCallsExpected)).getTicker(any()));

        // Checking that somme data have already been treated.
        // but not all as the flux should be asynchronous and single thread and strategy method method waits 1 second.
        assertTrue(strategy.getTickersUpdateReceived().size() > 0);
        assertTrue(strategy.getTickersUpdateReceived().size() < numberOfUpdatesExpected);

        // Wait for the strategy to have received all the tickers.
        await().untilAsserted(() -> assertTrue(strategy.getTickersUpdateReceived().size() >= numberOfUpdatesExpected));
        final Iterator<TickerDTO> iterator = strategy.getTickersUpdateReceived().iterator();

        // Test all values received by the strategy with update methods.
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

        // Eleventh value cp2 - 60.
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

        // Check data we have in strategy.
        final TickerDTO lastTickerForCp1 = strategy.getLastTicker().get(cp1);
        assertNotNull(lastTickerForCp1);
        assertEquals(cp1, lastTickerForCp1.getCurrencyPair());
        assertEquals(0, new BigDecimal("6").compareTo(lastTickerForCp1.getLast()));
        final TickerDTO lastTickerForCp2 = strategy.getLastTicker().get(cp2);
        assertNotNull(lastTickerForCp2);
        assertEquals(cp2, lastTickerForCp2.getCurrencyPair());
        assertEquals(0, new BigDecimal("70").compareTo(lastTickerForCp2.getLast()));
    }

}
