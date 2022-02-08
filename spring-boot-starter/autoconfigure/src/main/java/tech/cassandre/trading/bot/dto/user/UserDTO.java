package tech.cassandre.trading.bot.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing user information on the exchange.
 * {@link UserDTO} can have several {@link AccountDTO} and each account can have several {@link BalanceDTO}.
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class UserDTO {

    /** User ID (usually the exchange username). */
    String id;

    /** The accounts owned by the user. */
    @Singular
    Map<String, AccountDTO> accounts;

    /** Information timestamp. */
    ZonedDateTime timestamp;

    /**
     * Find an account by its id.
     *
     * @param accountId account id
     * @return account
     */
    public Optional<AccountDTO> getAccountById(@NonNull final String accountId) {
        return accounts.values()
                .stream()
                .filter(accountDTO -> accountId.equals(accountDTO.getAccountId()))
                .findFirst();
    }

    /**
     * Getter timestamp.
     *
     * @return timestamp
     */
    public ZonedDateTime getTimestamp() {
        return Objects.requireNonNullElseGet(timestamp, ZonedDateTime::now);
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
        final UserDTO that = (UserDTO) o;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .isEquals();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    @SuppressWarnings("checkstyle:DesignForExtension")
    public int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

}
