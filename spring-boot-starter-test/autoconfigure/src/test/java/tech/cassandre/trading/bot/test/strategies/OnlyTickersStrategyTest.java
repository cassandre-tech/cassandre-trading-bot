package tech.cassandre.trading.bot.test.strategies;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.test.mock.TickerFluxMock;
import tech.cassandre.trading.bot.test.util.BaseTest;
import tech.cassandre.trading.bot.test.util.OnlyTickersStrategy;

import java.math.BigDecimal;
import java.util.Map;

import static org.awaitility.Awaitility.with;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

@SpringBootTest(properties = {
        "ONLY_TICKERS_STRATEGY_ENABLED=true",
        "ONLY_ORDERS_STRATEGY_ENABLED=false",
        "ONLY_POSITIONS_STRATEGY_ENABLED=false"
})
@Import(TickerFluxMock.class)
@DisplayName("Only tickers strategy test")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class OnlyTickersStrategyTest extends BaseTest {

    @Autowired
    private TickerFluxMock tickerFluxMock;

    @Autowired
    private OnlyTickersStrategy strategy;

    @Test
    @DisplayName("Check received tickers with one flux")
    public void checkReceivedTickersWithOneFlux() {
        // There are four files and only three currency pairs are requested by the strategy.
        // - candles-for-backtesting-BTC-USDT.csv   -   Requested (7 lines).
        // - candles-for-backtesting-ETH-BTC.csv    -   Not requested.
        // - candles-for-backtesting-ETH-USDT.csv   -   Requested (5 lines).
        // - candles-for-backtesting-KCS-USDT.csv   -   Requested (3 lines).

        // Check if flux is not done.
        assertFalse(tickerFluxMock.getMarketServiceBacktestingImplementation().isFluxDone(new CurrencyPairDTO(ETH, USDT)));

        // As tickers-BTC-USDT.tsv has the most line, we should wait to have 7 replies.
        with().await().untilAsserted(() -> assertEquals(7, strategy.getTickersReceived().size()));

        // Check if flux is done.
        assertTrue(tickerFluxMock.getMarketServiceBacktestingImplementation().isFluxDone(new CurrencyPairDTO(ETH, USDT)));
    }

    @Test
    @DisplayName("Check received tickers")
    public void checkReceivedTickers() {
        // There are four files and only three currency pairs are requested by the strategy.
        // - candles-for-backtesting-BTC-USDT.csv   -   Requested (7 lines).
        // - candles-for-backtesting-ETH-BTC.csv    -   Not requested.
        // - candles-for-backtesting-ETH-USDT.csv   -   Requested (5 lines).
        // - candles-for-backtesting-KCS-USDT.csv   -   Requested (3 lines).

        // Check if flux is not done.
        assertFalse(tickerFluxMock.getMarketServiceBacktestingImplementation().isFluxDone());

        // As tickers-BTC-USDT.tsv has the most line, we should wait to have 7 replies.
        with().await().untilAsserted(() -> assertEquals(7, strategy.getTickersReceived().size()));

        // Check if flux is done.
        assertTrue(tickerFluxMock.getMarketServiceBacktestingImplementation().isFluxDone());

        // =============================================================================================================
        // First reply.
        // BTC/USDT - 1508371200	10000	10000	10000	10000	10000	10000
        // ETH-BTC  - Nothing
        // ETH-USDT - 1508371200	1000	1000	1000	1000	1000	1000
        // KCS-USDT - "1601596800","0.8494","0.85652","0.87","0.82001","6402298.90377638","5396388.7386519256337"
        final Map<CurrencyPairDTO, TickerDTO> reply01 = strategy.getTickersReceived().get(1L);
        // BTC/USDT.
        TickerDTO btcUsdtTicker01 = reply01.get(BTC_USDT);
        System.out.println("==> " + btcUsdtTicker01);
        assertNotNull(btcUsdtTicker01);
        assertEquals(0, new BigDecimal("10000").compareTo(btcUsdtTicker01.getLast()));
        // ETH-BTC.
        TickerDTO ethBtcTicker01 = reply01.get(ETH_BTC);
        assertNull(ethBtcTicker01);
        // ETH-USDT.
        TickerDTO ethUsdtTicker01 = reply01.get(ETH_USDT);
        assertNotNull(ethUsdtTicker01);
        assertEquals(0, new BigDecimal("1000").compareTo(ethUsdtTicker01.getLast()));
//        assertFalse(tickerFluxMock.isFluxDone(ETH_USDT));
        // KCS-USDT.
        TickerDTO kcsUsdtTicker01 = reply01.get(KCS_USDT);
        assertNotNull(kcsUsdtTicker01);
        assertEquals(0, new BigDecimal("0.85652").compareTo(kcsUsdtTicker01.getLast()));
//        assertFalse(tickerFluxMock.isFluxDone(KCS_USDT));

        // =============================================================================================================
        // Second reply.
        // BTC/USDT - 1508457600	20000	20000	20000	20000	20000	20000
        // ETH-BTC  - Nothing
        // ETH-USDT - 1508457600	5000	5000	5000	5000	5000	5000
        // KCS-USDT - "1601683200","0.85653","0.84261","0.88888","0.82","7349493.47425826","6292644.8212960955051"
        final Map<CurrencyPairDTO, TickerDTO> reply02 = strategy.getTickersReceived().get(2L);
        // BTC/USDT.
        TickerDTO btcUsdtTicker02 = reply02.get(BTC_USDT);
        assertNotNull(btcUsdtTicker02);
        assertEquals(0, new BigDecimal("20000").compareTo(btcUsdtTicker02.getLast()));
        // ETH-BTC.
        TickerDTO ethBtcTicker02 = reply02.get(ETH_BTC);
        assertNull(ethBtcTicker02);
        // ETH-USDT.
        TickerDTO ethUsdtTicker02 = reply02.get(ETH_USDT);
        assertNotNull(ethUsdtTicker02);
        assertEquals(0, new BigDecimal("5000").compareTo(ethUsdtTicker02.getLast()));
        // KCS-USDT.
        TickerDTO kcsUsdtTicker02 = reply02.get(KCS_USDT);
        assertNotNull(kcsUsdtTicker02);
        assertEquals(0, new BigDecimal("0.84261").compareTo(kcsUsdtTicker02.getLast()));

        // =============================================================================================================
        // Third reply.
        // BTC/USDT - 1508803200	50000	50000	50000	50000	50000	50000
        // ETH-BTC  - Nothing
        // ETH-USDT - 1508803200	6000	6000	6000	6000	6000	6000
        // KCS-USDT - "1601769600","0.84251","0.83751","0.8506","0.823","7306874.10063719","6158682.638871191526"
        final Map<CurrencyPairDTO, TickerDTO> reply03 = strategy.getTickersReceived().get(3L);
        // BTC/USDT.
        TickerDTO btcUsdtTicker03 = reply03.get(BTC_USDT);
        assertNotNull(btcUsdtTicker03);
        assertEquals(0, new BigDecimal("50000").compareTo(btcUsdtTicker03.getLast()));
        // ETH-BTC.
        TickerDTO ethBtcTicker03 = reply03.get(ETH_BTC);
        assertNull(ethBtcTicker03);
        // ETH-USDT.
        TickerDTO ethUsdtTicker03 = reply03.get(ETH_USDT);
        assertNotNull(ethUsdtTicker03);
        assertEquals(0, new BigDecimal("6000").compareTo(ethUsdtTicker03.getLast()));
        // KCS-USDT.
        TickerDTO kcsUsdtTicker03 = reply03.get(KCS_USDT);
        assertNotNull(kcsUsdtTicker03);
        assertEquals(0, new BigDecimal("0.83751").compareTo(kcsUsdtTicker03.getLast()));

        // =============================================================================================================
        // Fourth reply.
        // BTC/USDT - 1508803201	30000	30000	30000	30000	30000	30000
        // ETH-BTC  - Nothing
        // ETH-USDT - 1508803201	70000	70000	70000	70000	70000	70000
        // KCS-USDT - No more data
        final Map<CurrencyPairDTO, TickerDTO> reply04 = strategy.getTickersReceived().get(4L);
        // BTC/USDT.
        TickerDTO btcUsdtTicker04 = reply04.get(BTC_USDT);
        assertNotNull(btcUsdtTicker04);
        assertEquals(0, new BigDecimal("30000").compareTo(btcUsdtTicker04.getLast()));
        // ETH-BTC.
        TickerDTO ethBtcTicker04 = reply04.get(ETH_BTC);
        assertNull(ethBtcTicker04);
        // ETH-USDT.
        TickerDTO ethUsdtTicker04 = reply04.get(ETH_USDT);
        assertNotNull(ethUsdtTicker04);
        assertEquals(0, new BigDecimal("70000").compareTo(ethUsdtTicker04.getLast()));
        // KCS-USDT.
        TickerDTO kcsUsdtTicker04 = reply04.get(KCS_USDT);
        assertNull(kcsUsdtTicker04);

        // =============================================================================================================
        // Fifth reply.
        // BTC/USDT - 1508803202	40000	40000	40000	40000	40000	40000
        // ETH-BTC  - Nothing
        // ETH-USDT - 1508803202	10000	10000	10000	10000	10000	10000
        // KCS-USDT - No more data
        final Map<CurrencyPairDTO, TickerDTO> reply05 = strategy.getTickersReceived().get(5L);
        // BTC/USDT.
        TickerDTO btcUsdtTicker05 = reply05.get(BTC_USDT);
        assertNotNull(btcUsdtTicker05);
        assertEquals(0, new BigDecimal("40000").compareTo(btcUsdtTicker05.getLast()));
        // ETH-BTC.
        TickerDTO ethBtcTicker05 = reply05.get(ETH_BTC);
        assertNull(ethBtcTicker05);
        // ETH-USDT.
        TickerDTO ethUsdtTicker05 = reply05.get(ETH_USDT);
        assertNotNull(ethUsdtTicker05);
        assertEquals(0, new BigDecimal("10000").compareTo(ethUsdtTicker05.getLast()));
        // KCS-USDT.
        TickerDTO kcsUsdtTicker05 = reply04.get(KCS_USDT);
        assertNull(kcsUsdtTicker05);

        // =============================================================================================================
        // Sixth reply.
        // BTC/USDT - 1508803203	70000	70000	70000	70000	70000	70000
        // ETH-BTC  - Nothing
        // ETH-USDT - No more data
        // KCS-USDT - No more data
        final Map<CurrencyPairDTO, TickerDTO> reply06 = strategy.getTickersReceived().get(6L);
        // BTC/USDT.
        TickerDTO btcUsdtTicker06 = reply06.get(BTC_USDT);
        assertNotNull(btcUsdtTicker06);
        assertEquals(0, new BigDecimal("70000").compareTo(btcUsdtTicker06.getLast()));
        // ETH-BTC.
        TickerDTO ethBtcTicker06 = reply06.get(ETH_BTC);
        assertNull(ethBtcTicker06);
        // ETH-USDT.
        TickerDTO ethUsdtTicker06 = reply06.get(ETH_USDT);
        assertNull(ethUsdtTicker06);
        // KCS-USDT.
        TickerDTO kcsUsdtTicker06 = reply06.get(KCS_USDT);
        assertNull(kcsUsdtTicker06);

        // =============================================================================================================
        // Seventh reply.
        // BTC/USDT - 1508803204	25000	25000	25000	25000	25000	25000
        // ETH-BTC  - Nothing
        // ETH-USDT - No more data
        // KCS-USDT - No more data
        final Map<CurrencyPairDTO, TickerDTO> reply07 = strategy.getTickersReceived().get(7L);
        // BTC/USDT.
        TickerDTO btcUsdtTicker07 = reply07.get(BTC_USDT);
        assertNotNull(btcUsdtTicker07);
        assertEquals(0, new BigDecimal("25000").compareTo(btcUsdtTicker07.getLast()));
        // ETH-BTC.
        TickerDTO ethBtcTicker07 = reply07.get(ETH_BTC);
        assertNull(ethBtcTicker07);
        // ETH-USDT.
        TickerDTO ethUsdtTicker07 = reply07.get(ETH_USDT);
        assertNull(ethUsdtTicker07);
        // KCS-USDT.
        TickerDTO kcsUsdtTicker07 = reply07.get(KCS_USDT);
        assertNull(kcsUsdtTicker07);
    }

}
