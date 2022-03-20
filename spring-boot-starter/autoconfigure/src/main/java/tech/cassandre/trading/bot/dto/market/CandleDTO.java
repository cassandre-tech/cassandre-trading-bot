package tech.cassandre.trading.bot.dto.market;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing a candle.
 * A candle displays the high, low, open, and closing prices of a security for a specific period.
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class CandleDTO {

    /** Currency pair. */
    CurrencyPairDTO currencyPair;

    /** Opening price (first trade) in the bucket interval. */
    BigDecimal open;

    /** Highest price during the bucket interval. */
    BigDecimal high;

    /** Lowest price during the bucket interval. */
    BigDecimal low;

    /** Closing price (last trade) in the bucket interval. */
    BigDecimal close;

    /** Volume of trading activity during the bucket interval. */
    BigDecimal volume;

    /** Bucket start time. */
    ZonedDateTime timestamp;

    /**
     * Returns base currency.
     *
     * @return base currency
     */
    public CurrencyDTO getBaseCurrency() {
        return Optional.ofNullable(currencyPair).map(CurrencyPairDTO::getBaseCurrency).orElse(null);
    }

    /**
     * Returns quote currency.
     *
     * @return quote currency
     */
    public CurrencyDTO getQuoteCurrency() {
        return Optional.ofNullable(currencyPair).map(CurrencyPairDTO::getQuoteCurrency).orElse(null);
    }

    /**
     * Getter timestamp.
     *
     * @return timestamp
     */
    public ZonedDateTime getTimestamp() {
        return Objects.requireNonNullElseGet(timestamp, ZonedDateTime::now);
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
        final CandleDTO that = (CandleDTO) o;
        return new EqualsBuilder()
                .append(this.currencyPair, that.currencyPair)
                .append(this.getTimestamp(), that.getTimestamp())
                .isEquals();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    @SuppressWarnings("checkstyle:DesignForExtension")
    public int hashCode() {
        return new HashCodeBuilder()
                .append(currencyPair)
                .append(timestamp)
                .toHashCode();
    }

}
