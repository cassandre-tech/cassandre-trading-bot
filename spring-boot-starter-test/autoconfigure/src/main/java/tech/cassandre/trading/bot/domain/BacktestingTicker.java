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
 * Tickers used for backtesting.
 *
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
 *
 * note that for the moment, I did not write a test to validate parallel runs.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "BACKTESTING_TICKERS")
public class BacktestingTicker {

    /** Backtesting ticker id. */
    @EmbeddedId
    private BacktestingTickerId id;

    /** The opening price is the first trade price that was recorded during the day’s trading. */
    @Column(name = "OPEN", precision = PRECISION, scale = SCALE)
    private BigDecimal open;

    /** Last trade field is the price set during the last trade. */
    @Column(name = "LAST", precision = PRECISION, scale = SCALE)
    private BigDecimal last;

    /** The bid price shown represents the highest bid price. */
    @Column(name = "BID", precision = PRECISION, scale = SCALE)
    private BigDecimal bid;

    /** The ask price shown represents the lowest bid price. */
    @Column(name = "ASK", precision = PRECISION, scale = SCALE)
    private BigDecimal ask;

    /** The day’s high price. */
    @Column(name = "HIGH", precision = PRECISION, scale = SCALE)
    private BigDecimal high;

    /** The day’s low price. */
    @Column(name = "LOW", precision = PRECISION, scale = SCALE)
    private BigDecimal low;

    /** Volume-weighted average price (VWAP) is the ratio of the value traded to total volume traded over a particular time horizon (usually one day). */
    @Column(name = "VWAP", precision = PRECISION, scale = SCALE)
    private BigDecimal vwap;

    /** Volume is the number of shares or contracts traded. */
    @Column(name = "VOLUME", precision = PRECISION, scale = SCALE)
    private BigDecimal volume;

    /** Quote volume. */
    @Column(name = "QUOTE_VOLUME", precision = PRECISION, scale = SCALE)
    private BigDecimal quoteVolume;

    /** The bid size represents the quantity of a security that investors are willing to purchase at a specified bid price. */
    @Column(name = "BID_SIZE", precision = PRECISION, scale = SCALE)
    private BigDecimal bidSize;

    /** The ask size represents the quantity of a security that investors are willing to sell at a specified selling price. */
    @Column(name = "ASK_SIZE", precision = PRECISION, scale = SCALE)
    private BigDecimal askSize;

    /** Information timestamp. */
    @Column(name = "TIMESTAMP")
    private ZonedDateTime timestamp;

    /**
     * Returns currency pair DTO.
     * @return currency pair DTO
     */
    public CurrencyPairDTO getCurrencyPairDTO() {
        if (id != null) {
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
        BacktestingTicker that = (BacktestingTicker) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

}
