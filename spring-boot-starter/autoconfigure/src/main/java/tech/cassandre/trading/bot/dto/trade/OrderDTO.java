package tech.cassandre.trading.bot.dto.trade;

import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * DTO representing order information.
 * A market order is a request by an investor to buy or sell in the current market.
 */
public final class OrderDTO {

    /** An identifier set by the exchange that uniquely identifies the order. */
    private final String id;

    /** Order type i.e. bid or ask. */
    private final OrderTypeDTO type;

    /** Amount to be ordered / amount that was ordered. */
    private final BigDecimal originalAmount;

    /** The currency-pair. */
    private final CurrencyPairDTO currencyPair;

    /** An identifier provided by the user on placement that uniquely identifies the order. */
    private final String userReference;

    /** The timestamp on the order according to the exchange's server, null if not provided. */
    private final ZonedDateTime timestamp;

    /** Order status. */
    private final OrderStatusDTO status;

    /** Amount to be ordered / amount that has been matched against order on the order book/filled. */
    private final BigDecimal cumulativeAmount;

    /** Weighted Average price of the fills in the order. */
    private final BigDecimal averagePrice;

    /** The total of the fees incurred for all transactions related to this order. */
    private final BigDecimal fee;

    /** The leverage to use for margin related to this order. */
    private final String leverage;

    /** Limit price. */
    private final BigDecimal limitPrice;

    /**
     * Builder constructor.
     *
     * @param builder builder
     */
    protected OrderDTO(final OrderDTO.Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.originalAmount = builder.originalAmount;
        this.currencyPair = builder.currencyPair;
        this.userReference = builder.userReference;
        this.timestamp = Objects.requireNonNullElseGet(builder.timestamp, ZonedDateTime::now);
        this.status = builder.status;
        this.cumulativeAmount = builder.cumulativeAmount;
        this.averagePrice = builder.averagePrice;
        this.fee = builder.fee;
        this.leverage = builder.leverage;
        this.limitPrice = builder.limitPrice;
    }

    /**
     * Returns builder.
     *
     * @return builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Getter for id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Getter for type.
     *
     * @return type
     */
    public OrderTypeDTO getType() {
        return type;
    }

    /**
     * Getter for originalAmount.
     *
     * @return originalAmount
     */
    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    /**
     * Getter for currencyPair.
     *
     * @return currencyPair
     */
    public CurrencyPairDTO getCurrencyPair() {
        return currencyPair;
    }

    /**
     * Getter for userReference.
     *
     * @return userReference
     */
    public String getUserReference() {
        return userReference;
    }

    /**
     * Getter for timestamp.
     *
     * @return timestamp
     */
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Getter for status.
     *
     * @return status
     */
    public OrderStatusDTO getStatus() {
        return status;
    }

    /**
     * Getter for cumulativeAmount.
     *
     * @return cumulativeAmount
     */
    public BigDecimal getCumulativeAmount() {
        return cumulativeAmount;
    }

    /**
     * Getter for averagePrice.
     *
     * @return averagePrice
     */
    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    /**
     * Getter for fee.
     *
     * @return fee
     */
    public BigDecimal getFee() {
        return fee;
    }

    /**
     * Getter for leverage.
     *
     * @return leverage
     */
    public String getLeverage() {
        return leverage;
    }

    /**
     * Getter for limitPrice.
     *
     * @return limitPrice
     */
    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrderDTO orderDTO = (OrderDTO) o;
        return getType() == orderDTO.getType()
                && (Objects.nonNull(getOriginalAmount()) && 0 == getOriginalAmount().compareTo(orderDTO.getOriginalAmount()))
                && Objects.equals(getCurrencyPair(), orderDTO.getCurrencyPair())
                && Objects.equals(getId(), orderDTO.getId())
                && Objects.equals(getUserReference(), orderDTO.getUserReference())
                // TODO Check if the truncate is necessary ?
                && getTimestamp().truncatedTo(SECONDS).isEqual(orderDTO.getTimestamp().truncatedTo(SECONDS))
                && getStatus() == orderDTO.getStatus()
                && (Objects.nonNull(getCumulativeAmount()) && 0 == getCumulativeAmount().compareTo(orderDTO.getCumulativeAmount()))
                && (Objects.nonNull(getAveragePrice()) && 0 == getAveragePrice().compareTo(orderDTO.getAveragePrice()))
                && (Objects.nonNull(getFee()) && 0 == getFee().compareTo(orderDTO.getFee()))
                && (Objects.nonNull(getLeverage()) && 0 == getLeverage().compareTo(orderDTO.getLeverage()))
                && (Objects.nonNull(getLimitPrice()) && 0 == getLimitPrice().compareTo(orderDTO.getLimitPrice()));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "OrderDTO{"
                + " type=" + type
                + ", originalAmount=" + originalAmount
                + ", currencyPair=" + currencyPair
                + ", id='" + id + '\''
                + ", userReference='" + userReference + '\''
                + ", timestamp=" + timestamp
                + ", status=" + status
                + ", cumulativeAmount=" + cumulativeAmount
                + ", averagePrice=" + averagePrice
                + ", fee=" + fee
                + ", leverage='" + leverage + '\''
                + ", limitPrice=" + limitPrice
                + '}';
    }

    /**
     * Builder.
     */
    public static final class Builder {

        /** Order type i.e. bid or ask. */
        private OrderTypeDTO type;

        /** Amount to be ordered / amount that was ordered. */
        private BigDecimal originalAmount;

        /** Currency-pair. */
        private CurrencyPairDTO currencyPair;

        /** An identifier set by the exchange that uniquely identifies the order. */
        private String id;

        /** An identifier provided by the user on placement that uniquely identifies the order. */
        private String userReference;

        /** The timestamp on the order according to the exchange's server, null if not provided. */
        private ZonedDateTime timestamp;

        /** Order status. */
        private OrderStatusDTO status;

        /** Amount to be ordered / amount that has been matched against order on the order book/filled. */
        private BigDecimal cumulativeAmount;

        /** Weighted Average price of the fills in the order. */
        private BigDecimal averagePrice;

        /** The total of the fees incurred for all transactions related to this order. */
        private BigDecimal fee;

        /** The leverage to use for margin related to this order. */
        private String leverage;

        /** Limit price. */
        private BigDecimal limitPrice;

        /**
         * Type.
         *
         * @param newType type
         * @return builder
         */
        public Builder type(final OrderTypeDTO newType) {
            this.type = newType;
            return this;
        }

        /**
         * The original amount.
         *
         * @param newOriginalAmount original amount
         * @return builder
         */
        public Builder originalAmount(final BigDecimal newOriginalAmount) {
            this.originalAmount = newOriginalAmount;
            return this;
        }

        /**
         * Currency pair.
         *
         * @param newCurrencyPair currency pair
         * @return builder
         */
        public Builder currencyPair(final CurrencyPairDTO newCurrencyPair) {
            this.currencyPair = newCurrencyPair;
            return this;
        }

        /**
         * Id.
         *
         * @param newId id
         * @return builder
         */
        public Builder id(final String newId) {
            this.id = newId;
            return this;
        }

        /**
         * User reference.
         *
         * @param newUserReference user reference
         * @return builder
         */
        public Builder userReference(final String newUserReference) {
            this.userReference = newUserReference;
            return this;
        }

        /**
         * Timestamp.
         *
         * @param newTimestamp timestamp
         * @return builder
         */
        public Builder timestamp(final ZonedDateTime newTimestamp) {
            this.timestamp = newTimestamp;
            return this;
        }

        /**
         * Status.
         *
         * @param newStatus status
         * @return builder
         */
        public Builder status(final OrderStatusDTO newStatus) {
            this.status = newStatus;
            return this;
        }

        /**
         * The cumulative amount.
         *
         * @param newCumulativeAmount cumulative amount
         * @return builder
         */
        public Builder cumulativeAmount(final BigDecimal newCumulativeAmount) {
            this.cumulativeAmount = newCumulativeAmount;
            return this;
        }

        /**
         * Average price.
         *
         * @param newAveragePrice average price
         * @return builder
         */
        public Builder averagePrice(final BigDecimal newAveragePrice) {
            this.averagePrice = newAveragePrice;
            return this;
        }

        /**
         * Fee.
         *
         * @param newFee fee.
         * @return builder
         */
        public Builder fee(final BigDecimal newFee) {
            this.fee = newFee;
            return this;
        }

        /**
         * Leverage.
         *
         * @param newLeverage leverage
         * @return builder
         */
        public Builder leverage(final String newLeverage) {
            this.leverage = newLeverage;
            return this;
        }

        /**
         * Limit price.
         *
         * @param newLimitPrice limit price
         * @return builder
         */
        public Builder limitPrice(final BigDecimal newLimitPrice) {
            this.limitPrice = newLimitPrice;
            return this;
        }

        /**
         * Creates order.
         *
         * @return order
         */
        public OrderDTO create() {
            return new OrderDTO(this);
        }

    }

}
