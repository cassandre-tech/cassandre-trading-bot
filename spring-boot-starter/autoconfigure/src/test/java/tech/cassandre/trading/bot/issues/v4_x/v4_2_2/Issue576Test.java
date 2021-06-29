package tech.cassandre.trading.bot.issues.v4_x.v4_2_2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.marketdata.params.CurrencyPairsParam;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 576")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")
})
@Import(Issue576TestMock.class)
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue576Test extends BaseTest {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private MarketDataService marketService;

    @Captor
    ArgumentCaptor<CurrencyPairsParam> paramCaptor;

    @Test
    @DisplayName("Check tickers with two currency pairs")
    public void checkGetTickersForTwoCurrencyPairs() throws IOException {
        // Requested currency pairs.
        Set<CurrencyPairDTO> twoCurrencyPairs = ConcurrentHashMap.newKeySet();
        twoCurrencyPairs.add(ETH_BTC);
        twoCurrencyPairs.add(ETH_USDT);

        // Our strategy asks for two currency pairs.
        strategy.updateRequestedCurrencyPairs(twoCurrencyPairs);
        tickerFlux.update();
        verify(marketService).getTickers(paramCaptor.capture());
        assertEquals(2, paramCaptor.getAllValues().get(0).getCurrencyPairs().size());
    }

    @Test
    @DisplayName("Check tickers with one currency pair")
    public void checkGetTickersForOneCurrencyPair() throws IOException {
        // Requested currency pairs.
        Set<CurrencyPairDTO> oneCurrencyPair = ConcurrentHashMap.newKeySet();
        oneCurrencyPair.add(ETH_BTC);

        // Our strategy asks for one currency pair.
        strategy.updateRequestedCurrencyPairs(oneCurrencyPair);
        tickerFlux.update();
        verify(marketService).getTickers(paramCaptor.capture());
        assertEquals(1, paramCaptor.getAllValues().get(0).getCurrencyPairs().size());
    }

}
