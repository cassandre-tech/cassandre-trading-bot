package tech.cassandre.trading.bot.test.core.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DTO - PositionRulesDTO")
public class PositionRulesDTOTest {

    @Test
    @DisplayName("Check no rules & toString()")
    public void checkNoRules() {
        // Position rules creation.
        PositionRulesDTO p = PositionRulesDTO.builder().build();
        // Tests.
        assertFalse(p.isStopGainPercentageSet());
        assertNull(p.getStopGainPercentage());
        assertFalse(p.isStopLossPercentageSet());
        assertNull(p.getStopLossPercentage());
        assertEquals("No rules", p.toString());
    }

    @Test
    @DisplayName("Check stop gain rule & toString()")
    public void checkStopGainRule() {
        // Position rules creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopGainPercentage(1f)
                .build();
        // Tests.
        assertTrue(p.isStopGainPercentageSet());
        assertEquals(1f, p.getStopGainPercentage());
        assertFalse(p.isStopLossPercentageSet());
        assertNull(p.getStopLossPercentage());
        assertEquals("Stop gain at 1 %", p.toString());
    }

    @Test
    @DisplayName("Check stop loss rule & toString()")
    public void checkStopLossRule() {
        // Position rules creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopLossPercentage(2f)
                .build();
        // Tests.
        assertFalse(p.isStopGainPercentageSet());
        assertNull(p.getStopGainPercentage());
        assertTrue(p.isStopLossPercentageSet());
        assertEquals(2f, p.getStopLossPercentage());
        assertEquals("Stop loss at 2 %", p.toString());
    }

    @Test
    @DisplayName("Check both rules & toString()")
    public void checkBothRules() {
        // Position rules creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopGainPercentage(10f)
                .stopLossPercentage(11f)
                .build();
        // Tests.
        assertTrue(p.isStopGainPercentageSet());
        assertEquals(10f, p.getStopGainPercentage());
        assertTrue(p.isStopLossPercentageSet());
        assertEquals(11f, p.getStopLossPercentage());
        assertEquals("Stop gain at 10 % / Stop loss at 11 %", p.toString());
    }

}
