package tech.cassandre.trading.bot.test.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        Position position = positions.next();
        assertEquals(1, position.getId());
        assertEquals(OPENING, position.getStatus());
        assertNull(position.getStopGainPercentageRule());
        assertNull(position.getStopLossPercentageRule());
        assertEquals("BACKUP_OPENING_ORDER_01", position.getOpeningOrder().getId());
        assertNull(position.getClosingOrder());
        assertNull(position.getLowestPrice());
        assertNull(position.getHighestPrice());
        assertNull(position.getLatestPrice());
        assertTrue(position.getOpeningOrder().getTrades().isEmpty());
        assertNull(position.getClosingOrder());
        assertNotNull(position.getStrategy());
        assertEquals("001", position.getStrategy().getId());
        // Position 2.
        position = positions.next();
        assertEquals(2, position.getId());
        assertEquals(OPENED, position.getStatus());
        assertEquals(10, position.getStopGainPercentageRule());
        assertNull(position.getStopLossPercentageRule());
        assertEquals("BACKUP_OPENING_ORDER_02", position.getOpeningOrder().getId());
        assertNull(position.getClosingOrder());
        assertEquals(0, new BigDecimal("1").compareTo(position.getLowestPrice()));
        assertEquals(0, new BigDecimal("2").compareTo(position.getHighestPrice()));
        assertEquals(0, new BigDecimal("3").compareTo(position.getLatestPrice()));
        assertEquals(1, position.getOpeningOrder().getTrades().size());
        assertTrue(position.getOpeningOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_01".equals(trade.getId())));
        assertNotNull(position.getStrategy());
        assertEquals("001", position.getStrategy().getId());
        // Position 3.
        position = positions.next();
        assertEquals(3, position.getId());
        assertEquals(CLOSING, position.getStatus());
        assertNull(position.getStopGainPercentageRule());
        assertEquals(20, position.getStopLossPercentageRule());
        assertEquals("BACKUP_OPENING_ORDER_03", position.getOpeningOrder().getId());
        assertEquals("BACKUP_CLOSING_ORDER_01", position.getClosingOrder().getId());
        assertEquals(0, new BigDecimal("17").compareTo(position.getLowestPrice()));
        assertEquals(0, new BigDecimal("68").compareTo(position.getHighestPrice()));
        assertEquals(0, new BigDecimal("92").compareTo(position.getLatestPrice()));
        assertEquals(1, position.getOpeningOrder().getTrades().size());
        assertTrue(position.getOpeningOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_02".equals(trade.getId())));
        assertNotNull(position.getStrategy());
        assertEquals("001", position.getStrategy().getId());
        // Position 4.
        position = positions.next();
        assertEquals(4, position.getId());
        assertEquals(CLOSED, position.getStatus());
        assertEquals(30, position.getStopGainPercentageRule());
        assertEquals(40, position.getStopLossPercentageRule());
        assertEquals("BACKUP_OPENING_ORDER_04", position.getOpeningOrder().getId());
        assertEquals("BACKUP_CLOSING_ORDER_02", position.getClosingOrder().getId());
        assertEquals(0, new BigDecimal("17").compareTo(position.getLowestPrice()));
        assertEquals(0, new BigDecimal("68").compareTo(position.getHighestPrice()));
        assertEquals(0, new BigDecimal("93").compareTo(position.getLatestPrice()));
        assertEquals(1, position.getOpeningOrder().getTrades().size());
        assertEquals(1, position.getClosingOrder().getTrades().size());
        assertTrue(position.getOpeningOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_03".equals(trade.getId())));
        assertTrue(position.getClosingOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_05".equals(trade.getId())));
        assertNotNull(position.getStrategy());
        assertEquals("001", position.getStrategy().getId());
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
