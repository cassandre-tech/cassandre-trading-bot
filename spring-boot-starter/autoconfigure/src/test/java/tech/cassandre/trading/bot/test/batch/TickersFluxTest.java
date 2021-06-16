package tech.cassandre.trading.bot.test.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.test.batch.mocks.TickersFluxTestMock;
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
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Batch - Tickers flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@Import(TickersFluxTestMock.class)
public class TickersFluxTest extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private MarketDataService marketDataService;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // =============================================================================================================
        // Test asynchronous flux.

        // we will call the service 9 times.
        final int numberOfTickersExpected = 14;
        final int numberOfServiceCallsExpected = 10;

        // Waiting for the service to have been called with all the test data.
        await().untilAsserted(() -> verify(marketDataService, atLeast(numberOfServiceCallsExpected)).getTickers(any()));

        // Checking that somme data have already been treated.
        // but not all as the flux should be asynchronous and single thread and strategy method method waits 1 second.
        assertTrue(strategy.getTickersUpdatesReceived().size() > 0);
        assertTrue(strategy.getTickersUpdatesReceived().size() <= numberOfTickersExpected);

        // Wait for the strategy to have received all the tickers.
        await().untilAsserted(() -> assertTrue(strategy.getTickersUpdatesReceived().size() >= numberOfTickersExpected));
        final Iterator<TickerDTO> iterator = strategy.getTickersUpdatesReceived().iterator();

        // =============================================================================================================
        // Test all values received by the strategy with update methods.

        // First call.
        TickerDTO t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("1").compareTo(t.getLast()));
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(t.getLast()));

        // Second call.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("2").compareTo(t.getLast()));
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(t.getLast()));

        // Third call.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("3").compareTo(t.getLast()));
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(t.getLast()));

        // Fourth call.
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(t.getLast()));

        // Fifth call.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("4").compareTo(t.getLast()));
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("50").compareTo(t.getLast()));

        // Sixth call.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(t.getLast()));

        // Seventh call.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("5").compareTo(t.getLast()));
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("60").compareTo(t.getLast()));

        // Eighth call.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("6").compareTo(t.getLast()));

        // 9th call.
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("70").compareTo(t.getLast()));

        // =============================================================================================================
        // Check data we have in the strategy.
        assertEquals(2, strategy.getLastTickers().size());
        // For CP1.
        final Optional<TickerDTO> lastTickerForCP1 = strategy.getLastTickerByCurrencyPair(ETH_BTC);
        assertTrue(lastTickerForCP1.isPresent());
        assertEquals(ETH_BTC, lastTickerForCP1.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("6").compareTo(lastTickerForCP1.get().getLast()));
        // For CP2.
        final Optional<TickerDTO> lastTickerForCP2 = strategy.getLastTickerByCurrencyPair(ETH_USDT);
        assertTrue(lastTickerForCP2.isPresent());
        assertEquals(ETH_USDT, lastTickerForCP2.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("70").compareTo(lastTickerForCP2.get().getLast()));
    }

}
