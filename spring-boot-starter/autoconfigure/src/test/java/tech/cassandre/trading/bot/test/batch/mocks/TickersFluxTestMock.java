package tech.cassandre.trading.bot.test.batch.mocks;

import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class TickersFluxTestMock extends BaseMock {

    @Override
    public MarketDataService getXChangeMarketDataServiceMock() throws IOException {
        MarketDataService marketService = mock(MarketDataService.class);

        // We prepare the replies.
        final Date date = new Date();
        List<Ticker> reply01 = new LinkedList<>();
        reply01.add(getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("1")));
        reply01.add(getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("10")));
        List<Ticker> reply02 = new LinkedList<>();
        reply02.add(getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("2")));
        reply02.add(getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("20")));
        List<Ticker> reply03 = new LinkedList<>();
        reply03.add(getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("3")));
        reply03.add(getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("30")));
        List<Ticker> reply04 = new LinkedList<>();
        reply04.add(getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("40")));
        List<Ticker> reply05 = new LinkedList<>();
        reply05.add(getGeneratedTicker(date, XCHANGE_ETH_BTC, new BigDecimal("4")));
        reply05.add(getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("50")));
        List<Ticker> reply06 = new LinkedList<>();
        reply06.add(getGeneratedTicker(date, XCHANGE_ETH_BTC, new BigDecimal("40")));
        List<Ticker> reply07 = new LinkedList<>();
        reply07.add(getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("5")));
        reply07.add(getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("60")));
        List<Ticker> reply08 = new LinkedList<>();
        reply08.add(getGeneratedTicker(XCHANGE_ETH_BTC, new BigDecimal("6")));
        List<Ticker> reply09 = new LinkedList<>();
        reply09.add(getGeneratedTicker(XCHANGE_ETH_USDT, new BigDecimal("70")));

        // We use the getTickers method.
        //noinspection unchecked
        given(marketService.getTickers(any()))
                .willReturn(reply01,
                        reply02,
                        reply03,
                        reply04,
                        reply05,
                        reply06,
                        reply07,
                        reply08,
                        reply09,
                        new LinkedList<>());

        // We don't use the getTicker method.
        given(marketService.getTicker(any())).willThrow(new NotAvailableFromExchangeException("Not available in test"));

        return marketService;
    }

}
