package tech.cassandre.trading.bot.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;

/**
 * Trade (used to save data between restarts).
 */
@Entity
@Table(name = "TRADES")
public class Trade {

    /** An identifier set by the exchange that uniquely identifies the trade. */
    @Id
    @Column(name = "ID")
    private String id;

    /** The id of the order responsible for execution of this trade. */
    @Column(name = "ORDER_ID")
    private String orderId;

    /** A bid or a ask. */
    @Column(name = "ORDER_TYPE")
    private String type;

    /** Amount to be ordered / amount that was ordered. */
    @Column(name = "ORIGINAL_AMOUNT", precision = PRECISION, scale = SCALE)
    private BigDecimal originalAmount;

    /** The currency-pair. */
    @Column(name = "CURRENCY_PAIR")
    private String currencyPair;

    /** The price. */
    @Column(name = "PRICE", precision = PRECISION, scale = SCALE)
    private BigDecimal price;

    /** The timestamp on the order according to the exchange's server, null if not provided. */
    // TODO Change field name and java code ?
    @Column(name = "ORDER_TIMESTAMP")
    private ZonedDateTime timestamp;

    /** The fee that was charged by the exchange for this trade. */
    @Column(name = "FEE_AMOUNT", precision = PRECISION, scale = SCALE)
    private BigDecimal feeAmount;

    /** The fee that was charged by the exchange for this trade. */
    @Column(name = "FEE_CURRENCY")
    private String feeCurrency;

    /** Position using this trade. */
    @ManyToOne
    @JoinColumn(name = "POSITION_ID")
    private Position position;

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
     * Getter orderId.
     *
     * @return orderId
     */
    public String getOrderId() {
        return orderId;
    }

    /**
     * Setter orderId.
     *
     * @param newOrderId the orderId to set
     */
    public void setOrderId(final String newOrderId) {
        orderId = newOrderId;
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
     * Getter price.
     *
     * @return price
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Setter price.
     *
     * @param newPrice the price to set
     */
    public void setPrice(final BigDecimal newPrice) {
        price = newPrice;
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
     * Getter feeAmount.
     *
     * @return feeAmount
     */
    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    /**
     * Setter feeAmount.
     *
     * @param newFeeAmount the feeAmount to set
     */
    public void setFeeAmount(final BigDecimal newFeeAmount) {
        feeAmount = newFeeAmount;
    }

    /**
     * Getter feeCurrency.
     *
     * @return feeCurrency
     */
    public String getFeeCurrency() {
        return feeCurrency;
    }

    /**
     * Setter feeCurrency.
     *
     * @param newFeeCurrency the feeCurrency to set
     */
    public void setFeeCurrency(final String newFeeCurrency) {
        feeCurrency = newFeeCurrency;
    }

    /**
     * Getter position.
     *
     * @return position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Setter position.
     *
     * @param newPosition the position to set
     */
    public void setPosition(final Position newPosition) {
        position = newPosition;
    }

    @Override
    public final String toString() {
        return "Trade{"
                + " id='" + id + '\''
                + ", orderId='" + orderId + '\''
                + ", type='" + type + '\''
                + ", originalAmount=" + originalAmount
                + ", currencyPair='" + currencyPair + '\''
                + ", price=" + price
                + ", timestamp=" + timestamp
                + ", feeAmount=" + feeAmount
                + ", feeCurrency='" + feeCurrency + '\''
                + '}';
    }

}
