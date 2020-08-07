package tech.cassandre.trading.bot.integration.kucoin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("schedule-disabled")
@TestPropertySource(properties = {
        "cassandre.trading.bot.exchange.name=${KUCOIN_NAME}",
        "cassandre.trading.bot.exchange.modes.sandbox=true",
        "cassandre.trading.bot.exchange.modes.dry=false",
        "cassandre.trading.bot.exchange.username=${KUCOIN_USERNAME}",
        "cassandre.trading.bot.exchange.passphrase=${KUCOIN_PASSPHRASE}",
        "cassandre.trading.bot.exchange.key=${KUCOIN_KEY}",
        "cassandre.trading.bot.exchange.secret=${KUCOIN_SECRET}",
        "cassandre.trading.bot.exchange.rates.account=100",
        "cassandre.trading.bot.exchange.rates.ticker=101",
        "cassandre.trading.bot.exchange.rates.trade=102",
        "testableStrategy.enabled=true",
        "invalidStrategy.enabled=false"
})
@DisplayName("Kucoin - Market service")
public class MarketServiceTest {

    /** Account service. */
    @Autowired
    private MarketService marketService;

    @Test
    @DisplayName("Get ticker")
    public void testGetTicker() {
        CurrencyPairDTO cp = new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC);
        Optional<TickerDTO> result = marketService.getTicker(cp);
        assertTrue(result.isPresent());
        result.ifPresent(t -> {
            // currencyPair.
            assertNotNull(t.getCurrencyPair());
            assertEquals(t.getCurrencyPair(), cp);
            // open.
            assertNull(t.getOpen());
            // last.
            assertNotNull(t.getLast());
            assertTrue(t.getLast().compareTo(BigDecimal.ZERO) > 0);
            // bid.
            assertNotNull(t.getBid());
            assertTrue(t.getBid().compareTo(BigDecimal.ZERO) > 0);
            // ask.
            assertNotNull(t.getAsk());
            assertTrue(t.getAsk().compareTo(BigDecimal.ZERO) > 0);
            // high.
            assertNotNull(t.getHigh());
            assertTrue(t.getHigh().compareTo(BigDecimal.ZERO) > 0);
            // low.
            assertNotNull(t.getLow());
            assertTrue(t.getLow().compareTo(BigDecimal.ZERO) > 0);
            // volume.
            assertNotNull(t.getVolume());
            assertTrue(t.getVolume().compareTo(BigDecimal.ZERO) > 0);
            // quote volume.
            assertNotNull(t.getQuoteVolume());
            assertTrue(t.getQuoteVolume().compareTo(BigDecimal.ZERO) > 0);
            // timestamp.
            assertNotNull(t.getTimestamp());
            assertTrue(t.getTimestamp().isAfter(ZonedDateTime.now().minusMinutes(1)));
            assertTrue(t.getTimestamp().isBefore(ZonedDateTime.now().plusMinutes(1)));
            // bidSize.
            assertNull(t.getBidSize());
            // askSize.
            assertNull(t.getAskSize());
        });
    }

}


