package tech.cassandre.trading.bot.test.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("DTO - EqualsBuilder")
public class EqualsBuilderTest {

    @Test
    @DisplayName("Check equals on strings")
    public void checkStringEquals() {
        boolean result;

        // Two null strings -> Equals.
        result = new EqualsBuilder().append(null, null).isEquals();
        assertTrue(result);

        // First object null and second non null -> Not equals.
        result = new EqualsBuilder().append(null, "e").isEquals();
        assertFalse(result);

        // First object non null and the second null -> Not equals.
        result = new EqualsBuilder().append("e", null).isEquals();
        assertFalse(result);

        // Two non equal strings -> Not equals.
        result = new EqualsBuilder().append("e", "a").isEquals();
        assertFalse(result);

        // Two equal strings -> Not equals.
        result = new EqualsBuilder().append("test", "test").isEquals();
        assertTrue(result);

        // Two with several equals and one not equal.
        result = new EqualsBuilder()
                .append(null, null)
                .append(null, "e")
                .append("e", null)
                .append("e", "a")
                .append("test", "test")
                .isEquals();
        assertFalse(result);
        result = new EqualsBuilder()
                .append("test", "test")
                .append(null, null)
                .append(null, "e")
                .append("e", null)
                .append("e", "a")
                .isEquals();
        assertFalse(result);
    }

    @Test
    @DisplayName("Check equals on BigDecimal")
    public void checkBigDecimalEquals() {
        boolean result;

        // BigDecimal & null.
        result = new EqualsBuilder().append(new BigDecimal("1.00000"), null).isEquals();
        assertFalse(result);

        // Same BigDecimal but with a different format.
        result = new EqualsBuilder().append(new BigDecimal("1.00000"), new BigDecimal("1")).isEquals();
        assertTrue(result);

        // Two different BigDecimals.
        result = new EqualsBuilder().append(new BigDecimal("1.00000"), new BigDecimal("1.1")).isEquals();
        assertFalse(result);
    }

}
