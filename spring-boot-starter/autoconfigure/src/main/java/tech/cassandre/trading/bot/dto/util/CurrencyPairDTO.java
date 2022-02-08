package tech.cassandre.trading.bot.dto.util;

import lombok.Builder;
import lombok.Value;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

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
    public static final String CURRENCY_PAIR_SEPARATOR = "/";

    /** Currency pair default precision. */
    private static final Integer DEFAULT_CURRENCY_PRECISION = 8;

    /** The base currency is the first currency appearing in a currency pair quotation. */
    CurrencyDTO baseCurrency;

    /** The quote currency is the second currency appearing in a currency pair quotation. */
    CurrencyDTO quoteCurrency;

    /** The base currency precision. */
    int baseCurrencyPrecision;

    /** The quote currency precision. */
    int quoteCurrencyPrecision;

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
        this(CurrencyDTO.getInstance(newBaseCurrency), CurrencyDTO.getInstance(newQuoteCurrency), DEFAULT_CURRENCY_PRECISION, DEFAULT_CURRENCY_PRECISION);
    }

    /**
     * Constructor with {@link CurrencyDTO}.
     *
     * @param newBaseCurrency           The base currency
     * @param newQuoteCurrency          The quote currency
     * @param newBaseCurrencyPrecision  the base currency precision
     * @param newQuoteCurrencyPrecision the quote currency precision
     */
    public CurrencyPairDTO(final String newBaseCurrency, final String newQuoteCurrency, final int newBaseCurrencyPrecision, final int newQuoteCurrencyPrecision) {
        this(CurrencyDTO.getInstance(newBaseCurrency), CurrencyDTO.getInstance(newQuoteCurrency), newBaseCurrencyPrecision, newQuoteCurrencyPrecision);
    }

    /**
     * Constructor with String.
     *
     * @param newBaseCurrency  The base currency
     * @param newQuoteCurrency The quote currency
     */
    public CurrencyPairDTO(final CurrencyDTO newBaseCurrency, final CurrencyDTO newQuoteCurrency) {
        this(newBaseCurrency, newQuoteCurrency, DEFAULT_CURRENCY_PRECISION, DEFAULT_CURRENCY_PRECISION);
    }

    /**
     * Constructor with String.
     *
     * @param newBaseCurrency           The base currency
     * @param newQuoteCurrency          The quote currency
     * @param newBaseCurrencyPrecision  the base currency precision
     * @param newQuoteCurrencyPrecision the quote currency precision
     */
    public CurrencyPairDTO(final CurrencyDTO newBaseCurrency, final CurrencyDTO newQuoteCurrency, final int newBaseCurrencyPrecision, final int newQuoteCurrencyPrecision) {
        this.baseCurrency = newBaseCurrency;
        this.quoteCurrency = newQuoteCurrency;
        this.baseCurrencyPrecision = newBaseCurrencyPrecision;
        this.quoteCurrencyPrecision = newQuoteCurrencyPrecision;
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
        this.baseCurrencyPrecision = DEFAULT_CURRENCY_PRECISION;
        this.quoteCurrencyPrecision = DEFAULT_CURRENCY_PRECISION;
    }
    /**
     * Constructor from XChange instrument.
     *
     * @param instrument                instrument
     * @param newBaseCurrencyPrecision  the base currency precision
     * @param newQuoteCurrencyPrecision the quote currency precision
     */
    public CurrencyPairDTO(final Instrument instrument, final int newBaseCurrencyPrecision, final int newQuoteCurrencyPrecision) {
        final CurrencyPair cp = (CurrencyPair) instrument;
        this.baseCurrency = new CurrencyDTO(cp.base.getCurrencyCode());
        this.quoteCurrency = new CurrencyDTO(cp.counter.getCurrencyCode());
        this.baseCurrencyPrecision = newBaseCurrencyPrecision;
        this.quoteCurrencyPrecision = newQuoteCurrencyPrecision;
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
        final CurrencyPairDTO that = (CurrencyPairDTO) o;
        return getBaseCurrency().getCode().equalsIgnoreCase(that.getBaseCurrency().getCode())
                && getQuoteCurrency().getCode().equalsIgnoreCase(that.getQuoteCurrency().getCode());
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    @SuppressWarnings("checkstyle:DesignForExtension")
    public int hashCode() {
        return Objects.hash(getBaseCurrency().getCode(), getQuoteCurrency().getCode());
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public String toString() {
        return baseCurrency + CURRENCY_PAIR_SEPARATOR + quoteCurrency;
    }

}
