package tech.cassandre.trading.bot.domain;

import tech.cassandre.trading.bot.dto.trade.OrderStatusDTO;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.util.base.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;

/**
 * Order (used to save data between restarts).
 */
@Entity
@Table(name = "ORDERS")
public class Order extends BaseDomain {

    /** An identifier set by the exchange that uniquely identifies the order. */
    @Id
    @Column(name = "ID")
    private String id;

    /** Order type i.e. bid or ask. */
    @Enumerated(STRING)
    @Column(name = "TYPE")
    private OrderTypeDTO type;

    /** Amount to be ordered / amount that was ordered. */
    @Column(name = "ORIGINAL_AMOUNT", precision = PRECISION, scale = SCALE)
    private BigDecimal originalAmount;

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

    /** The total of the fees incurred for all transactions related to this order. */
    @Column(name = "FEE", precision = PRECISION, scale = SCALE)
    private BigDecimal fee;

    /** The leverage to use for margin related to this order. */
    @Column(name = "LEVERAGE")
    private String leverage;

    /** Limit price. */
    @Column(name = "LIMIT_PRICE", precision = PRECISION, scale = SCALE)
    private BigDecimal limitPrice;

    /** All trades related to order. */
    @OneToMany(fetch = EAGER)
    @JoinColumn(name = "ORDER_ID", updatable = false)
    private Set<Trade> trades = new HashSet<>();

    /** Strategy. */
    @ManyToOne(fetch = EAGER)
    @JoinColumn(name = "STRATEGY_ID", updatable = false)
    private Strategy strategy;

    /**
     * Getter id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter id.
     *
     * @param newId the id to set
     */
    public void setId(final String newId) {
        id = newId;
    }

    /**
     * Getter type.
     *
     * @return type
     */
    public OrderTypeDTO getType() {
        return type;
    }

    /**
     * Setter type.
     *
     * @param newType the type to set
     */
    public void setType(final OrderTypeDTO newType) {
        type = newType;
    }

    /**
     * Getter originalAmount.
     *
     * @return originalAmount
     */
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    /**
     * Setter originalAmount.
     *
     * @param newOriginalAmount the originalAmount to set
     */
    public void setOriginalAmount(final BigDecimal newOriginalAmount) {
        originalAmount = newOriginalAmount;
    }

    /**
     * Getter currencyPair.
     *
     * @return currencyPair
     */
    public String getCurrencyPair() {
        return currencyPair;
    }

    /**
     * Setter currencyPair.
     *
     * @param newCurrencyPair the currencyPair to set
     */
    public void setCurrencyPair(final String newCurrencyPair) {
        currencyPair = newCurrencyPair;
    }

    /**
     * Getter userReference.
     *
     * @return userReference
     */
    public String getUserReference() {
        return userReference;
    }

    /**
     * Setter userReference.
     *
     * @param newUserReference the userReference to set
     */
    public void setUserReference(final String newUserReference) {
        userReference = newUserReference;
    }

    /**
     * Getter timestamp.
     *
     * @return timestamp
     */
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Setter timestamp.
     *
     * @param newTimestamp the timestamp to set
     */
    public void setTimestamp(final ZonedDateTime newTimestamp) {
        timestamp = newTimestamp;
    }

    /**
     * Getter status.
     *
     * @return status
     */
    public OrderStatusDTO getStatus() {
        return status;
    }

    /**
     * Setter status.
     *
     * @param newStatus the status to set
     */
    public void setStatus(final OrderStatusDTO newStatus) {
        status = newStatus;
    }

    /**
     * Getter cumulativeAmount.
     *
     * @return cumulativeAmount
     */
    public BigDecimal getCumulativeAmount() {
        return cumulativeAmount;
    }

    /**
     * Setter cumulativeAmount.
     *
     * @param newCumulativeAmount the cumulativeAmount to set
     */
    public void setCumulativeAmount(final BigDecimal newCumulativeAmount) {
        cumulativeAmount = newCumulativeAmount;
    }

    /**
     * Getter averagePrice.
     *
     * @return averagePrice
     */
    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    /**
     * Setter averagePrice.
     *
     * @param newAveragePrice the averagePrice to set
     */
    public void setAveragePrice(final BigDecimal newAveragePrice) {
        averagePrice = newAveragePrice;
    }

    /**
     * Getter fee.
     *
     * @return fee
     */
    public BigDecimal getFee() {
        return fee;
    }

    /**
     * Setter fee.
     *
     * @param newFee the fee to set
     */
    public void setFee(final BigDecimal newFee) {
        fee = newFee;
    }

    /**
     * Getter leverage.
     *
     * @return leverage
     */
    public String getLeverage() {
        return leverage;
    }

    /**
     * Setter leverage.
     *
     * @param newLeverage the leverage to set
     */
    public void setLeverage(final String newLeverage) {
        leverage = newLeverage;
    }

    /**
     * Getter limitPrice.
     *
     * @return limitPrice
     */
    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    /**
     * Setter limitPrice.
     *
     * @param newLimitPrice the limitPrice to set
     */
    public void setLimitPrice(final BigDecimal newLimitPrice) {
        limitPrice = newLimitPrice;
    }

    /**
     * Getter trades.
     *
     * @return trades
     */
    public Set<Trade> getTrades() {
        return trades;
    }

    /**
     * Setter trades.
     *
     * @param newTrades the trades to set
     */
    public void setTrades(final Set<Trade> newTrades) {
        trades = newTrades;
    }

    /**
     * Getter strategy.
     *
     * @return strategy
     */
    public Strategy getStrategy() {
        return strategy;
    }

    /**
     * Setter strategy.
     *
     * @param newStrategy the strategy to set
     */
    public void setStrategy(final Strategy newStrategy) {
        strategy = newStrategy;
    }

    @Override
    public final String toString() {
        return "Order{"
                + " id='" + id + '\''
                + ", type=" + type
                + ", originalAmount=" + originalAmount
                + ", currencyPair='" + currencyPair + '\''
                + ", userReference='" + userReference + '\''
                + ", timestamp=" + timestamp
                + ", status=" + status
                + ", cumulativeAmount=" + cumulativeAmount
                + ", averagePrice=" + averagePrice
                + ", fee=" + fee
                + ", leverage='" + leverage + '\''
                + ", limitPrice=" + limitPrice
                + ", strategy=" + strategy
                + "}";
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return id.equals(order.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }
}
