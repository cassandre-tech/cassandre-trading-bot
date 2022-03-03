package tech.cassandre.trading.bot.test.core.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.instrument.Instrument;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.test.core.batch.mocks.TickerFluxTestMock;
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
@DisplayName("Batch - Ticker flux")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@Import(TickerFluxTestMock.class)
public class TickerFluxTest extends BaseTest {

    @Autowired
    private MarketDataService marketDataService;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Check received data")
    public void checkReceivedData() {
        // we will call the service 9 times.
        final int numberOfTickersExpected = 14;
        final int numberOfServiceCallsExpected = 9;

        // Waiting for the service to have been called with all the test data.
        await().untilAsserted(() -> verify(marketDataService, atLeast(numberOfServiceCallsExpected)).getTicker((Instrument)any()));
        // Waiting for the strategy to have received all the tickers.
        await().untilAsserted(() -> assertTrue(strategy.getTickersUpdatesReceived().size() >= numberOfTickersExpected));

        // =============================================================================================================
        // Test all values received by the strategy with update methods.
        final Iterator<TickerDTO> iterator = strategy.getTickersUpdatesReceived().iterator();

        // Call 1.
        TickerDTO t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("1").compareTo(t.getLast()));
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(t.getLast()));

        // Call 2.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("2").compareTo(t.getLast()));
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(t.getLast()));

        // Call 3.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("3").compareTo(t.getLast()));
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(t.getLast()));

        // Call 4.
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(t.getLast()));

        // Call 5.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("4").compareTo(t.getLast()));
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("50").compareTo(t.getLast()));

        // Call 6.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(t.getLast()));

        // Call 7.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("5").compareTo(t.getLast()));
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("60").compareTo(t.getLast()));

        // Call 8.
        t = iterator.next();
        assertEquals(ETH_BTC, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("6").compareTo(t.getLast()));

        // Call 9.
        t = iterator.next();
        assertEquals(ETH_USDT, t.getCurrencyPair());
        assertEquals(0, new BigDecimal("70").compareTo(t.getLast()));

        // =============================================================================================================
        // Check data we have in the strategy.
        assertEquals(2, strategy.getLastTickers().size());
        // For ETH/BTC
        final Optional<TickerDTO> lastTickerForETHBTC = strategy.getLastTickerByCurrencyPair(ETH_BTC);
        assertTrue(lastTickerForETHBTC.isPresent());
        assertEquals(ETH_BTC, lastTickerForETHBTC.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("6").compareTo(lastTickerForETHBTC.get().getLast()));
        // For ETH/USDT.
        final Optional<TickerDTO> lastTickerForETHUSDT = strategy.getLastTickerByCurrencyPair(ETH_USDT);
        assertTrue(lastTickerForETHUSDT.isPresent());
        assertEquals(ETH_USDT, lastTickerForETHUSDT.get().getCurrencyPair());
        assertEquals(0, new BigDecimal("70").compareTo(lastTickerForETHUSDT.get().getLast()));
    }

}
