package tech.cassandre.trading.bot.test.batch;

import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TickerFluxTestMock extends BaseMock {

    @Override
    public MarketDataService getXChangeMarketDataServiceMock() throws IOException {
        MarketDataService marketService = mock(MarketDataService.class);

        // Replies for ETH / BTC.
        final Date date = new Date();
        given(marketService
                .getTicker(xChanceCP1))
                .willReturn(getGeneratedTicker(xChanceCP1, new BigDecimal("1")),        // Value 01.
                        getGeneratedTicker(xChanceCP1, new BigDecimal("2")),       // Value 03.
                        getGeneratedTicker(xChanceCP1, new BigDecimal("3")),            // Value 05.
                        null,                                                               // Value 07.
                        getGeneratedTicker(date, xChanceCP1, new BigDecimal("4")),      // Value 09.
                        getGeneratedTicker(date, xChanceCP1, new BigDecimal("40")),     // Value 11.
                        getGeneratedTicker(xChanceCP1, new BigDecimal("5")),            // Value 13.
                        getGeneratedTicker(xChanceCP1, new BigDecimal("6")),            // Value 15.
                        null
                );

        // Replies for ETH / USDT.
        given(marketService
                .getTicker(xChanceCP2))
                .willReturn(getGeneratedTicker(xChanceCP2, new BigDecimal("10")),       // Value 02.
                        getGeneratedTicker(xChanceCP2, new BigDecimal("20")),      // Value 04.
                        getGeneratedTicker(xChanceCP2, new BigDecimal("30")),           // Value 06.
                        getGeneratedTicker(xChanceCP2, new BigDecimal("40")),           // Value 08.
                        getGeneratedTicker(xChanceCP2, new BigDecimal("50")),           // Value 10.
                        null,                                                               // Value 12.
                        getGeneratedTicker(xChanceCP2, new BigDecimal("60")),           // Value 14.
                        null,                                                               // Value 16.
                        getGeneratedTicker(xChanceCP2, new BigDecimal("70"))            // Value 17.
                );
        return marketService;
    }

}
