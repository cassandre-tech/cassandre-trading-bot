package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PositionRulesDTO test.
 */
@DisplayName("PositionRules DTO")
public class PositionRulesDTOTest {

    @Test
    @DisplayName("No rules & toString()")
    public void noRules() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder().create();
        // Tests.
        assertFalse(p.isStopGainPercentageSet());
        assertFalse(p.isStopLossPercentageSet());
        assertEquals("No rules", p.toString());
    }

    @Test
    @DisplayName("Stop gain rule & toString()")
    public void stopGainRule() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopGainPercentage(1f)
                .create();
        // Tests.
        assertTrue(p.isStopGainPercentageSet());
        assertFalse(p.isStopLossPercentageSet());
        assertEquals("Stop gain at 1 %", p.toString());
    }

    @Test
    @DisplayName("Stop loss rule & toString()")
    public void stopLossRule() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopLossPercentage(2f)
                .create();
        // Tests.
        assertFalse(p.isStopGainPercentageSet());
        assertTrue(p.isStopLossPercentageSet());
        assertEquals("Stop loss at 2 %", p.toString());
    }

    @Test
    @DisplayName("All rules & toString()")
    public void allRules() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopGainPercentage(10f)
                .stopLossPercentage(11)
                .create();
        // Tests.
        assertTrue(p.isStopGainPercentageSet());
        assertTrue(p.isStopLossPercentageSet());
        assertEquals("Stop gain at 10 % / Stop loss at 11 %", p.toString());
    }

}
