package tech.cassandre.trading.bot.util.dto;

import java.math.BigDecimal;

/**
 * Amount (amount value + currency).
 */
@SuppressWarnings("unused")
public class AmountDTO {

    /** Amount value. */
    private final BigDecimal value;

    /** Currency. */
    private final CurrencyDTO currency;

    /** Value provided. */
    private final boolean valueProvided;

    /**
     * Constructor for empty amount (0 USD).
     */
    public AmountDTO() {
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
    public AmountDTO(final BigDecimal newValue, final CurrencyDTO newCurrency) {
        this.valueProvided = true;
        this.value = newValue;
        this.currency = newCurrency;
    }

    /**
     * Getter value.
     *
     * @return value
     */
    public final BigDecimal getValue() {
        return value;
    }

    /**
     * Getter currency.
     *
     * @return currency
     */
    public final CurrencyDTO getCurrency() {
        return currency;
    }

    /**
     * Getter valueProvided.
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
