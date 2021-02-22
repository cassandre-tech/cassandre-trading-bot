package tech.cassandre.trading.bot.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing an account owned by a {@link UserDTO}.
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class AccountDTO {

    /** A unique identifier for this account. */
    String accountId;

    /** A descriptive name for this account. Defaults to {@link #accountId}. */
    String name;

    /** Account features. */
    @Singular
    Set<AccountFeatureDTO> features;

    /** Represents the different balances for each currency owned by the account. */
    @Singular
    Map<CurrencyDTO, BalanceDTO> balances;

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

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AccountDTO that = (AccountDTO) o;
        // Test accounts.
        boolean equals = new EqualsBuilder()
                .append(this.accountId, that.accountId)
                .append(this.name, that.name)
                .append(this.balances.size(), that.balances.size())
                .isEquals();
        // Tests balances.
        if (equals) {
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
        }
        return equals;
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(accountId)
                .append(name)
                .toHashCode();
    }

}
