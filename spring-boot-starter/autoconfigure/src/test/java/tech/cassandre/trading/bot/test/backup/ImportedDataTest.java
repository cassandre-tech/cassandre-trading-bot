package tech.cassandre.trading.bot.test.backup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS;


@SpringBootTest
@DisplayName("Backup - Imported data")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql"),
        @Property(key = "spring.jpa.hibernate.ddl-auto", value = "create-drop")
})
@DirtiesContext(classMode = BEFORE_CLASS)
public class ImportedDataTest extends BaseTest {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Test
    @DisplayName("Check trades in database from imported data")
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
    }

    @Test
    @DisplayName("Check positions in database from imported data")
    public void checkImportedPositions() {
        // Positions.
        final Iterator<Position> positions = positionRepository.findAll().iterator();
        assertEquals(5, positionRepository.count());
        // Position 1.
        Position p = positions.next();
        assertEquals(1, p.getId());
        assertEquals("OPENING", p.getStatus());
        assertNull(p.getStopGainPercentageRule());
        assertNull(p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPEN_ORDER_01", p.getOpenOrderId());
        assertNull(p.getCloseOrderId());
        assertNull(p.getLowestPrice());
        assertNull(p.getHighestPrice());
        assertTrue(p.getTrades().isEmpty());
        // Position 2.
        p = positions.next();
        assertEquals(2, p.getId());
        assertEquals("OPENED", p.getStatus());
        assertEquals(10, p.getStopGainPercentageRule());
        assertNull(p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPEN_ORDER_02", p.getOpenOrderId());
        assertNull(p.getCloseOrderId());
        assertEquals(0, new BigDecimal("1").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("2").compareTo(p.getHighestPrice()));
        assertEquals(1, p.getTrades().size());
        assertTrue(p.getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_01".equals(trade.getId())));
        // Position 3.
        p = positions.next();
        assertEquals(3, p.getId());
        assertEquals("CLOSING", p.getStatus());
        assertNull(p.getStopGainPercentageRule());
        assertEquals(20, p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPEN_ORDER_03", p.getOpenOrderId());
        assertEquals("NON_EXISTING_TRADE", p.getCloseOrderId());
        assertEquals(0, new BigDecimal("17").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("68").compareTo(p.getHighestPrice()));
        assertEquals(1, p.getTrades().size());
        assertTrue(p.getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_02".equals(trade.getId())));
        // Position 4.
        p = positions.next();
        assertEquals(4, p.getId());
        assertEquals("CLOSED", p.getStatus());
        assertEquals(30, p.getStopGainPercentageRule());
        assertEquals(40, p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPEN_ORDER_04", p.getOpenOrderId());
        assertEquals("BACKUP_OPEN_ORDER_05", p.getCloseOrderId());
        assertEquals(0, new BigDecimal("17").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("68").compareTo(p.getHighestPrice()));
        assertEquals(2, p.getTrades().size());
        assertTrue(p.getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_03".equals(trade.getId())));
        assertTrue(p.getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_04".equals(trade.getId())));
    }

}
