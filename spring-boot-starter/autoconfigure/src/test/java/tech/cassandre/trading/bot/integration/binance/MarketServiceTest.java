package tech.cassandre.trading.bot.integration.binance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.math.BigDecimal.ZERO;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

@SpringBootTest
@ActiveProfiles("schedule-disabled")
@TestPropertySource(properties = {
        "cassandre.trading.bot.exchange.driver-class-name=${BINANCE_NAME}",
        "cassandre.trading.bot.exchange.modes.sandbox=false",
        "cassandre.trading.bot.exchange.modes.dry=false",
        "cassandre.trading.bot.exchange.username=${BINANCE_USERNAME}",
        "cassandre.trading.bot.exchange.passphrase=${BINANCE_PASSPHRASE}",
        "cassandre.trading.bot.exchange.key=${BINANCE_KEY}",
        "cassandre.trading.bot.exchange.secret=${BINANCE_SECRET}",
        "cassandre.trading.bot.exchange.rates.account=100",
        "cassandre.trading.bot.exchange.rates.ticker=101",
        "cassandre.trading.bot.exchange.rates.trade=102",
        "cassandre.trading.bot.database.datasource.driver-class-name=org.hsqldb.jdbc.JDBCDriver",
        "cassandre.trading.bot.database.datasource.url=jdbc:hsqldb:mem:cassandre-database;shutdown=true",
        "cassandre.trading.bot.database.datasource.username=sa",
        "cassandre.trading.bot.database.datasource.password=",
        "testableStrategy.enabled=true",
        "invalidStrategy.enabled=false"
})
@DisplayName("Binance - Market service")
public class MarketServiceTest extends BaseTest {

    @Autowired
    private TickerFlux tickerFlux;

    @Autowired
    private MarketService marketService;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @Tag("integration")
    @DisplayName("Check get ticker")
    public void checkGetTicker() {
        CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);
        Optional<TickerDTO> t = marketService.getTicker(cp);
        assertTrue(t.isPresent());
        // currencyPair.
        assertNotNull(t.get().getCurrencyPair());
        assertEquals(t.get().getCurrencyPair(), cp);
        // open.
        assertNotNull(t.get().getOpen());
        assertTrue(t.get().getOpen().compareTo(ZERO) > 0);
        // last.
        assertNotNull(t.get().getLast());
        assertTrue(t.get().getLast().compareTo(ZERO) > 0);
        // bid.
        assertNotNull(t.get().getBid());
        assertTrue(t.get().getBid().compareTo(ZERO) > 0);
        // ask.
        assertNotNull(t.get().getAsk());
        assertTrue(t.get().getAsk().compareTo(ZERO) > 0);
        // high.
        assertNotNull(t.get().getHigh());
        assertTrue(t.get().getHigh().compareTo(ZERO) > 0);
        // low.
        assertNotNull(t.get().getLow());
        assertTrue(t.get().getLow().compareTo(ZERO) > 0);
        // volume.
        assertNotNull(t.get().getVolume());
        assertTrue(t.get().getVolume().compareTo(ZERO) > 0);
        // quote volume.
        assertNotNull(t.get().getQuoteVolume());
        assertTrue(t.get().getQuoteVolume().compareTo(ZERO) > 0);
        // timestamp.
        assertNotNull(t.get().getTimestamp());
    }

    @Test
    @Tag("integration")
    @DisplayName("Check ticker flux")
    public void checkTickerFlux() throws InterruptedException {
        tickerFlux.update();
        TimeUnit.SECONDS.sleep(WAITING_TIME_IN_SECONDS);
        tickerFlux.update();
        // We should have two tickers received by the strategy
        await().untilAsserted(() -> assertEquals(2, strategy.getTickersUpdatesReceived().size()));
    }

}
