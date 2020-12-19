package tech.cassandre.trading.bot.dto.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing a trade.
 * A trade is the action of buying and selling goods and services.
 * <p>
 * This is how it works :
 * - Received ticker - It means 1 Ether can be bought with 0.034797 Bitcoin
 * currencyPair=ETH/BTC
 * last=0.034797 (Last trade field is the price set during the last trade).
 * <p>
 * - Account before buying
 * BTC : 0.99963006
 * ETH : 10
 * <p>
 * - Buying 0.004 Bitcoin (should costs 0.05748 ether).
 * TradeDTO{currencyPair=ETH/BTC, originalAmount=0.004, price=0.034797}
 * <p>
 * - Account after buying
 * BTC : 0.99949078
 * ETH : 10.004
 * It cost me 0.00013928 BTC (0.99949078 - 0.99963006).
 * price * amount = 0.034797 * 0.004
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class TradeDTO {

    /** An identifier set by the exchange that uniquely identifies the trade. */
    String id;

    /** The id of the order responsible for execution of this trade. */
    String orderId;

    /** A bid or a ask. */
    OrderTypeDTO type;

    /** Amount to be ordered / amount that was ordered. */
    BigDecimal originalAmount;

    /** Currency-pair. */
    CurrencyPairDTO currencyPair;

    /** The price. */
    BigDecimal price;

    /** The timestamp of the order. */
    ZonedDateTime timestamp;

    /** The fee that was charged by the exchange for this trade. */
    CurrencyAmountDTO fee;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TradeDTO that = (TradeDTO) o;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.orderId, that.orderId)
                .append(this.type, that.type)
                .append(this.originalAmount, that.originalAmount)
                .append(this.currencyPair, that.currencyPair)
                .append(this.price, that.price)
                .append(this.timestamp, that.timestamp)
                .append(this.fee, that.fee)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

}
