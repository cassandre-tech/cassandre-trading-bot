package tech.cassandre.trading.bot.dto.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.domain.Order;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing a trade.
 * A trade is the action of buying and selling goods and services.
 * <p>
 * This is how it works:
 * - Received ticker - It means 1 Ether can be bought with 0.034797 Bitcoin
 * currencyPair=ETH/BTC
 * last=0.034797 (Last trade field is the price set during the last trade).
 * <p>
 * - Account before buying
 * BTC: 0.99963006
 * ETH: 10
 * <p>
 * - Buying 0.004 Bitcoin (should cost 0.05748 ether).
 * TradeDTO{currencyPair=ETH/BTC, originalAmount=0.004, price=0.034797}
 * <p>
 * - Account after buying
 * BTC: 0.99949078
 * ETH: 10.004
 * It cost me 0.00013928 BTC (0.99949078 - 0.99963006).
 * price * amount = 0.034797 * 0.004
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class TradeDTO {

    /** Technical id. */
    Long uid;

    /** An identifier set by the exchange that uniquely identifies the trade. */
    String tradeId;

    /** Trade type i.e. bid (buy) or ask (sell). */
    OrderTypeDTO type;

    /** The order id of the order responsible for this trade. */
    String orderId;

    /** The order object responsible for this trade. */
    Order order;

    /** Currency pair. */
    CurrencyPairDTO currencyPair;

    /** Amount of the trade. */
    CurrencyAmountDTO amount;

    /** The price. */
    CurrencyAmountDTO price;

    /** The fee that was charged by the exchange for this order. */
    CurrencyAmountDTO fee;

    /** An identifier provided by the user on placement that uniquely identifies the order. */
    String userReference;

    /** The timestamp of the trade. */
    ZonedDateTime timestamp;

    /**
     * Returns amount value.
     *
     * @return amount value
     */
    public BigDecimal getAmountValue() {
        return Optional.ofNullable(amount).map(CurrencyAmountDTO::getValue).orElse(null);
    }

    /**
     * Returns price value.
     *
     * @return price value
     */
    public BigDecimal getPriceValue() {
        return Optional.ofNullable(price).map(CurrencyAmountDTO::getValue).orElse(null);
    }

    /**
     * Returns fee value.
     *
     * @return fee value
     */
    public BigDecimal getFeeValue() {
        return Optional.ofNullable(fee).map(CurrencyAmountDTO::getValue).orElse(null);
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    @SuppressWarnings("checkstyle:DesignForExtension")
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TradeDTO that = (TradeDTO) o;
        return new EqualsBuilder()
                .append(this.tradeId, that.tradeId)
                .append(this.type, that.type)
                .append(this.orderId, that.orderId)
                .append(this.currencyPair, that.currencyPair)
                .append(this.amount, that.amount)
                .append(this.price, that.price)
                .append(this.fee, that.fee)
                .append(this.timestamp, that.timestamp)
                .isEquals();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    @SuppressWarnings("checkstyle:DesignForExtension")
    public int hashCode() {
        return new HashCodeBuilder()
                .append(tradeId)
                .toHashCode();
    }

}
