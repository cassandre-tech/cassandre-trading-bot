package tech.cassandre.trading.bot.test.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;

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
        assertEquals(OPENING, p.getStatus());
        assertNull(p.getStopGainPercentageRule());
        assertNull(p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPENING_ORDER_01", p.getOpeningOrder().getId());
        assertNull(p.getClosingOrder());
        assertNull(p.getLowestPrice());
        assertNull(p.getHighestPrice());
        assertNull(p.getLatestPrice());
        assertTrue(p.getTrades().isEmpty());
        // Position 2.
        p = positions.next();
        assertEquals(2, p.getId());
        assertEquals(OPENED, p.getStatus());
        assertEquals(10, p.getStopGainPercentageRule());
        assertNull(p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPENING_ORDER_02", p.getOpeningOrder().getId());
        assertNull(p.getClosingOrder());
        assertEquals(0, new BigDecimal("1").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("2").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("3").compareTo(p.getLatestPrice()));
        assertEquals(1, p.getTrades().size());
        assertTrue(p.getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_01".equals(trade.getId())));
        // Position 3.
        p = positions.next();
        assertEquals(3, p.getId());
        assertEquals(CLOSING, p.getStatus());
        assertNull(p.getStopGainPercentageRule());
        assertEquals(20, p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPENING_ORDER_03", p.getOpeningOrder().getId());
        assertEquals("BACKUP_CLOSING_ORDER_01", p.getClosingOrder().getId());
        assertEquals(0, new BigDecimal("17").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("68").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("92").compareTo(p.getLatestPrice()));
        assertEquals(1, p.getTrades().size());
        assertTrue(p.getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_02".equals(trade.getId())));
        // Position 4.
        p = positions.next();
        assertEquals(4, p.getId());
        assertEquals(CLOSED, p.getStatus());
        assertEquals(30, p.getStopGainPercentageRule());
        assertEquals(40, p.getStopLossPercentageRule());
        assertEquals("BACKUP_OPENING_ORDER_04", p.getOpeningOrder().getId());
        assertEquals("BACKUP_CLOSING_ORDER_02", p.getClosingOrder().getId());
        assertEquals(0, new BigDecimal("17").compareTo(p.getLowestPrice()));
        assertEquals(0, new BigDecimal("68").compareTo(p.getHighestPrice()));
        assertEquals(0, new BigDecimal("93").compareTo(p.getLatestPrice()));
        assertEquals(2, p.getTrades().size());
        assertTrue(p.getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_03".equals(trade.getId())));
        assertTrue(p.getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_04".equals(trade.getId())));
    }

    @Test
    @DisplayName("Check find by status")
    public void checkFindByStatus() {
        final List<Position> openingPositions = positionRepository.findByStatus(OPENING);
        assertEquals(1, openingPositions.size());
        assertEquals(1, openingPositions.get(0).getId());
        final List<Position> openedPositions = positionRepository.findByStatus(OPENED);
        assertEquals(1, openedPositions.size());
        assertEquals(2, openedPositions.get(0).getId());
        final List<Position> closingPositions = positionRepository.findByStatus(CLOSING);
        assertEquals(1, closingPositions.size());
        assertEquals(3, closingPositions.get(0).getId());
        final List<Position> closedPositions = positionRepository.findByStatus(CLOSED);
        assertEquals(2, closedPositions.size());
        assertEquals(4, closedPositions.get(0).getId());
        assertEquals(5, closedPositions.get(1).getId());
    }

    @Test
    @DisplayName("Check find by status not")
    public void checkFindByStatusNot() {
        final List<Position> notClosingPositions = positionRepository.findByStatusNot(CLOSING);
        assertEquals(4, notClosingPositions.size());
        assertEquals(1, notClosingPositions.get(0).getId());
        assertEquals(2, notClosingPositions.get(1).getId());
        assertEquals(4, notClosingPositions.get(2).getId());
        assertEquals(5, notClosingPositions.get(3).getId());
    }

}
