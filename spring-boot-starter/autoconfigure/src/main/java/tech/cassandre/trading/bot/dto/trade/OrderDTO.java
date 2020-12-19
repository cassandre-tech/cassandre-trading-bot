package tech.cassandre.trading.bot.dto.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing order information.
 * A market order is a request by an investor to buy or sell in the current market.
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class OrderDTO {

    /** An identifier set by the exchange that uniquely identifies the order. */
    String id;

    /** Order type i.e. bid or ask. */
    OrderTypeDTO type;

    /** Amount to be ordered / amount that was ordered. */
    BigDecimal originalAmount;

    /** Currency-pair. */
    CurrencyPairDTO currencyPair;

    /** An identifier provided by the user on placement that uniquely identifies the order. */
    String userReference;

    /** The timestamp of the order. */
    ZonedDateTime timestamp;

    /** Order status. */
    OrderStatusDTO status;

    /** Amount to be ordered / amount that has been matched against order on the order book/filled. */
    BigDecimal cumulativeAmount;

    /** Weighted Average price of the fills in the order. */
    BigDecimal averagePrice;

    /** The total of the fees incurred for all transactions related to this order. */
    BigDecimal fee;

    /** The leverage to use for margin related to this order. */
    String leverage;

    /** Limit price. */
    BigDecimal limitPrice;

    /** All trades related to order. */
    Set<TradeDTO> trades;

    /** Strategy. */
    StrategyDTO strategy;

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
                .append(this.id, that.id)
                .append(this.type, that.type)
                .append(this.originalAmount, that.originalAmount)
                .append(this.currencyPair, that.currencyPair)
                .append(this.userReference, that.userReference)
                .append(this.timestamp, that.timestamp)
                .append(this.status, that.status)
                .append(this.cumulativeAmount, that.cumulativeAmount)
                .append(this.averagePrice, that.averagePrice)
                .append(this.fee, that.fee)
                .append(this.leverage, that.leverage)
                .append(this.limitPrice, that.limitPrice)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return  new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

}
