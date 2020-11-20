package tech.cassandre.trading.bot.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;

/**
 * Order (used to save data between restarts).
 */
@Entity
@Table(name = "ORDERS")
public class Order {

    /** An identifier set by the exchange that uniquely identifies the order. */
    @Id
    @Column(name = "ID")
    private String id;

    /** Order type i.e. bid or ask. */
    @Column(name = "TYPE")
    private String type;

    /** Amount to be ordered / amount that was ordered. */
    @Column(name = "ORIGINAL_AMOUNT", precision = PRECISION, scale = SCALE)
    private BigDecimal originalAmount;

    /** The currency-pair. */
    @Column(name = "CURRENCY_PAIR")
    private String currencyPair;

    /** An identifier provided by the user on placement that uniquely identifies the order. */
    @Column(name = "USER_REFERENCE")
    private String userReference;

    /** The timestamp on the order according to the exchange's server, null if not provided. */
    @Column(name = "TIMESTAMP")
    private ZonedDateTime timestamp;

    /** Order status. */
    @Column(name = "STATUS")
    private String status;

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
    public String getType() {
        return type;
    }

    /**
     * Setter type.
     *
     * @param newType the type to set
     */
    public void setType(final String newType) {
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
    public String getStatus() {
        return status;
    }

    /**
     * Setter status.
     *
     * @param newStatus the status to set
     */
    public void setStatus(final String newStatus) {
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

}
