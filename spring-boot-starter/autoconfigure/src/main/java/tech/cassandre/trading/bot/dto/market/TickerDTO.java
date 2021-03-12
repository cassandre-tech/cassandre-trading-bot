package tech.cassandre.trading.bot.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing a stock ticker.
 * A ticker is a report of the price of certain securities, updated continuously throughout the trading session.
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class TickerDTO {

    /** Currency pair. */
    CurrencyPairDTO currencyPair;

    /** The opening price is the first trade price that was recorded during the day’s trading. */
    BigDecimal open;

    /** Last trade field is the price set during the last trade. */
    BigDecimal last;

    /** The bid price shown represents the highest bid price. */
    BigDecimal bid;

    /** The ask price shown represents the lowest bid price. */
    BigDecimal ask;

    /** The day’s high price. */
    BigDecimal high;

    /** The day’s low price. */
    BigDecimal low;

    /** Volume-weighted average price (VWAP) is the ratio of the value traded to total volume traded over a particular time horizon (usually one day). */
    BigDecimal vwap;

    /** Volume is the number of shares or contracts traded. */
    BigDecimal volume;

    /** Quote volume. */
    BigDecimal quoteVolume;

    /** The bid size represents the quantity of a security that investors are willing to purchase at a specified bid price. */
    BigDecimal bidSize;

    /** The ask size represents the quantity of a security that investors are willing to sell at a specified selling price. */
    BigDecimal askSize;

    /** Information timestamp. */
    ZonedDateTime timestamp;

    /**
     * Returns base currency.
     *
     * @return base currency
     */
    public CurrencyDTO getBaseCurrency() {
        if (currencyPair != null) {
            return currencyPair.getBaseCurrency();
        } else {
            return null;
        }
    }

    /**
     * Returns quote currency.
     *
     * @return quote currency
     */
    public CurrencyDTO getQuoteCurrency() {
        if (currencyPair != null) {
            return currencyPair.getQuoteCurrency();
        } else {
            return null;
        }
    }

    /**
     * Getter timestamp.
     *
     * @return timestamp
     */
    public final ZonedDateTime getTimestamp() {
        return Objects.requireNonNullElseGet(timestamp, ZonedDateTime::now);
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TickerDTO that = (TickerDTO) o;
        return new EqualsBuilder()
                .append(this.currencyPair, that.currencyPair)
                .append(this.getTimestamp(), that.getTimestamp())
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(currencyPair)
                .append(timestamp)
                .toHashCode();
    }

}
