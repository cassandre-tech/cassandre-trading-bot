package tech.cassandre.trading.bot.dto.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static java.math.BigDecimal.ZERO;
import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing an order.
 * A market order is a request by an investor to buy or sell in the current market.
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class OrderDTO {

    /** Technical id. */
    Long id;

    /** An identifier set by the exchange that uniquely identifies the order. */
    String orderId;

    /** Order type i.e. bid (buy) or ask (sell). */
    OrderTypeDTO type;

    /** The strategy that created the order. */
    StrategyDTO strategy;

    /** Currency pair. */
    CurrencyPairDTO currencyPair;

    /** Amount that was ordered. */
    CurrencyAmountDTO amount;

    /** Weighted Average price of the fills in the order. */
    CurrencyAmountDTO averagePrice;

    /** Limit price. */
    CurrencyAmountDTO limitPrice;

    /** Market price - The price Cassandre had when the order was created. */
    CurrencyAmountDTO marketPrice;

    /** The leverage to use for margin related to this order. */
    String leverage;

    /** Order status. */
    @NonFinal
    OrderStatusDTO status;

    /** Amount to be ordered / amount that has been matched against order on the order book/filled. */
    CurrencyAmountDTO cumulativeAmount;

    /** An identifier provided by the user on placement that uniquely identifies the order. */
    String userReference;

    /** The timestamp of the order. */
    ZonedDateTime timestamp;

    /** All trades related to order. */
    @Singular
    Set<TradeDTO> trades;

    /**
     * Allows you to manually update order status.
     *
     * @param newStatus new status
     */
    public final void updateStatus(final OrderStatusDTO newStatus) {
        status = newStatus;
    }

    /**
     * Returns trade from its id.
     *
     * @param tradeId trade id
     * @return trade
     */
    public final Optional<TradeDTO> getTrade(final String tradeId) {
        return trades.stream()
                .filter(t -> t.getTradeId().equals(tradeId))
                .findFirst();
    }

    /**
     * Returns amount value.
     *
     * @return amount value
     */
    // TODO Is it usefull ?
    public BigDecimal getAmountValue() {
        if (amount == null) {
            return null;
        } else {
            return amount.getValue();
        }
    }

    /**
     * Returns average price value.
     *
     * @return average price value.
     */
    // TODO Is it usefull ?
    public BigDecimal getAveragePriceValue() {
        if (averagePrice == null) {
            return null;
        } else {
            return averagePrice.getValue();
        }
    }

    /**
     * Returns limit price value.
     *
     * @return limit price value
     */
    // TODO Is it usefull ?
    public BigDecimal getLimitPriceValue() {
        if (limitPrice == null) {
            return null;
        } else {
            return limitPrice.getValue();
        }
    }

    /**
     * Returns market price.
     *
     * @return market price value
     */
    // TODO Is it usefull ?
    public BigDecimal getMarketPriceValue() {
        if (marketPrice == null) {
            return null;
        } else {
            return marketPrice.getValue();
        }
    }

    /**
     * Returns cumulative amount.
     *
     * @return cumulative amount.
     */
    // TODO Is it usefull ?
    public BigDecimal getCumulativeAmountValue() {
        if (cumulativeAmount == null) {
            return null;
        } else {
            return cumulativeAmount.getValue();
        }
    }

    /**
     * Returns true if the order has been fulfilled with trades.
     *
     * @return true if order completed
     */
    public final boolean isFulfilled() {
        return getTrades()
                .stream()
                .map(TradeDTO::getAmountValue)
                .reduce(ZERO, BigDecimal::add)
                .compareTo(getAmountValue()) == 0;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrderDTO that = (OrderDTO) o;
        return new EqualsBuilder()
                .append(this.orderId, that.orderId)
                .append(this.type, that.type)
                .append(this.currencyPair, that.currencyPair)
                .append(this.amount, that.amount)
                .append(this.averagePrice, that.averagePrice)
                .append(this.limitPrice, that.limitPrice)
                .append(this.leverage, that.leverage)
                .append(this.status, that.status)
                .append(this.cumulativeAmount, that.cumulativeAmount)
                .append(this.userReference, that.userReference)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(orderId)
                .toHashCode();
    }

}
