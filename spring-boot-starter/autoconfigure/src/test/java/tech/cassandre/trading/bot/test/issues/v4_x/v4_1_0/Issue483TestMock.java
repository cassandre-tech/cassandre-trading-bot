package tech.cassandre.trading.bot.test.issues.v4_x.v4_1_0;

import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
@SuppressWarnings("unused")
public class Issue483TestMock extends BaseMock {

    @Override
    public MarketDataService getXChangeMarketDataServiceMock() throws IOException {
        MarketDataService marketService = mock(MarketDataService.class);

        // Replies for ETH/BTC.
        given(marketService
                .getTicker(XCHANGE_ETH_BTC))
                .willReturn(getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("1")));
        return marketService;
    }

}
