package tech.cassandre.trading.bot.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.util.base.domain.BaseDomain;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.jpa.CurrencyAmount;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Trade.
 */
@Data
@Entity
@Table(name = "TRADES")
public class Trade extends BaseDomain {

    /** Technical ID. */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /** An identifier set by the exchange that uniquely identifies the trade. */
    @Column(name = "TRADE_ID")
    private String tradeId;

    /** Trade type i.e. bid (buy) or ask (sell). */
    @Enumerated(STRING)
    @Column(name = "TYPE")
    private OrderTypeDTO type;

    /** The order responsible of this trade. */
    @ManyToOne
    @JoinColumn(name = "FK_ORDER_ID", nullable = false)
    private Order order;

    /** Currency pair. */
    @Column(name = "CURRENCY_PAIR")
    private String currencyPair;

    /** Amount that was ordered. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "AMOUNT_VALUE")),
            @AttributeOverride(name = "currency", column = @Column(name = "AMOUNT_CURRENCY"))
    })
    private CurrencyAmount amount;

    /** The price. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "PRICE_VALUE")),
            @AttributeOverride(name = "currency", column = @Column(name = "PRICE_CURRENCY"))
    })
    private CurrencyAmount price;

    /** The fee that was charged by the exchange for this trade. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "FEE_VALUE")),
            @AttributeOverride(name = "currency", column = @Column(name = "FEE_CURRENCY"))
    })
    private CurrencyAmount fee;

    /** An identifier provided by the user on placement that uniquely identifies the order of this trade. */
    @Column(name = "USER_REFERENCE")
    private String userReference;

    /** The timestamp of the trade. */
    @Column(name = "TIMESTAMP")
    private ZonedDateTime timestamp;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Trade that = (Trade) o;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.tradeId, that.tradeId)
                .append(this.type, that.type)
                .append(this.currencyPair, that.currencyPair)
                .append(this.amount, that.amount)
                .append(this.price, that.price)
                .append(this.fee, that.fee)
                .append(this.userReference, that.userReference)
                .append(this.timestamp, that.timestamp)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(tradeId)
                .toHashCode();
    }

}
