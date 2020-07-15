package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PositionRulesDTO test.
 */
@DisplayName("PositionRules DTO tests")
public class PositionRulesDTOTest {

    @Test
    @DisplayName("No rules")
    public void noRules() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder().create();
        // Tests.
        assertFalse(p.isStopGainPercentageSet());
        assertFalse(p.isStopLossPercentageSet());
    }

    @Test
    @DisplayName("Stop gain rule")
    public void stopGainRule() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopGainPercentage(1f)
                .create();
        // Tests.
        assertTrue(p.isStopGainPercentageSet());
        assertFalse(p.isStopLossPercentageSet());
    }

    @Test
    @DisplayName("Stop loss rule")
    public void stopLossRule() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopLossPercentage(1f)
                .create();
        // Tests.
        assertFalse(p.isStopGainPercentageSet());
        assertTrue(p.isStopLossPercentageSet());
    }

    @Test
    @DisplayName("All rules")
    public void allRules() {
        // Position creation.
        PositionRulesDTO p = PositionRulesDTO.builder()
                .stopGainPercentage(10f)
                .stopLossPercentage(10f)
                .create();
        // Tests.
        assertTrue(p.isStopGainPercentageSet());
        assertTrue(p.isStopLossPercentageSet());
    }

}
