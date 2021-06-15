package tech.cassandre.trading.bot.test.batch.mocks;

import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TickerFluxTestMock extends BaseMock {

    @Override
    public MarketDataService getXChangeMarketDataServiceMock() throws IOException {
        MarketDataService marketService = mock(MarketDataService.class);

        // We don't use the getTickers method.
        given(marketService.getTickers(any())).willThrow(new NotAvailableFromExchangeException("Not available in test"));

        // Replies for ETH / BTC.
        final Date date = new Date();
        given(marketService
                .getTicker(XCHANGE_ETH_BTC))
                .willReturn(getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("1")),
                        getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("2")),
                        getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("3")),
                        null,
                        getGeneratedTicker(date, XCHANGE_ETH_BTC, new BigDecimal("4")),
                        getGeneratedTicker(date, XCHANGE_ETH_BTC, new BigDecimal("40")),
                        getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("5")),
                        getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("6")),
                        null
                );

        // Replies for ETH / USDT.
        given(marketService
                .getTicker(XCHANGE_ETH_USDT))
                .willReturn(getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("10")),
                        getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("20")),
                        getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("30")),
                        getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("40")),
                        getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("50")),
                        null,
                        getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("60")),
                        null,
                        getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("70"))
                );
        return marketService;
    }

}
