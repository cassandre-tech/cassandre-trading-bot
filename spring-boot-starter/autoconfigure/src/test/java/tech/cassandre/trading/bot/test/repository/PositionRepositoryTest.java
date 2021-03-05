package tech.cassandre.trading.bot.test.repository;

import io.qase.api.annotation.CaseId;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.position.PositionTypeDTO.LONG;

@SpringBootTest
@DisplayName("Repository - Position")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql")
})
public class PositionRepositoryTest {

    @Autowired
    private PositionRepository positionRepository;

    @Test
    @CaseId(57)
    @DisplayName("Check imported data")
    public void checkImportedPositions() {
        // Positions.
        final Iterator<Position> positions = positionRepository.findByOrderById().iterator();
        assertEquals(5, positionRepository.count());

        // Position 1.
        Position p = positions.next();
        assertEquals(1, p.getId());
        assertEquals(1, p.getPositionId());
        assertEquals(LONG, p.getType());
        assertNotNull(p.getStrategy());
        assertEquals(1, p.getStrategy().getId());
        assertEquals("01", p.getStrategy().getStrategyId());
        assertEquals("BTC/USDT", p.getCurrencyPair());
        assertEquals(0, new BigDecimal("10").compareTo(p.getAmount().getValue()));
        assertEquals("BTC", p.getAmount().getCurrency());
        assertNull(p.getStopGainPercentageRule());
        assertNull(p.getStopLossPercentageRule());
        assertEquals(OPENING, p.getStatus());
        assertEquals("BACKUP_OPENING_ORDER_01", p.getOpeningOrder().getOrderId());
        assertTrue(p.getOpeningOrder().getTrades().isEmpty());
        assertNull(p.getClosingOrder());
        assertNull(p.getLowestGainPrice());
        assertNull(p.getHighestGainPrice());
        assertNull(p.getLatestGainPrice());

        // Retrieving position 1 with findByPositionId().
        Optional<Position> pBis = positionRepository.findByPositionId(1L);
        assertTrue(pBis.isPresent());
        assertEquals(p, pBis.get());

        // Position 2.
        p = positions.next();
        assertEquals(2, p.getId());
        assertEquals(2, p.getPositionId());
        assertEquals(LONG, p.getType());
        assertNotNull(p.getStrategy());
        assertEquals(1, p.getStrategy().getId());
        assertEquals("01", p.getStrategy().getStrategyId());
        assertEquals("BTC/USDT", p.getCurrencyPair());
        assertEquals(0, new BigDecimal("20").compareTo(p.getAmount().getValue()));
        assertEquals("BTC", p.getAmount().getCurrency());
        assertEquals(10, p.getStopGainPercentageRule());
        assertNull(p.getStopLossPercentageRule());
        assertEquals(OPENED, p.getStatus());
        assertEquals("BACKUP_OPENING_ORDER_02", p.getOpeningOrder().getOrderId());
        assertTrue(p.getOpeningOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_01".equals(trade.getTradeId())));
        assertNull(p.getClosingOrder());
        assertEquals(0, new BigDecimal("1").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals("USDT", p.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("2").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals("USDT", p.getHighestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("3").compareTo(p.getLatestGainPrice().getValue()));
        assertEquals("USDT", p.getLatestGainPrice().getCurrency());

        // Retrieving position 2 with findByPositionId().
        pBis = positionRepository.findByPositionId(2L);
        assertTrue(pBis.isPresent());
        assertEquals(p, pBis.get());

        // Position 3.
        p = positions.next();
        assertEquals(3, p.getId());
        assertEquals(3, p.getPositionId());
        assertEquals(LONG, p.getType());
        assertNotNull(p.getStrategy());
        assertEquals(1, p.getStrategy().getId());
        assertEquals("01", p.getStrategy().getStrategyId());
        assertEquals("BTC/USDT", p.getCurrencyPair());
        assertEquals(0, new BigDecimal("30").compareTo(p.getAmount().getValue()));
        assertEquals("BTC", p.getAmount().getCurrency());
        assertNull(p.getStopGainPercentageRule());
        assertEquals(20, p.getStopLossPercentageRule());
        assertEquals(CLOSING, p.getStatus());
        assertEquals("BACKUP_OPENING_ORDER_03", p.getOpeningOrder().getOrderId());
        assertEquals(1, p.getOpeningOrder().getTrades().size());
        assertTrue(p.getOpeningOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_02".equals(trade.getTradeId())));
        assertEquals("BACKUP_CLOSING_ORDER_01", p.getClosingOrder().getOrderId());
        assertEquals(1, p.getClosingOrder().getTrades().size());
        assertTrue(p.getClosingOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_04".equals(trade.getTradeId())));
        assertEquals(0, new BigDecimal("17").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals("USDT", p.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("68").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals("USDT", p.getHighestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("92").compareTo(p.getLatestGainPrice().getValue()));
        assertEquals("USDT", p.getLatestGainPrice().getCurrency());

        // Retrieving position 3 with findByPositionId().
        pBis = positionRepository.findByPositionId(3L);
        assertTrue(pBis.isPresent());
        assertEquals(p, pBis.get());

        // Position 4.
        p = positions.next();
        assertEquals(4, p.getId());
        assertEquals(4, p.getPositionId());
        assertEquals(LONG, p.getType());
        assertNotNull(p.getStrategy());
        assertEquals(1, p.getStrategy().getId());
        assertEquals("01", p.getStrategy().getStrategyId());
        assertEquals("BTC/USDT", p.getCurrencyPair());
        assertEquals(0, new BigDecimal("40").compareTo(p.getAmount().getValue()));
        assertEquals("BTC", p.getAmount().getCurrency());
        assertEquals(30, p.getStopGainPercentageRule());
        assertEquals(40, p.getStopLossPercentageRule());
        assertEquals(CLOSED, p.getStatus());
        assertEquals("BACKUP_OPENING_ORDER_04", p.getOpeningOrder().getOrderId());
        assertEquals(1, p.getOpeningOrder().getTrades().size());
        assertTrue(p.getOpeningOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_03".equals(trade.getTradeId())));
        assertEquals("BACKUP_CLOSING_ORDER_02", p.getClosingOrder().getOrderId());
        assertEquals(1, p.getClosingOrder().getTrades().size());
        assertTrue(p.getClosingOrder().getTrades().stream().anyMatch(trade -> "BACKUP_TRADE_05".equals(trade.getTradeId())));
        assertEquals(0, new BigDecimal("17").compareTo(p.getLowestGainPrice().getValue()));
        assertEquals("USDT", p.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("68").compareTo(p.getHighestGainPrice().getValue()));
        assertEquals("USDT", p.getLowestGainPrice().getCurrency());
        assertEquals(0, new BigDecimal("93").compareTo(p.getLatestGainPrice().getValue()));
        assertEquals("USDT", p.getLowestGainPrice().getCurrency());

        // Retrieving position 4 with findByPositionId().
        pBis = positionRepository.findByPositionId(4L);
        assertTrue(pBis.isPresent());
        assertEquals(p, pBis.get());
    }

    @Test
    @CaseId(58)
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
    @CaseId(59)
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
