package tech.cassandre.trading.bot.test.core.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DTO - PositionRulesDTO")
public class PositionRulesDTOTest {

    @Test
    @DisplayName("Check no rules & toString()")
    public void checkNoRules() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder().build();
        // Tests.
        assertFalse(p.isStopGainPercentageSet());
        assertFalse(p.isStopLossPercentageSet());
        assertEquals("No rules", p.toString());
    }

    @Test
    @DisplayName("Check stop gain rule & toString()")
    public void checkStopGainRule() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopGainPercentage(1f)
                .build();
        // Tests.
        assertTrue(p.isStopGainPercentageSet());
        assertFalse(p.isStopLossPercentageSet());
        assertEquals("Stop gain at 1 %", p.toString());
    }

    @Test
    @DisplayName("Check stop loss rule & toString()")
    public void checkStopLossRule() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopLossPercentage(2f)
                .build();
        // Tests.
        assertFalse(p.isStopGainPercentageSet());
        assertTrue(p.isStopLossPercentageSet());
        assertEquals("Stop loss at 2 %", p.toString());
    }

    @Test
    @DisplayName("Check All rules & toString()")
    public void checkAllRules() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopGainPercentage(10f)
                .stopLossPercentage(11f)
                .build();
        // Tests.
        assertTrue(p.isStopGainPercentageSet());
        assertTrue(p.isStopLossPercentageSet());
        assertEquals("Stop gain at 10 % / Stop loss at 11 %", p.toString());
    }

}
