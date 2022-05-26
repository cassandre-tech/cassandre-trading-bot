package tech.cassandre.trading.bot.domain;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.Hibernate;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.csv.EpochToZonedDateTime;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;
import static tech.cassandre.trading.bot.dto.util.CurrencyPairDTO.CURRENCY_PAIR_SEPARATOR;

/**
 * Imported candles (map "IMPORTED_CANDLES" table).
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "IMPORTED_CANDLES")
public class ImportedCandle {

    /** Technical ID. */
    @Id
    @Column(name = "UID")
    private Long uid;

    /** The currency-pair. */
    @CsvBindByName(column = "CURRENCY_PAIR")
    @Column(name = "CURRENCY_PAIR")
    private String currencyPair;

    /** Opening price (first trade) in the bucket interval. */
    @CsvBindByName(column = "OPEN")
    @Column(name = "OPEN", precision = PRECISION, scale = SCALE)
    private BigDecimal open;

    /** Highest price during the bucket interval. */
    @CsvBindByName(column = "HIGH")
    @Column(name = "HIGH", precision = PRECISION, scale = SCALE)
    private BigDecimal high;

    /** Lowest price during the bucket interval. */
    @CsvBindByName(column = "LOW")
    @Column(name = "LOW", precision = PRECISION, scale = SCALE)
    private BigDecimal low;

    /** Closing price (last trade) in the bucket interval. */
    @CsvBindByName(column = "CLOSE")
    @Column(name = "CLOSE", precision = PRECISION, scale = SCALE)
    private BigDecimal close;

    /** Volume of trading activity during the bucket interval. */
    @CsvBindByName(column = "VOLUME")
    @Column(name = "VOLUME", precision = PRECISION, scale = SCALE)
    private BigDecimal volume;

    /** Bucket start time. */
    @CsvCustomBindByName(column = "TIMESTAMP", converter = EpochToZonedDateTime.class)
    @Column(name = "TIMESTAMP")
    private ZonedDateTime timestamp;

    /**
     * Returns currency pair DTO.
     *
     * @return currency pair DTO
     */
    public CurrencyPairDTO getCurrencyPairDTO() {
        if (currencyPair != null) {
            return new CurrencyPairDTO(currencyPair.replaceAll("-", CURRENCY_PAIR_SEPARATOR));
        } else {
            return null;
        }
    }

    /**
     * Setter currencyPair.
     *
     * @param newCurrencyPair the currencyPair to set
     */
    public void setCurrencyPair(final String newCurrencyPair) {
        if (newCurrencyPair != null) {
            currencyPair = newCurrencyPair.replaceAll("-", CURRENCY_PAIR_SEPARATOR);
        }
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        ImportedCandle that = (ImportedCandle) o;
        return Objects.equals(uid, that.uid);
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(uid)
                .toHashCode();
    }

}
