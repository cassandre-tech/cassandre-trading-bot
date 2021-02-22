package tech.cassandre.trading.bot.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.base.domain.BaseDomain;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Exchange account.
 */
@Data
@Entity
@Table(name = "EXCHANGE_ACCOUNTS")
public class ExchangeAccount extends BaseDomain {

    /** Technical ID. */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = IDENTITY)
    private long id;

    /** Exchange name. */
    @Column(name = "EXCHANGE")
    private String exchange;

    /** Exchange account. */
    @Column(name = "ACCOUNT")
    private String account;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ExchangeAccount that = (ExchangeAccount) o;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.exchange, that.exchange)
                .append(this.account, that.account)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .append(exchange)
                .append(account)
                .toHashCode();
    }

}
