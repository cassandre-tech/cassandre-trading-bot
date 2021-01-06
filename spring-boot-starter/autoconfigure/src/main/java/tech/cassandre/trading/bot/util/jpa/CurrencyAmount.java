package tech.cassandre.trading.bot.util.jpa;

import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;

/**
 * Currency amount (amount value + currency).
 */
@Embeddable
@SuppressWarnings("checkstyle:VisibilityModifier")
public class CurrencyAmount {

    /** Amount value. */
    @Column(precision = PRECISION, scale = SCALE)
    BigDecimal value;

    /** Amount currency. */
    String currency;

    public CurrencyAmount() {
    }

    /**
     * Constructor.
     *
     * @param currencyAmountDTO currency amount
     */
    public CurrencyAmount(final CurrencyAmountDTO currencyAmountDTO) {
        this.value = currencyAmountDTO.getValue();
        this.currency = currencyAmountDTO.getCurrency().toString();
    }

    /**
     * Getter value.
     *
     * @return value
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Setter value.
     *
     * @param newValue the value to set
     */
    public void setValue(final BigDecimal newValue) {
        value = newValue;
    }

    /**
     * Getter currency.
     *
     * @return currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Setter currency.
     *
     * @param newCurrency the currency to set
     */
    public void setCurrency(final String newCurrency) {
        currency = newCurrency;
    }

    @Override
    public final String toString() {
        return "CurrencyAmount{"
                + " value=" + value
                + ", currency='" + currency + '\''
                + '}';
    }

}
