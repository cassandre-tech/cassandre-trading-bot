package tech.cassandre.trading.bot.test.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@DisplayName("Repository - Trade")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
public class TradeRepositoryTest extends BaseTest {

    @Autowired
    private TradeRepository tradeRepository;

    @Test
    @DisplayName("Check imported data")
    public void checkImportedTrades() {
        // Trades.
        final Iterator<Trade> trades = tradeRepository.findByOrderByTimestampAsc().iterator();
        assertEquals(10, tradeRepository.count());
        // Trade 01.
        Trade t = trades.next();
        assertEquals("BACKUP_TRADE_01", t.getId());
        assertEquals("BACKUP_OPEN_ORDER_02", t.getOrderId());
        assertEquals("BID", t.getType());
        assertEquals(0, t.getOriginalAmount().compareTo(new BigDecimal("20")));
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getPrice().compareTo(new BigDecimal("10")));
        assertEquals(createZonedDateTime("01-08-2020"), t.getTimestamp());
        assertEquals(0, t.getFeeAmount().compareTo(new BigDecimal("1")));
        assertEquals("USDT", t.getFeeCurrency());
        assertEquals(2, t.getPosition().getId());
        // Trade 02.
        t = trades.next();
        assertEquals("BACKUP_TRADE_02", t.getId());
        assertEquals("BACKUP_OPEN_ORDER_03", t.getOrderId());
        assertEquals("BID", t.getType());
        assertEquals(0, t.getOriginalAmount().compareTo(new BigDecimal("30")));
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getPrice().compareTo(new BigDecimal("20")));
        assertEquals(createZonedDateTime("02-08-2020"), t.getTimestamp());
        assertEquals(0, t.getFeeAmount().compareTo(new BigDecimal("2")));
        assertEquals("USDT", t.getFeeCurrency());
        // Trade 03.
        t = trades.next();
        assertEquals("BACKUP_TRADE_03", t.getId());
        assertEquals("BACKUP_OPEN_ORDER_04", t.getOrderId());
        assertEquals("BID", t.getType());
        assertEquals(0, t.getOriginalAmount().compareTo(new BigDecimal("40")));
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getPrice().compareTo(new BigDecimal("30")));
        assertEquals(createZonedDateTime("03-08-2020"), t.getTimestamp());
        assertEquals(0, t.getFeeAmount().compareTo(new BigDecimal("3")));
        assertEquals("USDT", t.getFeeCurrency());
        assertEquals(4, t.getPosition().getId());
        // Trade 04.
        t = trades.next();
        assertEquals("BACKUP_TRADE_04", t.getId());
        assertEquals("BACKUP_OPEN_ORDER_05", t.getOrderId());
        assertEquals("ASK", t.getType());
        assertEquals(0, t.getOriginalAmount().compareTo(new BigDecimal("40")));
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getPrice().compareTo(new BigDecimal("40")));
        assertEquals(createZonedDateTime("04-08-2020"), t.getTimestamp());
        assertEquals(0, t.getFeeAmount().compareTo(new BigDecimal("4")));
        assertEquals("USDT", t.getFeeCurrency());
        assertEquals(4, t.getPosition().getId());
        // Trade 05.
        t = trades.next();
        assertEquals("BACKUP_TRADE_05", t.getId());
        assertEquals("BACKUP_OPEN_ORDER_06", t.getOrderId());
        assertEquals("ASK", t.getType());
        assertEquals(0, t.getOriginalAmount().compareTo(new BigDecimal("50")));
        assertEquals("ETH/USD", t.getCurrencyPair());
        assertEquals(0, t.getPrice().compareTo(new BigDecimal("50")));
        assertEquals(createZonedDateTime("05-08-2020"), t.getTimestamp());
        assertEquals(0, t.getFeeAmount().compareTo(new BigDecimal("5")));
        assertEquals("USD", t.getFeeCurrency());
        assertNull(t.getPosition());
    }

}
