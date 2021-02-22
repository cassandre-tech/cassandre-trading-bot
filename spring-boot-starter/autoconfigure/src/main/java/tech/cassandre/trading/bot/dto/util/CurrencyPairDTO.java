package tech.cassandre.trading.bot.dto.util;

import lombok.Builder;
import lombok.Value;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;

import java.util.Objects;

/**
 * Currency pair for trading.
 * The base currency represents how much of the quote currency to get one unit of the base currency.
 * For example, if you were looking at the CAD/USD currency pair, the Canadian dollar would be the base currency, and the U.S. dollar would be the quote currency.
 */
@Value
@Builder
@SuppressWarnings("checkstyle:VisibilityModifier")
public class CurrencyPairDTO {

    /** Currency pair separator. */
    private static final String CURRENCY_PAIR_SEPARATOR = "/";

    /** The base currency is the first currency appearing in a currency pair quotation. */
    CurrencyDTO baseCurrency;

    /** The quote currency is the second currency appearing in a currency pair quotation. */
    CurrencyDTO quoteCurrency;

    /**
     * Constructor.
     *
     * @param currencyPair currency pair
     */
    public CurrencyPairDTO(final String currencyPair) {
        this(currencyPair.split(CURRENCY_PAIR_SEPARATOR)[0], currencyPair.split(CURRENCY_PAIR_SEPARATOR)[1]);
    }

    /**
     * Constructor.
     *
     * @param currencyPair currency pair
     */
    public CurrencyPairDTO(final CurrencyPair currencyPair) {
        this(currencyPair.base.toString(), currencyPair.counter.toString());
    }

    /**
     * Constructor with {@link CurrencyDTO}.
     *
     * @param newBaseCurrency  The base currency
     * @param newQuoteCurrency The quote currency
     */
    public CurrencyPairDTO(final String newBaseCurrency, final String newQuoteCurrency) {
        this(CurrencyDTO.getInstance(newBaseCurrency), CurrencyDTO.getInstance(newQuoteCurrency));
    }

    /**
     * Constructor with String.
     *
     * @param newBaseCurrency  The base currency
     * @param newQuoteCurrency The quote currency
     */
    public CurrencyPairDTO(final CurrencyDTO newBaseCurrency, final CurrencyDTO newQuoteCurrency) {
        this.baseCurrency = newBaseCurrency;
        this.quoteCurrency = newQuoteCurrency;
    }

    /**
     * Constructor from XChange instrument.
     *
     * @param instrument instrument
     */
    public CurrencyPairDTO(final Instrument instrument) {
        final CurrencyPair cp = (CurrencyPair) instrument;
        this.baseCurrency = new CurrencyDTO(cp.base.getCurrencyCode());
        this.quoteCurrency = new CurrencyDTO(cp.counter.getCurrencyCode());
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CurrencyPairDTO that = (CurrencyPairDTO) o;
        return getBaseCurrency().getCode().equalsIgnoreCase(that.getBaseCurrency().getCode())
                && getQuoteCurrency().getCode().equalsIgnoreCase(that.getQuoteCurrency().getCode());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getBaseCurrency().getCode(), getQuoteCurrency().getCode());
    }

    @Override
    public final String toString() {
        return baseCurrency + CURRENCY_PAIR_SEPARATOR + quoteCurrency;
    }

}
