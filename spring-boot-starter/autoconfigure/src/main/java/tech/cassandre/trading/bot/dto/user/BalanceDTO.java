package tech.cassandre.trading.bot.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import java.math.BigDecimal;

import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing a balance in a {@link CurrencyDTO} for an {@link AccountDTO}.
 * {@link UserDTO} can have several {@link AccountDTO} and each account can have several {@link BalanceDTO}.
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class BalanceDTO {

    /** Currency. */
    CurrencyDTO currency;

    /** Returns the total amount of the {@link CurrencyDTO} in this balance. */
    BigDecimal total;

    /** Returns the amount of the {@link CurrencyDTO} in this balance that is available to trade. */
    BigDecimal available;

    /** Returns the frozen amount of the {@link CurrencyDTO} in this balance that is locked in trading. */
    BigDecimal frozen;

    /** Returns the loaned amount of the total {@link CurrencyDTO} in this balance that will be returned. */
    BigDecimal loaned;

    /** Returns the borrowed amount of the available {@link CurrencyDTO} in this balance that must be repaid. */
    BigDecimal borrowed;

    /** Returns the amount of the {@link CurrencyDTO} in this balance that is locked in withdrawal. */
    BigDecimal withdrawing;

    /** Returns the amount of the {@link CurrencyDTO} in this balance that is locked in the deposit. */
    BigDecimal depositing;

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
        final BalanceDTO that = (BalanceDTO) o;
        return new EqualsBuilder()
                .append(this.currency, that.currency)
                .append(this.total, that.total)
                .append(this.available, that.available)
                .append(this.frozen, that.frozen)
                .append(this.loaned, that.loaned)
                .append(this.borrowed, that.borrowed)
                .append(this.withdrawing, that.withdrawing)
                .append(this.depositing, that.depositing)
                .isEquals();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    @SuppressWarnings("checkstyle:DesignForExtension")
    public int hashCode() {
        return new HashCodeBuilder()
                .append(currency)
                .append(total)
                .append(available)
                .append(frozen)
                .append(loaned)
                .append(borrowed)
                .append(withdrawing)
                .append(depositing)
                .toHashCode();
    }

}
