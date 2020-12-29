package tech.cassandre.trading.bot.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.util.base.BaseDomain;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;

/**
 * Order (used to save data between restarts).
 */
@Data
@Entity
@Table(name = "ORDERS")
public class Order extends BaseDomain {

    /** Technical ID. */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /** An identifier set by the exchange that uniquely identifies the order. */
    @Column(name = "ORDER_ID")
    private String orderId;

    /** Order type i.e. bid or ask. */
    @Enumerated(STRING)
    @Column(name = "TYPE")
    private OrderTypeDTO type;

    /** Amount to be ordered / amount that was ordered. */
    @Column(name = "AMOUNT", precision = PRECISION, scale = SCALE)
    private BigDecimal amount;

    /** The currency-pair. */
    @Column(name = "CURRENCY_PAIR")
    private String currencyPair;

    /** An identifier provided by the user on placement that uniquely identifies the order. */
    @Column(name = "USER_REFERENCE")
    private String userReference;

    /** The timestamp of the order. */
    @Column(name = "TIMESTAMP")
    private ZonedDateTime timestamp;

    /** Order status. */
    @Enumerated(STRING)
    @Column(name = "STATUS")
    private OrderStatusDTO status;

    /** Amount to be ordered / amount that has been matched against order on the order book/filled. */
    @Column(name = "CUMULATIVE_AMOUNT", precision = PRECISION, scale = SCALE)
    private BigDecimal cumulativeAmount;

    /** Weighted Average price of the fills in the order. */
    @Column(name = "AVERAGE_PRICE", precision = PRECISION, scale = SCALE)
    private BigDecimal averagePrice;

    /** The leverage to use for margin related to this order. */
    @Column(name = "LEVERAGE")
    private String leverage;

    /** Limit price. */
    @Column(name = "LIMIT_PRICE", precision = PRECISION, scale = SCALE)
    private BigDecimal limitPrice;

    /** All trades related to order. */
    @OneToMany(fetch = EAGER)
    @OrderBy("timestamp")
    @JoinColumn(name = "FK_ORDER_ID", updatable = false)
    private Set<Trade> trades = new LinkedHashSet<>();

    /** Strategy. */
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "FK_STRATEGY_ID", updatable = false)
    private Strategy strategy;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Order that = (Order) o;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.orderId, that.orderId)
                .append(this.type, that.type)
                .append(this.amount, that.amount)
                .append(this.currencyPair, that.currencyPair)
                .append(this.userReference, that.userReference)
                .append(this.timestamp, that.timestamp)
                .append(this.status, that.status)
                .append(this.cumulativeAmount, that.cumulativeAmount)
                .append(this.averagePrice, that.averagePrice)
                .append(this.leverage, that.leverage)
                .append(this.limitPrice, that.limitPrice)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

}
