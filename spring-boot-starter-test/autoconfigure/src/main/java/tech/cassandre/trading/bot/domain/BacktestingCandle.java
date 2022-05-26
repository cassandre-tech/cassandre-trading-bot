package tech.cassandre.trading.bot.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.Hibernate;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;

/**
 * Candles used for backtesting.
 * <p>
 * This is how data are stored:
 * | TEST_SESSION_ID    | RESPONSE_ORDER    | CURRENCY_PAIR | LAST  |
 * -- A test session is running.
 * | 09c5b4ba-1e24      | 1                 | BTC/USDT      | 11    |
 * | 09c5b4ba-1e24      | 1                 | BTC/ETH       | 20    |
 * | 09c5b4ba-1e24      | 2                 | BTC/USDT      | 10    |
 * | 09c5b4ba-1e24      | 2                 | BTC/ETH       | 22    |
 * -- Another test session is running.
 * | b3e00c9e-2a5d      | 1                 | ETH/USDT      | 4     |
 * | b3e00c9e-2a5d      | 1                 | ETH/AVA       | 5     |
 * | b3e00c9e-2a5d      | 1                 | ETH/EUR       | 6     |
 * | b3e00c9e-2a5d      | 2                 | ETH/USDT      | 5     |
 * | b3e00c9e-2a5d      | 2                 | ETH/AVA       | 6     |
 * | b3e00c9e-2a5d      | 2                 | ETH/EUR       | 7     |
 * <p>
 * TODO Write a test to validate parallel runs.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "BACKTESTING_CANDLES")
public class BacktestingCandle {

    /** Backtesting candle id. */
    @EmbeddedId
    private BacktestingCandleId id;

    /** Opening price (first trade) in the bucket interval. */
    @Column(name = "OPEN", precision = PRECISION, scale = SCALE)
    private BigDecimal open;

    /** Highest price during the bucket interval. */
    @Column(name = "HIGH", precision = PRECISION, scale = SCALE)
    private BigDecimal high;

    /** Lowest price during the bucket interval. */
    @Column(name = "LOW", precision = PRECISION, scale = SCALE)
    private BigDecimal low;

    /** Closing price (last trade) in the bucket interval. */
    @Column(name = "CLOSE", precision = PRECISION, scale = SCALE)
    private BigDecimal close;

    /** Volume of trading activity during the bucket interval. */
    @Column(name = "VOLUME", precision = PRECISION, scale = SCALE)
    private BigDecimal volume;

    /** Bucket start time. */
    @Column(name = "TIMESTAMP")
    private ZonedDateTime timestamp;

    /**
     * Returns currency pair DTO.
     *
     * @return currency pair DTO
     */
    public CurrencyPairDTO getCurrencyPairDTO() {
        if (id != null && id.getCurrencyPair() != null) {
            return new CurrencyPairDTO(id.getCurrencyPair());
        } else {
            return null;
        }
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        BacktestingCandle that = (BacktestingCandle) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

}
