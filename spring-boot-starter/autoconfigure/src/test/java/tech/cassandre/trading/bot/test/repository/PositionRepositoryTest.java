package tech.cassandre.trading.bot.test.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DisplayName("Repository - Position")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
public class PositionRepositoryTest {

    @Autowired
    private PositionRepository positionRepository;

    @Test
    @DisplayName("Check imported data")
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
        assertNull(p.getLatestPrice());
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
        assertEquals(0, new BigDecimal("3").compareTo(p.getLatestPrice()));
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
        assertEquals(0, new BigDecimal("92").compareTo(p.getLatestPrice()));
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
        assertEquals(0, new BigDecimal("93").compareTo(p.getLatestPrice()));
        assertEquals(2, p.getTrades().size());
        assertTrue(p.getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_03".equals(trade.getId())));
        assertTrue(p.getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_04".equals(trade.getId())));
    }

}
