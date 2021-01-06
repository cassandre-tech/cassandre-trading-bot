package tech.cassandre.trading.bot.dto.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

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

    /** Order type i.e. bid or ask. */
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

    /** The leverage to use for margin related to this order. */
    String leverage;

    /** Order status. */
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
     * Returns trade from its id.
     *
     * @param tradeId trade id
     * @return trade
     */
    public final Optional<TradeDTO> getTrade(final String tradeId) {
        if (tradeId == null) {
            return Optional.empty();
        } else {
            return trades.stream()
                    .filter(t -> tradeId.equals(t.getTradeId()))
                    .findFirst();
        }
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
        return  new HashCodeBuilder()
                .append(orderId)
                .toHashCode();
    }

}
