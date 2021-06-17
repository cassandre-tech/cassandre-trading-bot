package tech.cassandre.trading.bot.issues.v4_x.v4_2_1;

import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class Issue558TestMock extends BaseMock {

    @Override
    public MarketDataService getXChangeMarketDataServiceMock() throws IOException {
        MarketDataService marketService = mock(MarketDataService.class);

        List<Ticker> tickers = new LinkedList<>();
        tickers.add(getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("1")));
        tickers.add(getGeneratedTicker(XCHANGE_BTC_ETH, new BigDecimal("2")));
        tickers.add(getGeneratedTicker(XCHANGE_BTC_USDT, new BigDecimal("3")));

        List<Ticker> emptyTickers = new LinkedList<>();

        // We use getTickers instead of getTicker.
        given(marketService.getTickers(any())).willReturn(tickers, emptyTickers);
        given(marketService.getTicker(any())).willThrow(new RuntimeException("getTicker() was called !"));
        return marketService;
    }

}
