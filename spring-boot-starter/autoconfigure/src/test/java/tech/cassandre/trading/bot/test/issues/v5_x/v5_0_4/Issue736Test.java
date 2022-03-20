package tech.cassandre.trading.bot.test.issues.v5_x.v5_0_4;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.repository.ImportedTickerRepository;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.BTC_USDT;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.ETH_USDT;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 736")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue736Test {

    @Autowired
    private ImportedTickerRepository importedTickerRepository;

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Initialize() method on strategy")
    public void testCurrencySerialization() {
        await().untilAsserted(() -> assertTrue(strategy.isInitialized()));
    }

    @Test
    @DisplayName("Imported tickers")
    public void testImportedTickers() {
        // We wait for initialized to be sure all data have been imported.
        await().untilAsserted(() -> assertTrue(strategy.isInitialized()));

        // 5 tickers imported from tickers-to-import-1.csv & tickers-to-import-2.csv.
        assertEquals(5, importedTickerRepository.count());

        // =============================================================================================================
        // BTC/USDT.
        // Ticker 1 : BTC/USDT - 0.00000001 - 1508546000 (Saturday 21 October 2017 00:33:20)
        // Ticker 2 : BTC/USDT - 1.00000001 - 1508446000 (Thursday 19 October 2017 20:46:40)
        // Order expected: ticker 2, ticker 1.
        List<TickerDTO> btcUsdtTickers = strategy.getImportedTickers(BTC_USDT);
        assertEquals(2, btcUsdtTickers.size());

        // Ticker 1
        TickerDTO ticker1 = btcUsdtTickers.get(0);
        assertNotNull(ticker1);
        assertEquals(BTC_USDT, ticker1.getCurrencyPair());
        assertEquals(0, new BigDecimal("1.00000001").compareTo(ticker1.getOpen()));
        assertEquals(0, new BigDecimal("1.00000002").compareTo(ticker1.getLast()));
        assertEquals(0, new BigDecimal("1.00000003").compareTo(ticker1.getBid()));
        assertEquals(0, new BigDecimal("1.00000004").compareTo(ticker1.getAsk()));
        assertEquals(0, new BigDecimal("1.00000005").compareTo(ticker1.getHigh()));
        assertEquals(0, new BigDecimal("1.00000006").compareTo(ticker1.getLow()));
        assertEquals(0, new BigDecimal("1.00000007").compareTo(ticker1.getVwap()));
        assertEquals(0, new BigDecimal("1.00000008").compareTo(ticker1.getVolume()));
        assertEquals(0, new BigDecimal("1.00000009").compareTo(ticker1.getQuoteVolume()));
        assertEquals(0, new BigDecimal("1.00000010").compareTo(ticker1.getBidSize()));
        assertEquals(0, new BigDecimal("1.00000011").compareTo(ticker1.getAskSize()));
        assertEquals(19, ticker1.getTimestamp().getDayOfMonth());
        assertEquals(Month.OCTOBER, ticker1.getTimestamp().getMonth());
        assertEquals(2017, ticker1.getTimestamp().getYear());

        // Ticker 2
        TickerDTO ticker2 = btcUsdtTickers.get(1);
        assertNotNull(ticker2);
        assertEquals(BTC_USDT, ticker2.getCurrencyPair());
        assertEquals(0, new BigDecimal("0.00000001").compareTo(ticker2.getOpen()));
        assertEquals(0, new BigDecimal("0.00000002").compareTo(ticker2.getLast()));
        assertEquals(0, new BigDecimal("0.00000003").compareTo(ticker2.getBid()));
        assertEquals(0, new BigDecimal("0.00000004").compareTo(ticker2.getAsk()));
        assertEquals(0, new BigDecimal("0.00000005").compareTo(ticker2.getHigh()));
        assertEquals(0, new BigDecimal("0.00000006").compareTo(ticker2.getLow()));
        assertEquals(0, new BigDecimal("0.00000007").compareTo(ticker2.getVwap()));
        assertEquals(0, new BigDecimal("0.00000008").compareTo(ticker2.getVolume()));
        assertEquals(0, new BigDecimal("0.00000009").compareTo(ticker2.getQuoteVolume()));
        assertEquals(0, new BigDecimal("0.00000010").compareTo(ticker2.getBidSize()));
        assertEquals(0, new BigDecimal("0.00000011").compareTo(ticker2.getAskSize()));
        assertEquals(21, ticker2.getTimestamp().getDayOfMonth());
        assertEquals(Month.OCTOBER, ticker2.getTimestamp().getMonth());
        assertEquals(2017, ticker2.getTimestamp().getYear());

        // =============================================================================================================
        // ETH/USDT.
        // Ticker 1 : ETH/USDT - 2.00000001 - 1508346000 (Wednesday 18 October 2017 17:00:00)
        // Ticker 2 : ETH/USDT - 1 BID_SIZE - 1508746000 (Monday 23 October 2017 08:06:40)
        // Ticker 3 : ETH/USDT - 2 ASK_SIZE - 1508646000 (Sunday 22 October 2017 04:20:00)
        // Order expected: ticker 1, ticker 3, ticker 2.
        List<TickerDTO> ethUsdtTickers = strategy.getImportedTickers(ETH_USDT);
        assertEquals(3, ethUsdtTickers.size());

        // Ticker 3
        TickerDTO ticker3 = ethUsdtTickers.get(0);
        assertNotNull(ticker3);
        assertEquals(ETH_USDT, ticker3.getCurrencyPair());
        assertEquals(0, new BigDecimal("2.00000001").compareTo(ticker3.getOpen()));
        assertEquals(0, new BigDecimal("2.00000002").compareTo(ticker3.getLast()));
        assertEquals(0, new BigDecimal("2.00000003").compareTo(ticker3.getBid()));
        assertEquals(0, new BigDecimal("2.00000004").compareTo(ticker3.getAsk()));
        assertEquals(0, new BigDecimal("2.00000005").compareTo(ticker3.getHigh()));
        assertEquals(0, new BigDecimal("2.00000006").compareTo(ticker3.getLow()));
        assertEquals(0, new BigDecimal("2.00000007").compareTo(ticker3.getVwap()));
        assertEquals(0, new BigDecimal("2.00000008").compareTo(ticker3.getVolume()));
        assertEquals(0, new BigDecimal("2.00000009").compareTo(ticker3.getQuoteVolume()));
        assertEquals(0, new BigDecimal("2.00000010").compareTo(ticker3.getBidSize()));
        assertEquals(0, new BigDecimal("2.00000011").compareTo(ticker3.getAskSize()));
        assertEquals(18, ticker3.getTimestamp().getDayOfMonth());
        assertEquals(Month.OCTOBER, ticker3.getTimestamp().getMonth());
        assertEquals(2017, ticker3.getTimestamp().getYear());

        // Ticker 4
        TickerDTO ticker4 = ethUsdtTickers.get(1);
        assertNotNull(ticker4);
        assertEquals(ETH_USDT, ticker4.getCurrencyPair());
        assertNull(ticker4.getOpen());
        assertNull(ticker4.getLast());
        assertNull(ticker4.getBid());
        assertNull(ticker4.getAsk());
        assertNull(ticker4.getHigh());
        assertNull(ticker4.getLow());
        assertNull(ticker4.getVwap());
        assertNull(ticker4.getOpen());
        assertNull(ticker4.getVolume());
        assertNull(ticker4.getQuoteVolume());
        assertNull(ticker4.getBidSize());
        assertEquals(0, new BigDecimal("2").compareTo(ticker4.getAskSize()));
        assertEquals(22, ticker4.getTimestamp().getDayOfMonth());
        assertEquals(Month.OCTOBER, ticker4.getTimestamp().getMonth());
        assertEquals(2017, ticker4.getTimestamp().getYear());

        // Ticker 5
        TickerDTO ticker5 = ethUsdtTickers.get(2);
        assertNotNull(ticker5);
        assertEquals(ETH_USDT, ticker5.getCurrencyPair());
        assertNull(ticker5.getOpen());
        assertNull(ticker5.getLast());
        assertNull(ticker5.getBid());
        assertNull(ticker5.getAsk());
        assertNull(ticker5.getHigh());
        assertNull(ticker5.getLow());
        assertNull(ticker5.getVwap());
        assertNull(ticker5.getOpen());
        assertNull(ticker5.getVolume());
        assertNull(ticker5.getQuoteVolume());
        assertEquals(0, new BigDecimal("1").compareTo(ticker5.getBidSize()));
        assertNull(ticker5.getAskSize());
        assertEquals(23, ticker5.getTimestamp().getDayOfMonth());
        assertEquals(Month.OCTOBER, ticker5.getTimestamp().getMonth());
        assertEquals(2017, ticker5.getTimestamp().getYear());

        // Test all tickers order.
        // Order: ETH/USDT-1, BTC/USDT-2, BTC/USDT-1, ETH/USDT-3, ETH/USDT-2.
        List<TickerDTO> allTickers = strategy.getImportedTickers();
        assertEquals(5, allTickers.size());

        ZonedDateTime ticker1Date = allTickers.get(0).getTimestamp();
        assertEquals(18, ticker1Date.getDayOfMonth());
        assertEquals(Month.OCTOBER, ticker1Date.getMonth());
        assertEquals(2017, ticker1Date.getYear());

        ZonedDateTime ticker2Date = allTickers.get(1).getTimestamp();
        assertEquals(19, ticker2Date.getDayOfMonth());
        assertEquals(Month.OCTOBER, ticker2Date.getMonth());
        assertEquals(2017, ticker2Date.getYear());

        ZonedDateTime ticker3Date = allTickers.get(2).getTimestamp();
        assertEquals(21, ticker3Date.getDayOfMonth());
        assertEquals(Month.OCTOBER, ticker3Date.getMonth());
        assertEquals(2017, ticker3Date.getYear());

        ZonedDateTime ticker4Date = allTickers.get(3).getTimestamp();
        assertEquals(22, ticker4Date.getDayOfMonth());
        assertEquals(Month.OCTOBER, ticker4Date.getMonth());
        assertEquals(2017, ticker4Date.getYear());

        ZonedDateTime ticker5Date = allTickers.get(4).getTimestamp();
        assertEquals(23, ticker5Date.getDayOfMonth());
        assertEquals(Month.OCTOBER, ticker5Date.getMonth());
        assertEquals(2017, ticker5Date.getYear());
    }

}
