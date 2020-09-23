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


@SpringBootTest
@DisplayName("Backup - Imported data")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql"),
        @Property(key = "spring.jpa.hibernate.ddl-auto", value = "create-drop")
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ImportedDataTest extends BaseTest {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Test
    @DisplayName("Check trades from imported data")
    public void checkImportedTrades() {
        // Trades.
        final Iterator<Trade> trades = tradeRepository.findByOrderByTimestampAsc().iterator();
        assertEquals(5, tradeRepository.count());
        // Trade 01.
        Trade t = trades.next();
        assertEquals("BACKUP_TRADE_01", t.getId());
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getFeeAmount().compareTo(new BigDecimal("11")));
        assertEquals("USDT", t.getFeeCurrency());
        assertEquals("BACKUP_OPEN_ORDER_02", t.getOrderId());
        assertEquals(0, t.getOriginalAmount().compareTo(new BigDecimal("12")));
        assertEquals(0, t.getPrice().compareTo(new BigDecimal("13")));
        assertEquals(createZonedDateTime("01-08-2020"), t.getTimestamp());
        assertEquals("BID", t.getType());
        // Trade 02.
        t = trades.next();
        assertEquals("BACKUP_TRADE_02", t.getId());
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getFeeAmount().compareTo(new BigDecimal("21")));
        assertEquals("USDT", t.getFeeCurrency());
        assertEquals("BACKUP_OPEN_ORDER_03", t.getOrderId());
        assertEquals(0, t.getOriginalAmount().compareTo(new BigDecimal("22")));
        assertEquals(0, t.getPrice().compareTo(new BigDecimal("23")));
        assertEquals(createZonedDateTime("02-08-2020"), t.getTimestamp());
        assertEquals("BID", t.getType());
        // Trade 03.
        t = trades.next();
        assertEquals("BACKUP_TRADE_03", t.getId());
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getFeeAmount().compareTo(new BigDecimal("31")));
        assertEquals("USDT", t.getFeeCurrency());
        assertEquals("BACKUP_OPEN_ORDER_04", t.getOrderId());
        assertEquals(0, t.getOriginalAmount().compareTo(new BigDecimal("32")));
        assertEquals(0, t.getPrice().compareTo(new BigDecimal("33")));
        assertEquals(createZonedDateTime("03-08-2020"), t.getTimestamp());
        assertEquals("BID", t.getType());
        // Trade 04.
        t = trades.next();
        assertEquals("BACKUP_TRADE_04", t.getId());
        assertEquals("BTC/USDT", t.getCurrencyPair());
        assertEquals(0, t.getFeeAmount().compareTo(new BigDecimal("41")));
        assertEquals("USDT", t.getFeeCurrency());
        assertEquals("BACKUP_OPEN_ORDER_05", t.getOrderId());
        assertEquals(0, t.getOriginalAmount().compareTo(new BigDecimal("42")));
        assertEquals(0, t.getPrice().compareTo(new BigDecimal("43")));
        assertEquals(createZonedDateTime("04-08-2020"), t.getTimestamp());
        assertEquals("ASK", t.getType());
        // Trade 05.
        t = trades.next();
        assertEquals("BACKUP_TRADE_00", t.getId());
        assertEquals("ETH/USD", t.getCurrencyPair());
        assertEquals(0, t.getFeeAmount().compareTo(new BigDecimal("51")));
        assertEquals("USD", t.getFeeCurrency());
        assertEquals("TEMP", t.getOrderId());
        assertEquals(0, t.getOriginalAmount().compareTo(new BigDecimal("52")));
        assertEquals(0, t.getPrice().compareTo(new BigDecimal("53")));
        assertEquals(createZonedDateTime("05-08-2020"), t.getTimestamp());
        assertEquals("ASK", t.getType());
    }

    @Test
    @DisplayName("Check positions from imported data")
    public void checkImportedPositions() {
        // Positions.
        final Iterator<Position> positions = positionRepository.findAll().iterator();
        assertEquals(6, positionRepository.count());
        // Position 1.
        Position p = positions.next();
        assertEquals(1, p.getId());
        assertNull(p.getStopGainPercentageRule());
        assertNull(p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPEN_ORDER_01", p.getOpenOrderId());
        assertNull(p.getCloseOrderId());
        // Position 2.
        p = positions.next();
        assertEquals(2, p.getId());
        assertEquals(10, p.getStopGainPercentageRule());
        assertNull(p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPEN_ORDER_02", p.getOpenOrderId());
        assertNull(p.getCloseOrderId());
        // Position 3.
        p = positions.next();
        assertEquals(3, p.getId());
        assertNull(p.getStopGainPercentageRule());
        assertEquals(20, p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPEN_ORDER_03", p.getOpenOrderId());
        assertEquals("NON_EXISTING_TRADE", p.getCloseOrderId());
        // Position 4.
        p = positions.next();
        assertEquals(4, p.getId());
        assertEquals(30, p.getStopGainPercentageRule());
        assertEquals(40, p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPEN_ORDER_04", p.getOpenOrderId());
        assertEquals("BACKUP_OPEN_ORDER_05", p.getCloseOrderId());
        // Position 5.
        p = positions.next();
        assertEquals(5, p.getId());
        assertEquals(51, p.getStopGainPercentageRule());
        assertEquals(52, p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPEN_ORDER_51", p.getOpenOrderId());
        assertNull(p.getCloseOrderId());
        // Position 6.
        p = positions.next();
        assertEquals(6, p.getId());
        assertEquals(61, p.getStopGainPercentageRule());
        assertEquals(62, p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPEN_ORDER_61", p.getOpenOrderId());
        assertNull(p.getCloseOrderId());
    }

}
