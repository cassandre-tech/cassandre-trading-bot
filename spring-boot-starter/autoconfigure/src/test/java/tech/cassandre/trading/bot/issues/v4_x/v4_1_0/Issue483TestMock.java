package tech.cassandre.trading.bot.issues.v4_x.v4_1_0;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class Issue483TestMock extends BaseMock {

    @Override
    public MarketDataService getXChangeMarketDataServiceMock() throws IOException {
        MarketDataService marketService = mock(MarketDataService.class);

        // Replies for BTC/USDT.
        CurrencyPair cp = new CurrencyPair(Currency.ETH, Currency.BTC);
        given(marketService
                .getTicker(cp))
                .willReturn(getGeneratedTicker(cp, new BigDecimal("1")));
        return marketService;
    }

}
