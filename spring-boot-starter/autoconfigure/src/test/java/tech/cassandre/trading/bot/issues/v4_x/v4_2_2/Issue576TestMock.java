package tech.cassandre.trading.bot.issues.v4_x.v4_2_2;

import org.knowm.xchange.service.marketdata.MarketDataService;
import org.springframework.boot.test.context.TestConfiguration;
import tech.cassandre.trading.bot.test.util.junit.BaseMock;

import java.io.IOException;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class Issue576TestMock extends BaseMock {

    @Override
    public MarketDataService getXChangeMarketDataServiceMock() throws IOException {
        return mock(MarketDataService.class);
    }

}
