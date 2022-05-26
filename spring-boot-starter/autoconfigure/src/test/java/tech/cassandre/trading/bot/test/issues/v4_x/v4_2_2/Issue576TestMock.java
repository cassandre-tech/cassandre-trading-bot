package tech.cassandre.trading.bot.test.issues.v4_x.v4_2_2;

import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import static org.mockito.Mockito.mock;

@TestConfiguration
@SuppressWarnings("unused")
public class Issue576TestMock extends BaseMock {

    @Override
    public MarketDataService getXChangeMarketDataServiceMock() {
        return mock(MarketDataService.class);
    }

}
