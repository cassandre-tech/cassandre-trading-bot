package tech.cassandre.trading.bot.test.batch;

import io.qase.api.annotation.CaseId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Batch - Ticker flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@Import(TickerFluxTestMock.class)
public class TickerFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private MarketService marketService;

    @Autowired
    private MarketDataService marketDataService;

    @Test
    @CaseId(5)
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // =============================================================================================================
        // Test asynchronous flux.

        // 14 tickers are sent via the mocked service.
        // - 7 for cp1 ETH/BTC.
        // - 7 for cp2 ETH/USDT.
        // Two tickers for cp1 are identical so we expect to receive 13 tickers in the strategy.
        // we will call the service 17 times as some replies ill be empty.
        final int numberOfUpdatesExpected = 13;
        final int numberOfServiceCallsExpected = 17;

        // Waiting for the service to have been called with all the test data (16).
        await().untilAsserted(() -> verify(marketDataService, atLeast(numberOfServiceCallsExpected)).getTicker(any()));

        // Checking that somme data have already been treated.
        // but not all as the flux should be asynchronous and single thread and strategy method method waits 1 second.
        assertTrue(strategy.getTickersUpdateReceived().size() > 0);
        assertTrue(strategy.getTickersUpdateReceived().size() <= numberOfUpdatesExpected);

        // Wait for the strategy to have received all the tickers.
        await().untilAsserted(() -> assertTrue(strategy.getTickersUpdateReceived().size() >= numberOfUpdatesExpected));
        final Iterator<TickerDTO> iterator = strategy.getTickersUpdateReceived().iterator();

        // =============================================================================================================
        // Test all values received by the strategy with update methods.

        // First value cp1 - 1.
        TickerDTO t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("1").compareTo(t.getLast()));

        // Second value cp2 - 10.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(t.getLast()));

        // Third value cp1 - 2.
        t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("2").compareTo(t.getLast()));

        // Fourth value cp2 - 20.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(t.getLast()));

        // Fifth value cp1 - 3.
        t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("3").compareTo(t.getLast()));

        // Sixth value cp2 - 30.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(t.getLast()));

        // Seventh value cp2 - 40.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(t.getLast()));

        // Eighth value cp1 - 4.
        t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("4").compareTo(t.getLast()));

        // Ninth value cp2 - 50.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("50").compareTo(t.getLast()));

        // Tenth value cp1 - 5.
        t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("5").compareTo(t.getLast()));

        // Eleventh value cp2 - 60.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("60").compareTo(t.getLast()));

        // Twelfth value cp1 - 6.
        t = iterator.next();
        assertEquals(cp1, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("6").compareTo(t.getLast()));

        // Thirteenth value cp2 - 70.
        t = iterator.next();
        assertEquals(cp2, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("70").compareTo(t.getLast()));

        // =============================================================================================================
        // Check data we have in the strategy.
        assertEquals(2, strategy.getLastTickers().size());
        // For CP1.
        final Optional<TickerDTO> lastTickerForCP1 = strategy.getLastTickerByCurrencyPair(cp1);
        assertTrue(lastTickerForCP1.isPresent());
        assertEquals(cp1, lastTickerForCP1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("6").compareTo(lastTickerForCP1.get().getLast()));
        // For CP2.
        final Optional<TickerDTO> lastTickerForCP2 = strategy.getLastTickerByCurrencyPair(cp2);
        assertTrue(lastTickerForCP2.isPresent());
        assertEquals(cp2, lastTickerForCP2.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("70").compareTo(lastTickerForCP2.get().getLast()));
    }

}
