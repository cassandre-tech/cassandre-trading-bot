package tech.cassandre.trading.bot.domain;

import lombok.Data;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;
import tech.cassandre.trading.bot.dto.position.PositionTypeDTO;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Position.
 */
@Data
@Entity
@Table(name = "POSITIONS")
public class Position extends BaseDomain {

    /** Technical ID. */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /** An identifier that uniquely identifies the position for a strategy. */
    @Column(name = "POSITION_ID")
    private Long positionId;

    /** Position type - Short or Long. */
    @Enumerated(STRING)
    @Column(name = "TYPE")
    private PositionTypeDTO type;

    /** The strategy that created the position. */
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "FK_STRATEGY_ID", updatable = false)
    private Strategy strategy;

    /** The currency-pair. */
    @Column(name = "CURRENCY_PAIR")
    private String currencyPair;

    /** Amount that was ordered. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "AMOUNT_VALUE")),
            @AttributeOverride(name = "currency", column = @Column(name = "AMOUNT_CURRENCY"))
    })
    private CurrencyAmount amount;

    /** Position rules - stop gain percentage. */
    @Column(name = "RULES_STOP_GAIN_PERCENTAGE")
    private Float stopGainPercentageRule;

    /** Position rules - stop loss percentage. */
    @Column(name = "RULES_STOP_LOSS_PERCENTAGE")
    private Float stopLossPercentageRule;

    /** Position status. */
    @Enumerated(STRING)
    @Column(name = "STATUS")
    private PositionStatusDTO status;

    /** Indicates that the position must be closed no matter the rules. */
    @Column(name = "FORCE_CLOSING")
    private boolean forceClosing;

    /** The order created to open the position. */
    @OneToOne(fetch = EAGER, cascade = ALL)
    @JoinColumn(name = "FK_OPENING_ORDER_ID")
    private Order openingOrder;

    /** The order created to close the position. */
    @OneToOne(fetch = EAGER, cascade = ALL)
    @JoinColumn(name = "FK_CLOSING_ORDER_ID")
    private Order closingOrder;

    /** Price of lowest gain reached by this position. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "LOWEST_GAIN_PRICE_VALUE")),
            @AttributeOverride(name = "currency", column = @Column(name = "LOWEST_GAIN_PRICE_CURRENCY"))
    })
    private CurrencyAmount lowestGainPrice;

    /** Price of highest gain reached by this position. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "HIGHEST_GAIN_PRICE_VALUE")),
            @AttributeOverride(name = "currency", column = @Column(name = "HIGHEST_GAIN_PRICE_CURRENCY"))
    })
    private CurrencyAmount highestGainPrice;

    /** Price of latest gain price for this position. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "LATEST_GAIN_PRICE_VALUE")),
            @AttributeOverride(name = "currency", column = @Column(name = "LATEST_GAIN_PRICE_CURRENCY"))
    })
    private CurrencyAmount latestGainPrice;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Position that = (Position) o;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.positionId, that.positionId)
                .append(this.type, that.type)
                .append(this.currencyPair, that.currencyPair)
                .append(this.amount, that.amount)
                .append(this.stopGainPercentageRule, that.stopGainPercentageRule)
                .append(this.stopLossPercentageRule, that.stopLossPercentageRule)
                .append(this.status, that.status)
                .append(this.forceClosing, that.forceClosing)
                .append(this.openingOrder, that.openingOrder)
                .append(this.closingOrder, that.closingOrder)
                .append(this.lowestGainPrice, that.lowestGainPrice)
                .append(this.highestGainPrice, that.highestGainPrice)
                .append(this.latestGainPrice, that.latestGainPrice)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

}
