package tech.cassandre.trading.bot.dto.user;

import tech.cassandre.trading.bot.util.dto.CurrencyDTO;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * DTO representing an account owned by a {@link UserDTO}.
 */
@SuppressWarnings("unused")
public final class AccountDTO {

    /** A unique identifier for this account. */
    private final String id;

    /** A descriptive name for this account. Defaults to {@link #id}. */
    private final String name;

    /** Account features. */
    private final Set<AccountFeatureDTO> features = new LinkedHashSet<>();

    /** Represents the different balances for each currency owned by the account. */
    private final Map<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();

    /**
     * Constructor.
     *
     * @param builder Builder.
     */
    protected AccountDTO(final AccountDTO.Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        if (builder.balances != null) {
            this.balances.putAll(builder.balances);
        }
        if (builder.features != null) {
            this.features.addAll(builder.features);
        }
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
     * Getter for id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for features.
     *
     * @return features
     */
    public Set<AccountFeatureDTO> getFeatures() {
        return features;
    }

    /**
     * Returns balance of a currency.
     *
     * @param currencyCode currency code
     * @return balance
     */
    public Optional<BalanceDTO> getBalance(final String currencyCode) {
        CurrencyDTO currency = CurrencyDTO.getInstanceNoCreate(currencyCode);
        if (currency == null) {
            return Optional.empty();
        } else {
            return getBalance(CurrencyDTO.getInstanceNoCreate(currencyCode));
        }
    }

    /**
     * Returns balance of a currency.
     *
     * @param currency currency
     * @return balance
     */
    public Optional<BalanceDTO> getBalance(final CurrencyDTO currency) {
        return Optional.ofNullable(balances.get(currency));
    }

    /**
     * Returns list of balances.
     *
     * @return balances
     */
    public Set<BalanceDTO> getBalances() {
        return new LinkedHashSet<>(balances.values());
    }


    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AccountDTO that = (AccountDTO) o;

        // Testing ID and Name.
        if (!Objects.equals(getId(), that.getId()) || !Objects.equals(getName(), that.getName())) {
            return false;
        }

        // Testing balances size.
        if (balances.size() != that.balances.size()) {
            return false;
        }

        // Testing balances.
        for (Map.Entry<CurrencyDTO, BalanceDTO> balance : balances.entrySet()) {
            Optional<BalanceDTO> balanceValue = that.getBalance(balance.getKey());
            // Checking that the list of currencies exists.
            if (balanceValue.isEmpty()) {
                // Did not find the cryptocurrency.
                return false;
            } else {
                // Check each balance.
                if (!balance.getValue().equals(balanceValue.get())) {
                    return false;
                }

            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getBalances());
    }

    @Override
    public String toString() {
        return "AccountDTO{"
                + " id='" + id + '\''
                + ", name='" + name + '\''
                + ", features=" + features
                + ", balances=" + balances
                + '}';
    }

    /**
     * Builder.
     */
    public static final class Builder {

        /** A unique identifier for this account. */
        private String id;

        /** A descriptive name for this account. Defaults to {@link #id}. */
        private String name;

        /** Account features. */
        private Set<AccountFeatureDTO> features = new LinkedHashSet<>();

        /** Represents the different currencies of the account. */
        private Map<CurrencyDTO, BalanceDTO> balances = new LinkedHashMap<>();

        /**
         * Id.
         *
         * @param newId id
         * @return builder
         */
        public Builder id(final String newId) {
            this.id = newId;
            return this;
        }

        /**
         * Name.
         *
         * @param newName name
         * @return builder
         */
        public Builder name(final String newName) {
            this.name = newName;
            return this;
        }

        /**
         * Features.
         *
         * @param newFeatures features
         * @return builder
         */
        public Builder features(final Set<AccountFeatureDTO> newFeatures) {
            this.features = newFeatures;
            return this;
        }

        /**
         * Balances.
         *
         * @param newBalances balances
         * @return builder
         */
        public Builder balances(final Map<CurrencyDTO, BalanceDTO> newBalances) {
            this.balances = newBalances;
            return this;
        }

        /**
         * Creates account.
         *
         * @return account
         */
        public AccountDTO create() {
            return new AccountDTO(this);
        }

    }

}
