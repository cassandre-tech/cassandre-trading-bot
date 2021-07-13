package tech.cassandre.trading.bot.util.jpa;

import lombok.Data;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;

/**
 * Currency amount (amount value + currency).
 */
@Data
@Embeddable
@SuppressWarnings("checkstyle:VisibilityModifier")
public class CurrencyAmount {

    /** Amount value. */
    @Column(precision = PRECISION, scale = SCALE)
    BigDecimal value;

    /** Amount currency. */
    String currency;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CurrencyAmount that = (CurrencyAmount) o;
        return new EqualsBuilder()
                .append(this.value, that.value)
                .append(this.currency, that.currency)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(value)
                .append(currency)
                .toHashCode();
    }

    @Override
    public final String toString() {
        if (value != null) {
            return value + " " + currency;
        } else {
            return "Null";
        }
    }

}
