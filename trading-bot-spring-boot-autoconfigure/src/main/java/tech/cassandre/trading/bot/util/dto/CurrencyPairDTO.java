package tech.cassandre.trading.bot.util.dto;

import java.util.Objects;

/**
 * Currency pair for trading.
 * The base currency represents how much of the quote currency to get one unit of the base currency.
 * For example, if you were looking at the CAD/USD currency pair, the Canadian dollar would be the base currency, and the U.S. dollar would be the quote currency.
 */
public final class CurrencyPairDTO {

    /** Currency pair separator. */
    private static final String CURRENCY_PAIR_SEPARATOR = "/";

    /** The base currency is the first currency appearing in a currency pair quotation. */
    private final CurrencyDTO baseCurrency;

    /** The quote currency is the second currency appearing in a currency pair quotation. */
    private final CurrencyDTO quoteCurrency;

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
     * Builder constructor with {@link CurrencyDTO}.
     *
     * @param builder builder
     */
    protected CurrencyPairDTO(final CurrencyPairDTO.Builder builder) {
        this(builder.baseCurrency, builder.quoteCurrency);
    }

    /**
     * Getter for baseCurrency.
     *
     * @return baseCurrency
     */
    public CurrencyDTO getBaseCurrency() {
        return baseCurrency;
    }

    /**
     * Getter for quoteCurrency.
     *
     * @return quoteCurrency
     */
    public CurrencyDTO getQuoteCurrency() {
        return quoteCurrency;
    }

    /**
     * Returns builder.
     *
     * @return builder
     */
    public static CurrencyPairDTO.Builder builder() {
        return new CurrencyPairDTO.Builder();
    }

    public static final class Builder {

        /**
         * The base currency is the first currency appearing in a currency pair quotation.
         */
        private CurrencyDTO baseCurrency;

        /**
         * The quote currency is the second currency appearing in a currency pair quotation.
         */
        private CurrencyDTO quoteCurrency;

        /**
         * Set baseCurrency.
         *
         * @param newBaseCurrency baseCurrency.
         * @return builder
         */
        public Builder baseCurrency(final CurrencyDTO newBaseCurrency) {
            this.baseCurrency = newBaseCurrency;
            return this;
        }

        /**
         * Set quoteCurrency.
         *
         * @param newQuoteCurrency quoteCurrency.
         * @return builder
         */
        public Builder quoteCurrency(final CurrencyDTO newQuoteCurrency) {
            this.quoteCurrency = newQuoteCurrency;
            return this;
        }

        /**
         * Creator.
         *
         * @return Account
         */
        public CurrencyPairDTO create() {
            return new CurrencyPairDTO(this);
        }

    }

    @Override
    public String toString() {
        return baseCurrency + CURRENCY_PAIR_SEPARATOR + quoteCurrency;
    }

    @Override
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
    public int hashCode() {
        return Objects.hash(getBaseCurrency().getCode(), getQuoteCurrency().getCode());
    }

}
