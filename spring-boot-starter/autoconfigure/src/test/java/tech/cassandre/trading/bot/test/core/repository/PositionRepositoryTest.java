package tech.cassandre.trading.bot.test.core.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.LONG;

@SpringBootTest
@DisplayName("Repository - Position")
@Configuration({
        @Property(key = "spring.liquibase.change-log", value = "classpath:db/test/core/backup.yaml")
})
@ActiveProfiles("schedule-disabled")
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class PositionRepositoryTest {

    @Autowired
    private PositionRepository positionRepository;

    @Test
    @DisplayName("Check imported data")
    public void checkImportedPositions() {
        // Positions.
        final Iterator<Position> positions = positionRepository.findByOrderByUid().iterator();
        assertEquals(5, positionRepository.count());

        // Position 1.
        Position position1 = positions.next();
        assertEquals(1, position1.getUid());
        assertEquals(1, position1.getPositionId());
        assertEquals(LONG, position1.getType());
        assertNotNull(position1.getStrategy());
        assertEquals(1, position1.getStrategy().getUid());
        assertEquals("01", position1.getStrategy().getStrategyId());
        assertEquals("BTC/USDT", position1.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(position1.getAmount().getValue()));
        assertEquals("BTC", position1.getAmount().getCurrency());
        assertNull(position1.getStopGainPercentageRule());
        assertNull(position1.getStopLossPercentageRule());
        assertEquals(OPENING, position1.getStatus());
        assertFalse(position1.isForceClosing());
        assertEquals("BACKUP_OPENING_ORDER_01", position1.getOpeningOrder().getOrderId());
        assertTrue(position1.getOpeningOrder().getTrades().isEmpty());
        assertNull(position1.getClosingOrder());
        assertNull(position1.getLowestGainPrice());
        assertNull(position1.getHighestGainPrice());
        assertNull(position1.getLatestGainPrice());

        // Retrieving position 1 with findByPositionId().
        Optional<Position> position1Bis = positionRepository.findByPositionId(1L);
        assertTrue(position1Bis.isPresent());
        assertEquals(position1, position1Bis.get());

        // Position 2.
        Position position2 = positions.next();
        assertEquals(2, position2.getUid());
        assertEquals(2, position2.getPositionId());
        assertEquals(LONG, position2.getType());
        assertNotNull(position2.getStrategy());
        assertEquals(1, position2.getStrategy().getUid());
        assertEquals("01", position2.getStrategy().getStrategyId());
        assertEquals("BTC/USDT", position2.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(position2.getAmount().getValue()));
        assertEquals("BTC", position2.getAmount().getCurrency());
        assertEquals(10, position2.getStopGainPercentageRule());
        assertNull(position2.getStopLossPercentageRule());
        assertEquals(OPENED, position2.getStatus());
        assertFalse(position2.isForceClosing());
        assertEquals("BACKUP_OPENING_ORDER_02", position2.getOpeningOrder().getOrderId());
        assertTrue(position2.getOpeningOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_01".equals(trade.getTradeId())));
        assertNull(position2.getClosingOrder());
        assertEquals(0, new BigDecimal("1").compareTo(position2.getLowestGainPrice().getValue()));
        assertEquals("USDT", position2.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("2").compareTo(position2.getHighestGainPrice().getValue()));
        assertEquals("USDT", position2.getHighestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("3").compareTo(position2.getLatestGainPrice().getValue()));
        assertEquals("USDT", position2.getLatestGainPrice().getCurrency());

        // Retrieving position 2 with findByPositionId().
        Optional<Position> position2Bis = positionRepository.findByPositionId(2L);
        assertTrue(position2Bis.isPresent());
        assertEquals(position2, position2Bis.get());

        // Position 3.
        Position position3 = positions.next();
        assertEquals(3, position3.getUid());
        assertEquals(3, position3.getPositionId());
        assertEquals(LONG, position3.getType());
        assertNotNull(position3.getStrategy());
        assertEquals(1, position3.getStrategy().getUid());
        assertEquals("01", position3.getStrategy().getStrategyId());
        assertEquals("BTC/USDT", position3.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(position3.getAmount().getValue()));
        assertEquals("BTC", position3.getAmount().getCurrency());
        assertNull(position3.getStopGainPercentageRule());
        assertEquals(20, position3.getStopLossPercentageRule());
        assertEquals(CLOSING, position3.getStatus());
        assertFalse(position3.isForceClosing());
        assertEquals("BACKUP_OPENING_ORDER_03", position3.getOpeningOrder().getOrderId());
        assertEquals(1, position3.getOpeningOrder().getTrades().size());
        assertTrue(position3.getOpeningOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_02".equals(trade.getTradeId())));
        assertEquals("BACKUP_CLOSING_ORDER_01", position3.getClosingOrder().getOrderId());
        assertEquals(1, position3.getClosingOrder().getTrades().size());
        assertTrue(position3.getClosingOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_04".equals(trade.getTradeId())));
        assertEquals(0, new BigDecimal("17").compareTo(position3.getLowestGainPrice().getValue()));
        assertEquals("USDT", position3.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("68").compareTo(position3.getHighestGainPrice().getValue()));
        assertEquals("USDT", position3.getHighestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("92").compareTo(position3.getLatestGainPrice().getValue()));
        assertEquals("USDT", position3.getLatestGainPrice().getCurrency());

        // Retrieving position 3 with findByPositionId().
        Optional<Position> position3Bis = positionRepository.findByPositionId(3L);
        assertTrue(position3Bis.isPresent());
        assertEquals(position3, position3Bis.get());

        // Position 4.
        Position position4 = positions.next();
        assertEquals(4, position4.getUid());
        assertEquals(4, position4.getPositionId());
        assertEquals(LONG, position4.getType());
        assertNotNull(position4.getStrategy());
        assertEquals(1, position4.getStrategy().getUid());
        assertEquals("01", position4.getStrategy().getStrategyId());
        assertEquals("BTC/USDT", position4.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(position4.getAmount().getValue()));
        assertEquals("BTC", position4.getAmount().getCurrency());
        assertEquals(30, position4.getStopGainPercentageRule());
        assertEquals(40, position4.getStopLossPercentageRule());
        assertEquals(CLOSED, position4.getStatus());
        assertFalse(position4.isForceClosing());
        assertEquals("BACKUP_OPENING_ORDER_04", position4.getOpeningOrder().getOrderId());
        assertEquals(1, position4.getOpeningOrder().getTrades().size());
        assertTrue(position4.getOpeningOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_03".equals(trade.getTradeId())));
        assertEquals("BACKUP_CLOSING_ORDER_02", position4.getClosingOrder().getOrderId());
        assertEquals(1, position4.getClosingOrder().getTrades().size());
        assertTrue(position4.getClosingOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_05".equals(trade.getTradeId())));
        assertEquals(0, new BigDecimal("17").compareTo(position4.getLowestGainPrice().getValue()));
        assertEquals("USDT", position4.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("68").compareTo(position4.getHighestGainPrice().getValue()));
        assertEquals("USDT", position4.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("93").compareTo(position4.getLatestGainPrice().getValue()));
        assertEquals("USDT", position4.getLowestGainPrice().getCurrency());

        // Retrieving position 4 with findByPositionId().
        Optional<Position> position4Bis = positionRepository.findByPositionId(4L);
        assertTrue(position4Bis.isPresent());
        assertEquals(position4, position4Bis.get());

        // Test last position id retrieval.
        assertEquals(5, positionRepository.getLastPositionIdUsedByStrategy(1L));
        assertEquals(0, positionRepository.getLastPositionIdUsedByStrategy(9L));
    }

    @Test
    @DisplayName("Check find by status")
    public void checkFindByStatus() {
        final List<Position> openingPositions = positionRepository.findByStatus(OPENING);
        assertEquals(1, openingPositions.size());
        assertEquals(1, openingPositions.get(0).getUid());
        final List<Position> openedPositions = positionRepository.findByStatus(OPENED);
        assertEquals(1, openedPositions.size());
        assertEquals(2, openedPositions.get(0).getUid());
        final List<Position> closingPositions = positionRepository.findByStatus(CLOSING);
        assertEquals(1, closingPositions.size());
        assertEquals(3, closingPositions.get(0).getUid());
        final List<Position> closedPositions = positionRepository.findByStatus(CLOSED);
        assertEquals(2, closedPositions.size());
        assertEquals(4, closedPositions.get(0).getUid());
        assertEquals(5, closedPositions.get(1).getUid());

        // Tests for findByStatusIn().
        final List<Position> positions = positionRepository.findByStatusIn(Stream.of(CLOSING, CLOSED).toList());
        assertEquals(3, positions.size());
    }

    @Test
    @DisplayName("Check find by status not")
    public void checkFindByStatusNot() {
        final List<Position> notClosingPositions = positionRepository.findByStatusNot(CLOSING);
        assertEquals(4, notClosingPositions.size());
        assertEquals(1, notClosingPositions.get(0).getUid());
        assertEquals(2, notClosingPositions.get(1).getUid());
        assertEquals(4, notClosingPositions.get(2).getUid());
        assertEquals(5, notClosingPositions.get(3).getUid());
    }

    @Test
    @DisplayName("Check update rules on position")
    public void checkUpdateRulesOnPosition() {
        // Position used by test.
        final long positionId = 5L;

        // We retrieve the positions.
        Optional<Position> position = positionRepository.findById(positionId);
        assertTrue(position.isPresent());
        assertEquals(30, position.get().getStopGainPercentageRule());
        assertEquals(40, position.get().getStopLossPercentageRule());

        // We update the rules with new values.
        positionRepository.updateStopGainRule(positionId, 10f);
        positionRepository.updateStopLossRule(positionId, 20f);
        position = positionRepository.findById(positionId);
        assertTrue(position.isPresent());
        assertEquals(10, position.get().getStopGainPercentageRule());
        assertEquals(20, position.get().getStopLossPercentageRule());

        // We update the rules with null.
        positionRepository.updateStopGainRule(positionId, null);
        positionRepository.updateStopLossRule(positionId, null);
        position = positionRepository.findById(positionId);
        assertTrue(position.isPresent());
        assertNull(position.get().getStopGainPercentageRule());
        assertNull(position.get().getStopLossPercentageRule());
    }

    @Test
    @DisplayName("Check update force closing on position")
    public void checkUpdateForceClosingPosition() {
        // Position used by test.
        final long positionId = 5L;

        // We retrieve the position.
        Optional<Position> p = positionRepository.findById(positionId);
        assertTrue(p.isPresent());
        assertFalse(p.get().isForceClosing());

        // We update the force closing.
        positionRepository.updateForceClosing(positionId, true);

        // We retrieve the value to check if it has been updated.
        p = positionRepository.findById(positionId);
        assertTrue(p.isPresent());
        assertTrue(p.get().isForceClosing());
    }

    @Test
    @DisplayName("Check update auto close on position")
    public void checkUpdateAutoClosePosition() {
        // Position used by test.
        final long positionId = 5L;

        // We retrieve the position.
        Optional<Position> p = positionRepository.findById(positionId);
        assertTrue(p.isPresent());
        assertTrue(p.get().isAutoClose());

        // We update the force closing.
        positionRepository.updateAutoClose(positionId, false);

        // We retrieve the value to check if it has been updated.
        p = positionRepository.findById(positionId);
        assertTrue(p.isPresent());
        assertFalse(p.get().isAutoClose());
    }

}
