package tech.cassandre.trading.bot.integration.gemini;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.MarketService;

import java.time.ZonedDateTime;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

@SpringBootTest
@ActiveProfiles("schedule-disabled")
@TestPropertySource(properties = {
        "cassandre.trading.bot.exchange.driver-class-name=${GEMINI_NAME}",
        "cassandre.trading.bot.exchange.modes.sandbox=true",
        "cassandre.trading.bot.exchange.modes.dry=false",
        "cassandre.trading.bot.exchange.username=${GEMINI_USERNAME}",
        "cassandre.trading.bot.exchange.passphrase=${GEMINI_PASSPHRASE}",
        "cassandre.trading.bot.exchange.key=${GEMINI_KEY}",
        "cassandre.trading.bot.exchange.secret=${GEMINI_SECRET}",
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
@DisplayName("Gemini - Market service")
public class MarketServiceTest {

    @Autowired
    private MarketService marketService;

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
        assertNull(t.get().getOpen());
        // last.
        assertNotNull(t.get().getLast());
        assertTrue(t.get().getLast().compareTo(ZERO) > 0);
        // bid.
        assertNotNull(t.get().getBid());
        assertTrue(t.get().getBid().compareTo(ZERO) > 0);
        // ask.
        assertNotNull(t.get().getAsk());
        assertTrue(t.get().getAsk().compareTo(ZERO) > 0);
        // bidSize.
        assertNull(t.get().getBidSize());
        // askSize.
        assertNull(t.get().getAskSize());
        // timestamp.
        assertNotNull(t.get().getTimestamp());
        assertTrue(t.get().getTimestamp().isAfter(ZonedDateTime.now().minusHours(1)));
        assertTrue(t.get().getTimestamp().isBefore(ZonedDateTime.now().plusHours(1)));
    }

}
