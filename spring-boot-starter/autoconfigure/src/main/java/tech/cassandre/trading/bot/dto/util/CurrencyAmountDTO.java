package tech.cassandre.trading.bot.dto.util;

import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import java.math.BigDecimal;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;

/**
 * Currency amount (amount value and currency).
 */
@Value
@Builder
@SuppressWarnings("checkstyle:VisibilityModifier")
public class CurrencyAmountDTO {

    /** Zero. */
    public static final CurrencyAmountDTO ZERO = CurrencyAmountDTO.builder()
            .value(BigDecimal.ZERO)
            .currency(BTC)
            .build();

    /** Amount value. */
    BigDecimal value;

    /** Currency. */
    CurrencyDTO currency;

    /**
     * Constructor.
     *
     * @param newValue    amount value
     * @param newCurrency amount currency
     */
    public CurrencyAmountDTO(final String newValue, final CurrencyDTO newCurrency) {
        if (newValue != null && newCurrency != null) {
            this.value = new BigDecimal(newValue);
            this.currency = newCurrency;
        } else {
            this.value = new BigDecimal(0);
            this.currency = BTC;
        }
    }

    /**
     * Constructor.
     *
     * @param newValue    amount value
     * @param newCurrency amount currency
     */
    public CurrencyAmountDTO(final BigDecimal newValue, final CurrencyDTO newCurrency) {
        if (newValue != null && newCurrency != null) {
            this.value = newValue;
            this.currency = newCurrency;
        } else {
            this.value = new BigDecimal(0);
            this.currency = BTC;
        }
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    @SuppressWarnings("checkstyle:DesignForExtension")
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CurrencyAmountDTO that = (CurrencyAmountDTO) o;
        return new EqualsBuilder()
                .append(this.value, that.value)
                .append(this.currency, that.currency)
                .isEquals();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    @SuppressWarnings("checkstyle:DesignForExtension")
    public int hashCode() {
        return new HashCodeBuilder()
                .append(value)
                .append(currency)
                .toHashCode();
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public String toString() {
        return value.stripTrailingZeros().toPlainString() + " " + currency;
    }

}
