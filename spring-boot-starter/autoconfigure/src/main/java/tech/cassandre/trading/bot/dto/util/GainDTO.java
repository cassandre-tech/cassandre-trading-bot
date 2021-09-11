package tech.cassandre.trading.bot.dto.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

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
    public boolean isInferiorTo(final GainDTO other) {
        if (other != null) {
            return getPercentage() < other.getPercentage();
        } else {
            return false;
        }
    }

    /**
     * Returns true if the current gain is superior to the gain passed as a parameter.
     *
     * @param other other gain
     * @return true if this gain is superior to the gain passed as a parameter
     */
    public boolean isSuperiorTo(final GainDTO other) {
        if (other != null) {
            return getPercentage() > other.getPercentage();
        } else {
            return false;
        }
    }

    @Override
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
