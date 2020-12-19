package tech.cassandre.trading.bot.dto.util;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import java.math.BigDecimal;

/**
 * Currency amount (amount value + currency).
 */
public class CurrencyAmountDTO {

    /** Amount value. */
    private final BigDecimal value;

    /** Currency. */
    private final CurrencyDTO currency;

    /** Value provided. */
    private final boolean valueProvided;

    /**
     * Constructor for empty amount (0 USD).
     */
    public CurrencyAmountDTO() {
        this.valueProvided = false;
        this.value = new BigDecimal(0);
        this.currency = CurrencyDTO.USD;
    }

    /**
     * Constructor.
     *
     * @param newValue    amount value
     * @param newCurrency amount currency
     */
    public CurrencyAmountDTO(final BigDecimal newValue, final CurrencyDTO newCurrency) {
        this.valueProvided = true;
        this.value = newValue;
        this.currency = newCurrency;
    }

    /**
     * Getter for value.
     *
     * @return value
     */
    public final BigDecimal getValue() {
        return value;
    }

    /**
     * Getter for currency.
     *
     * @return currency
     */
    public final CurrencyDTO getCurrency() {
        return currency;
    }

    /**
     * Getter for valueProvided.
     *
     * @return valueProvided
     */
    public final boolean isValueProvided() {
        return valueProvided;
    }

    @Override
    public final boolean equals(final Object o) {
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
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(value)
                .append(currency)
                .toHashCode();
    }

    @Override
    public final String toString() {
        if (isValueProvided()) {
            return value.toString() + " " + currency;
        } else {
            return "Not provided";
        }
    }

}
