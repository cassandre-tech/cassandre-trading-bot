package tech.cassandre.trading.bot.test.issues.v6_x.v6_0_0;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.dto.market.CandleDTO;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.test.util.junit.BaseTest.BTC_USDT;
import static tech.cassandre.trading.bot.test.util.junit.configuration.ConfigurationExtension.PARAMETER_EXCHANGE_DRY;

@SpringBootTest
@DisplayName("Github issue 938")
@Configuration({
        @Property(key = PARAMETER_EXCHANGE_DRY, value = "false")})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class Issue938Test {

    @Autowired
    private TestableCassandreStrategy strategy;

    @Test
    @DisplayName("Initialize() method on strategy")
    public void testCurrencySerialization() {
        await().untilAsserted(() -> assertTrue(strategy.isInitialized()));
    }

    @Test
    @DisplayName("Imported candles")
    public void testImportedCandles() {
        // We wait for initialized to be sure all data have been imported.
        await().untilAsserted(() -> assertTrue(strategy.isInitialized()));

        // 180 candles imported from:
        // candles-to-import-three-months-of-btc-usdt.csv
        // candles-to-import-three-months-of-eth-usdt.csv
        assertEquals(180, strategy.getImportedCandles().size());

        // Testing getImportedCandles by currency pair.
        List<CandleDTO> btcUsdtCandles = strategy.getImportedCandles(BTC_USDT);
        assertEquals(90, btcUsdtCandles.size());

        // Testing one value.
        final Optional<CandleDTO> firstValue = strategy.getImportedCandles(BTC_USDT).stream().findFirst();
        assertTrue(firstValue.isPresent());
        assertNotNull(firstValue.get().getTimestamp());
        assertEquals(0, new BigDecimal("46898.1").compareTo(firstValue.get().getOpen()));
        assertEquals(0, new BigDecimal("49322").compareTo(firstValue.get().getHigh()));
        assertEquals(0, new BigDecimal("46655.6").compareTo(firstValue.get().getLow()));
        assertEquals(0, new BigDecimal("48891.4").compareTo(firstValue.get().getClose()));
        assertEquals(0, new BigDecimal("7389.39809406").compareTo(firstValue.get().getVolume()));
    }

}
