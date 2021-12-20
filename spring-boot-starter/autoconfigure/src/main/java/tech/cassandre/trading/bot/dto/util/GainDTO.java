package tech.cassandre.trading.bot.dto.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /** Opening order fees (list coming from trade fees). */
    @Singular
    List<CurrencyAmountDTO> openingOrderFees;

    /** Closing order fees (list coming from trade fees). */
    @Singular
    List<CurrencyAmountDTO> closingOrderFees;

    /** Fees. */
    CurrencyAmountDTO fees;

    /**
     * Getter netAmount.
     *
     * @return netAmount
     * @Deprecated this method cannot be used as fees are not necessary the same currency as value.
     */
    @Deprecated
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
     * Getter fees.
     *
     * @return fees
     * @Deprecated This method should not be used anymore as a bug was found in issue 850.
     * A gain is linked to a position and a position has an opening order and a closing order.
     * the opening order trades and the closing order trades may have different currencies !
     * So it's not possible to return only a CurrencyAmountDTO !
     * Only a HashMap of currency and amount.
     */
    @Deprecated
    public final CurrencyAmountDTO getFees() {
        return fees;
    }

    /**
     * Returns the sum of fees from opening and closing orders.
     *
     * @return fees
     */
    public final Map<CurrencyDTO, CurrencyAmountDTO> getOrdersFees() {
        return Stream.concat(openingOrderFees.stream(), closingOrderFees.stream())
                .collect(Collectors.groupingBy(
                        CurrencyAmountDTO::getCurrency,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                CurrencyAmountDTO::getValue,
                                BigDecimal::add)))
                .entrySet()
                .stream()
                .map(currencyAmount -> new CurrencyAmountDTO(currencyAmount.getValue(), currencyAmount.getKey()))
                .collect(Collectors.toMap(CurrencyAmountDTO::getCurrency, Function.identity()));
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
                .append(this.openingOrderFees, that.openingOrderFees)
                .append(this.closingOrderFees, that.closingOrderFees)
                .isEquals();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(amount)
                .append(openingOrderFees)
                .append(closingOrderFees)
                .toHashCode();
    }

    @Override
    public final String toString() {
        if (percentage == 0) {
            return "No gain";
        } else {
            return "Gains: " + amount + " (" + percentage + " %)";
        }
    }

}
