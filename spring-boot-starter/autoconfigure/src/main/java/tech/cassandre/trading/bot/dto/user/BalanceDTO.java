package tech.cassandre.trading.bot.dto.user;

import tech.cassandre.trading.bot.util.dto.CurrencyDTO;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * DTO representing a balance in a {@link CurrencyDTO} for an {@link AccountDTO}.
 */
public final class BalanceDTO {

    /** Currency. */
    private final CurrencyDTO currency;

    /** Returns the total amount of the <code>currency</code> in this balance. */
    private final BigDecimal total;

    /** Returns the amount of the <code>currency</code> in this balance that is available to trade. */
    private final BigDecimal available;

    /** Returns the frozen amount of the <code>currency</code> in this balance that is locked in trading. */
    private final BigDecimal frozen;

    /** Returns the loaned amount of the total <code>currency</code> in this balance that will be returned. */
    private final BigDecimal loaned;

    /** Returns the borrowed amount of the available <code>currency</code> in this balance that must be repaid. */
    private final BigDecimal borrowed;

    /** Returns the amount of the <code>currency</code> in this balance that is locked in withdrawal. */
    private final BigDecimal withdrawing;

    /** Returns the amount of the <code>currency</code> in this balance that is locked in the deposit. */
    private final BigDecimal depositing;

    /**
     * Builder constructor.
     *
     * @param builder builder
     */
    protected BalanceDTO(final BalanceDTO.Builder builder) {
        this.currency = builder.currency;
        this.total = builder.total;
        this.available = builder.available;
        this.frozen = builder.frozen;
        this.loaned = builder.loaned;
        this.borrowed = builder.borrowed;
        this.withdrawing = builder.withdrawing;
        this.depositing = builder.depositing;
    }

    /**
     * Returns builder.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Getter for currency.
     *
     * @return currency
     */
    public CurrencyDTO getCurrency() {
        return currency;
    }

    /**
     * Getter for total.
     *
     * @return total
     */
    public BigDecimal getTotal() {
        return total;
    }

    /**
     * Getter for available.
     *
     * @return available
     */
    public BigDecimal getAvailable() {
        return available;
    }

    /**
     * Getter for frozen.
     *
     * @return frozen
     */
    public BigDecimal getFrozen() {
        return frozen;
    }

    /**
     * Getter for loaned.
     *
     * @return loaned
     */
    public BigDecimal getLoaned() {
        return loaned;
    }

    /**
     * Getter for borrowed.
     *
     * @return borrowed
     */
    public BigDecimal getBorrowed() {
        return borrowed;
    }

    /**
     * Getter for withdrawing.
     *
     * @return withdrawing
     */
    public BigDecimal getWithdrawing() {
        return withdrawing;
    }

    /**
     * Getter for depositing.
     *
     * @return depositing
     */
    public BigDecimal getDepositing() {
        return depositing;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final BalanceDTO that = (BalanceDTO) o;
        return Objects.equals(getCurrency(), that.getCurrency())
                && Objects.equals(getTotal(), that.getTotal())
                && Objects.equals(getAvailable(), that.getAvailable())
                && Objects.equals(getFrozen(), that.getFrozen())
                && Objects.equals(getLoaned(), that.getLoaned())
                && Objects.equals(getBorrowed(), that.getBorrowed())
                && Objects.equals(getWithdrawing(), that.getWithdrawing())
                && Objects.equals(getDepositing(), that.getDepositing());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrency(), getTotal(), getAvailable(), getFrozen(), getLoaned(), getBorrowed(), getWithdrawing(), getDepositing());
    }

    @Override
    public String toString() {
        return "BalanceDTO{"
                + " currency=" + currency
                + ", total=" + total
                + ", available=" + available
                + ", frozen=" + frozen
                + ", loaned=" + loaned
                + ", borrowed=" + borrowed
                + ", withdrawing=" + withdrawing
                + ", depositing=" + depositing
                + '}';
    }

    /**
     * Builder.
     */
    public static final class Builder {

        /** Currency. */
        private CurrencyDTO currency;

        /** Returns the total amount of the <code>currency</code> in this balance. */
        private BigDecimal total = BigDecimal.ZERO;

        /** Returns the amount of the <code>currency</code> in this balance that is available to trade. */
        private BigDecimal available = BigDecimal.ZERO;

        /** Returns the frozen amount of the <code>currency</code> in this balance that is locked in trading. */
        private BigDecimal frozen = BigDecimal.ZERO;

        /** Returns the loaned amount of the total <code>currency</code> in this balance that will be returned. */
        private BigDecimal loaned = BigDecimal.ZERO;

        /** Returns the borrowed amount of the available <code>currency</code> in this balance that must be repaid. */
        private BigDecimal borrowed = BigDecimal.ZERO;

        /** Returns the amount of the <code>currency</code> in this balance that is locked in withdrawal. */
        private BigDecimal withdrawing = BigDecimal.ZERO;

        /** Returns the amount of the <code>currency</code> in this balance that is locked in the deposit. */
        private BigDecimal depositing = BigDecimal.ZERO;

        /**
         * Currency.
         *
         * @param newCurrency currency
         * @return builder
         */
        public Builder currency(final CurrencyDTO newCurrency) {
            this.currency = newCurrency;
            return this;
        }

        /**
         * Total.
         *
         * @param newTotal total
         * @return builder
         */
        public Builder total(final BigDecimal newTotal) {
            this.total = newTotal;
            return this;
        }

        /**
         * Available.
         *
         * @param newAvailable available
         * @return builder
         */
        public Builder available(final BigDecimal newAvailable) {
            this.available = newAvailable;
            return this;
        }

        /**
         * Frozen.
         *
         * @param newFrozen frozen
         * @return builder
         */
        public Builder frozen(final BigDecimal newFrozen) {
            this.frozen = newFrozen;
            return this;
        }

        /**
         * Loaned.
         *
         * @param newLoaned loaned
         * @return builder
         */
        public Builder loaned(final BigDecimal newLoaned) {
            this.loaned = newLoaned;
            return this;
        }

        /**
         * Borrowed.
         *
         * @param newBorrowed borrowed
         * @return builder
         */
        public Builder borrowed(final BigDecimal newBorrowed) {
            this.borrowed = newBorrowed;
            return this;
        }

        /**
         * Withdrawing.
         *
         * @param newWithdrawing withdrawing
         * @return builder
         */
        public Builder withdrawing(final BigDecimal newWithdrawing) {
            this.withdrawing = newWithdrawing;
            return this;
        }

        /**
         * Depositing.
         *
         * @param newDepositing depositing
         * @return builder
         */
        public Builder depositing(final BigDecimal newDepositing) {
            this.depositing = newDepositing;
            return this;
        }

        /**
         * Creates balance.
         *
         * @return balance
         */
        public BalanceDTO create() {
            return new BalanceDTO(this);
        }

    }

}
