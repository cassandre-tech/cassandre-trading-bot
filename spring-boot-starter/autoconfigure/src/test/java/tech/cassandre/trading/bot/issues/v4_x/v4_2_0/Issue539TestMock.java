package tech.cassandre.trading.bot.issues.v4_x.v4_2_0;

import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class Issue539TestMock extends BaseMock {

    @Override
    public MarketDataService getXChangeMarketDataServiceMock() throws IOException {
        MarketDataService marketService = mock(MarketDataService.class);

        // We don't use the getTickers method.
        given(marketService.getTickers(any())).willThrow(new NotAvailableFromExchangeException("Not available in test"));

        given(marketService.getTicker(XCHANGE_ETH_BTC))
                .willReturn(getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("1")))
                .willThrow(new RuntimeException("Raised issue539TestMock exception"))
                .willReturn(getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("2")));
        return marketService;
    }

}
