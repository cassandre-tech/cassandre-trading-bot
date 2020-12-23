package tech.cassandre.trading.bot.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.util.base.BaseDomain;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static javax.persistence.EnumType.STRING;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;

/**
 * Trade (used to save data between restarts).
 */
@Data
@Entity
@Table(name = "TRADES")
public class Trade extends BaseDomain {

    /** An identifier set by the exchange that uniquely identifies the trade. */
    @Id
    @Column(name = "ID")
    private String id;

    /** The id of the order responsible for execution of this trade. */
    @Column(name = "ORDER_ID", updatable = false)
    private String orderId;

    /** A bid or a ask. */
    @Enumerated(STRING)
    @Column(name = "TYPE")
    private OrderTypeDTO type;

    /** Amount to be ordered / amount that was ordered. */
    @Column(name = "AMOUNT", precision = PRECISION, scale = SCALE)
    private BigDecimal amount;

    /** The currency-pair. */
    @Column(name = "CURRENCY_PAIR")
    private String currencyPair;

    /** The price. */
    @Column(name = "PRICE", precision = PRECISION, scale = SCALE)
    private BigDecimal price;

    /** The timestamp of the trade. */
    @Column(name = "TIMESTAMP")
    private ZonedDateTime timestamp;

    /** The fee that was charged by the exchange for this trade. */
    @Column(name = "FEE_AMOUNT", precision = PRECISION, scale = SCALE)
    private BigDecimal feeAmount;

    /** The fee that was charged by the exchange for this trade. */
    @Column(name = "FEE_CURRENCY")
    private String feeCurrency;

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
                .append(this.orderId, that.orderId)
                .append(this.type, that.type)
                .append(this.amount, that.amount)
                .append(this.currencyPair, that.currencyPair)
                .append(this.price, that.price)
                .append(this.timestamp, that.timestamp)
                .append(this.feeAmount, that.feeAmount)
                .append(this.feeCurrency, that.feeCurrency)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

}
