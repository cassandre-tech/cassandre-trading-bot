package tech.cassandre.trading.bot.util.jpa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.Hibernate;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;

import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;

/**
 * Currency amount (amount value + currency).
 */
@Getter
@Setter
@RequiredArgsConstructor
@Embeddable
@SuppressWarnings("checkstyle:VisibilityModifier")
public class CurrencyAmount {

    /** Amount value. */
    @Column(precision = PRECISION, scale = SCALE)
    BigDecimal value;

    /** Amount currency. */
    String currency;

    @Override
    public final String toString() {
        if (value != null) {
            return value + " " + currency;
        } else {
            return "Null";
        }
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        CurrencyAmount that = (CurrencyAmount) o;

        if (value.compareTo(that.getValue()) != 0) {
            return false;
        }
        return Objects.equals(currency, that.currency);
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(value)
                .append(currency)
                .toHashCode();
    }

}
