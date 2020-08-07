package tech.cassandre.trading.bot.util.dto;

import java.math.BigDecimal;

/**
 * Currency amount (amount value + currency).
 */
@SuppressWarnings("unused")
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
    public final String toString() {
        if (isValueProvided()) {
            return value + " " + currency;
        } else {
            return "Not provided";
        }
    }

}
