package tech.cassandre.trading.bot.issues.v4_x.v4_1_0;

import org.knowm.xchange.dto.marketdata.Ticker;
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
public class Issue482TestMock extends BaseMock {

    @Override
    public MarketDataService getXChangeMarketDataServiceMock() throws IOException {
        MarketDataService marketService = mock(MarketDataService.class);

        // We don't use the getTickers method.
        given(marketService.getTickers(any())).willThrow(new NotAvailableFromExchangeException("Not available in test"));

        final BigDecimal value = new BigDecimal("1");
        // Replies for BTC/USDT.
        given(marketService
                .getTicker(XCHANGE_ETH_BTC))
                .willReturn(
                        // Ticker 1.
                        new Ticker.Builder()
                                .instrument(XCHANGE_ETH_BTC) // currency pair.
                                .open(value)            // open.
                                .last(value)            // last.
                                .bid(value)             // bid.
                                .ask(value)             // ask.
                                .high(value)            // high.
                                .low(value)             // low.
                                .vwap(value)            // wmap.
                                .volume(value)          // value.
                                .quoteVolume(value)     // quote volume.
                                .timestamp(null)        // timestamp.
                                .bidSize(value)         // bid size.
                                .askSize(value)         // ask size.
                                .build(),
                        // Ticker 2.
                        new Ticker.Builder()
                                .instrument(XCHANGE_ETH_BTC) // currency pair.
                                .open(value)            // open.
                                .last(value)            // last.
                                .bid(value)             // bid.
                                .ask(value)             // ask.
                                .high(value)            // high.
                                .low(value)             // low.
                                .vwap(value)            // wmap.
                                .volume(value)          // value.
                                .quoteVolume(value)     // quote volume.
                                .timestamp(null)        // timestamp.
                                .bidSize(value)         // bid size.
                                .askSize(value)         // ask size.
                                .build(),
                        // Ticker 3.
                        new Ticker.Builder()
                                .instrument(XCHANGE_ETH_BTC) // currency pair.
                                .open(value)            // open.
                                .last(value)            // last.
                                .bid(value)             // bid.
                                .ask(value)             // ask.
                                .high(value)            // high.
                                .low(value)             // low.
                                .vwap(value)            // wmap.
                                .volume(value)          // value.
                                .quoteVolume(value)     // quote volume.
                                .timestamp(null)        // timestamp.
                                .bidSize(value)         // bid size.
                                .askSize(value)         // ask size.
                                .build()
                );
        return marketService;
    }

}
