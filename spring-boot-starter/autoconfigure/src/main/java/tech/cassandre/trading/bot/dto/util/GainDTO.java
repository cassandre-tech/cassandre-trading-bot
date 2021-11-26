package tech.cassandre.trading.bot.dto.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import static lombok.AccessLevel.PRIVATE;

/**
 * Gain.
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class GainDTO {

    /** Zero constant. */
    public static final GainDTO ZERO = GainDTO.builder()
            .percentage(0)
            .amount(CurrencyAmountDTO.ZERO)
            .fees(CurrencyAmountDTO.ZERO)
            .build();

    /** Gain made (percentage). */
    double percentage;

    /** Gain made (amount). */
    CurrencyAmountDTO amount;

    /** Fees. */
    CurrencyAmountDTO fees;

    /**
     * Getter netAmount.
     *
     * @return netAmount
     */
    public CurrencyAmountDTO getNetAmount() {
        if (amount != null && fees != null) {
            return CurrencyAmountDTO.builder()
                    .value(amount.getValue().subtract(fees.getValue()))
                    .currency(amount.getCurrency())
                    .build();
        } else {
            return CurrencyAmountDTO.ZERO;
        }
    }

    /**
     * Returns true if the current gain is inferior to the gain passed as a parameter.
     *
     * @param other other gain
     * @return true if this gain is inferior to the gain passed as a parameter
     */
    public boolean isInferiorTo(@NonNull final GainDTO other) {
        return getPercentage() < other.getPercentage();
    }

    /**
     * Returns true if the current gain is superior to the gain passed as a parameter.
     *
     * @param other other gain
     * @return true if this gain is superior to the gain passed as a parameter
     */
    public boolean isSuperiorTo(@NonNull final GainDTO other) {
        return getPercentage() > other.getPercentage();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GainDTO that = (GainDTO) o;
        return new EqualsBuilder()
                .append(this.amount, that.amount)
                .append(this.fees, that.fees)
                .isEquals();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(amount)
                .append(fees)
                .toHashCode();
    }

    @Override
    public final String toString() {
        if (percentage == 0) {
            return "No gain";
        } else {
            return "Gains: " + amount + " (" + percentage + " %) / Fees: " + fees;
        }
    }

}
