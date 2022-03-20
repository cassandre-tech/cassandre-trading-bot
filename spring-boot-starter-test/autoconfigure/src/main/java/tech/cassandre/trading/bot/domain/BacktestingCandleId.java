package tech.cassandre.trading.bot.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.Hibernate;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

import static tech.cassandre.trading.bot.dto.util.CurrencyPairDTO.CURRENCY_PAIR_SEPARATOR;

/**
 * {@link BacktestingCandle} id.
 */
@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class BacktestingCandleId implements Serializable {

    /** Test session id. */
    @Column(name = "TEST_SESSION_ID")
    private String testSessionId;

    /** Response sequence id. */
    @Column(name = "RESPONSE_SEQUENCE_ID")
    private Long responseSequenceId;

    /** Currency pair. */
    @Column(name = "CURRENCY_PAIR")
    private String currencyPair;

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
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        BacktestingCandleId that = (BacktestingCandleId) o;

        if (!Objects.equals(testSessionId, that.testSessionId)) {
            return false;
        }
        if (!Objects.equals(responseSequenceId, that.responseSequenceId)) {
            return false;
        }
        return Objects.equals(currencyPair, that.currencyPair);
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(testSessionId)
                .append(responseSequenceId)
                .append(currencyPair)
                .toHashCode();
    }

}
