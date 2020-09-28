package tech.cassandre.trading.bot.test.backup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import tech.cassandre.trading.bot.batch.PositionFlux;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;
import tech.cassandre.trading.bot.service.PositionService;
import tech.cassandre.trading.bot.test.backup.mocks.PositionBackupMock;
import tech.cassandre.trading.bot.test.util.junit.BaseTest;
import tech.cassandre.trading.bot.test.util.junit.configuration.Configuration;
import tech.cassandre.trading.bot.test.util.junit.configuration.Property;
import tech.cassandre.trading.bot.test.util.strategies.TestableCassandreStrategy;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.CLOSING;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENED;
import static tech.cassandre.trading.bot.dto.position.PositionStatusDTO.OPENING;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;

@SpringBootTest
@DisplayName("Backup - Positions")
@Configuration({
        @Property(key = "spring.datasource.data", value = "classpath:/backup.sql"),
        @Property(key = "spring.jpa.hibernate.ddl-auto", value = "create-drop")
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(PositionBackupMock.class)
public class PositionBackupTest extends BaseTest {

    public static final CurrencyPairDTO cp = new CurrencyPairDTO(ETH, BTC);

    @Autowired
    private TestableCassandreStrategy strategy;

    @Autowired
    private PositionService positionService;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private PositionFlux positionFlux;

    @Test
    @DisplayName("Check restored positions")
    public void checkRestoredPositions() {
        // =============================================================================================================
        // Check that positions and restored in strategy, services & flux.
        assertTrue(strategy.getPositionService().getPositions().size() >= 6);
        assertTrue(strategy.getPositions().size() >= 6);
        assertTrue(strategy.getPositionsUpdateReceived().isEmpty());

        // Check position 1.
        PositionDTO p = strategy.getPositions().get(1L);
        assertNotNull(p);
        assertEquals(OPENING, p.getStatus());
        assertEquals(1L, p.getId());
        assertFalse(p.getRules().isStopGainPercentageSet());
        assertFalse(p.getRules().isStopLossPercentageSet());
        assertEquals("BACKUP_OPEN_ORDER_01", p.getOpenOrderId());
        assertNull(p.getCloseOrderId());
        // Check position 2.
        p = strategy.getPositions().get(2L);
        assertNotNull(p);
        assertEquals(OPENED, p.getStatus());
        assertEquals(2L, p.getId());
        assertTrue(p.getRules().isStopGainPercentageSet());
        assertEquals(10, p.getRules().getStopGainPercentage());
        assertFalse(p.getRules().isStopLossPercentageSet());
        assertEquals("BACKUP_OPEN_ORDER_02", p.getOpenOrderId());
        assertNull(p.getCloseOrderId());
        // Check position 3.
        p = strategy.getPositions().get(3L);
        assertNotNull(p);
        assertEquals(CLOSING, p.getStatus());
        assertEquals(3L, p.getId());
        assertFalse(p.getRules().isStopGainPercentageSet());
        assertTrue(p.getRules().isStopLossPercentageSet());
        assertEquals(20, p.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPEN_ORDER_03", p.getOpenOrderId());
        assertEquals("NON_EXISTING_TRADE", p.getCloseOrderId());
        // Check position 4.
        p = strategy.getPositions().get(4L);
        assertNotNull(p);
        assertEquals(CLOSED, p.getStatus());
        assertEquals(4L, p.getId());
        assertTrue(p.getRules().isStopGainPercentageSet());
        assertEquals(30, p.getRules().getStopGainPercentage());
        assertTrue(p.getRules().isStopLossPercentageSet());
        assertEquals(40, p.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPEN_ORDER_04", p.getOpenOrderId());
        assertEquals("BACKUP_OPEN_ORDER_05", p.getCloseOrderId());
        // Check position 5.
        p = strategy.getPositions().get(5L);
        assertNotNull(p);
        assertEquals(OPENING, p.getStatus());
        assertEquals(5L, p.getId());
        assertTrue(p.getRules().isStopGainPercentageSet());
        assertEquals(51, p.getRules().getStopGainPercentage());
        assertTrue(p.getRules().isStopLossPercentageSet());
        assertEquals(52, p.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPEN_ORDER_51", p.getOpenOrderId());
        assertNull(p.getCloseOrderId());
        // Check position 6.
        p = strategy.getPositions().get(6L);
        assertNotNull(p);
        assertEquals(OPENING, p.getStatus());
        assertEquals(6L, p.getId());
        assertTrue(p.getRules().isStopGainPercentageSet());
        assertEquals(61, p.getRules().getStopGainPercentage());
        assertTrue(p.getRules().isStopLossPercentageSet());
        assertEquals(62, p.getRules().getStopLossPercentage());
        assertEquals("BACKUP_OPEN_ORDER_61", p.getOpenOrderId());
        assertNull(p.getCloseOrderId());
    }

    @Test
    @DisplayName("Check saved positions")
    public void checkSavedPositions() {
        // =============================================================================================================
        // Add a position.
        long positionCount = positionRepository.count();
        PositionRulesDTO rules = PositionRulesDTO.builder().stopGainPercentage(1).stopLossPercentage(2).create();
        PositionCreationResultDTO creationResult1 = positionService.createPosition(cp, new BigDecimal("0.0001"), rules);
        assertTrue(creationResult1.isSuccessful());
        assertEquals("ORDER00010", creationResult1.getOrderId());

        // Check the position created.
        await().untilAsserted(() -> assertEquals(positionCount + 1, positionRepository.count()));
        Optional<Position> p = positionRepository.findById(7L);
        assertTrue(p.isPresent());
        assertEquals(7L, p.get().getId());
        assertEquals(1, p.get().getStopGainPercentageRule());
        assertEquals(2, p.get().getStopLossPercentageRule());
        assertEquals("ORDER00010", p.get().getOpenOrderId());
        assertNull(p.get().getCloseOrderId());

        // =============================================================================================================
        // Add another position.
        PositionCreationResultDTO creationResult2 = positionService.createPosition(cp, new BigDecimal("0.0002"), PositionRulesDTO.builder().create());
        assertTrue(creationResult2.isSuccessful());
        assertEquals("ORDER00020", creationResult2.getOrderId());

        // Check the position created.
        await().untilAsserted(() -> assertEquals(positionCount + 2, positionRepository.count()));
        p = positionRepository.findById(8L);
        assertTrue(p.isPresent());
        assertEquals(8L, p.get().getId());
        assertNull(p.get().getStopGainPercentageRule());
        assertNull(p.get().getStopLossPercentageRule());
        assertEquals("ORDER00020", p.get().getOpenOrderId());
        assertNull(p.get().getCloseOrderId());

        // =============================================================================================================
        // Position 2 is opened - try to close it with CLOSE_ORDER_FOR_POSITION_2.

        // Closing the position - order created but trade not arrived.
        Optional<PositionDTO> positionDTO2 = positionService.getPositionById(2L);
        assertTrue(positionDTO2.isPresent());
        assertEquals(OPENED, positionDTO2.get().getStatus());
        positionDTO2.get().setCloseOrderId("CLOSE_ORDER_FOR_POSITION_2");
        assertEquals(CLOSING, positionDTO2.get().getStatus());
        positionFlux.emitValue(positionDTO2.get());
        assertTrue(positionRepository.findById(2L).isPresent());
        await().untilAsserted(() -> assertEquals("CLOSE_ORDER_FOR_POSITION_2", positionRepository.findById(2L).get().getCloseOrderId()));
    }

}
