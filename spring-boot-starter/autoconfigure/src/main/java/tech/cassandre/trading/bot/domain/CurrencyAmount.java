package tech.cassandre.trading.bot.domain;

import lombok.Value;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.PRECISION;
import static tech.cassandre.trading.bot.configuration.DatabaseAutoConfiguration.SCALE;

/**
 * Currency amount (amount value + currency).
 */
@Value
@Embeddable
@SuppressWarnings("checkstyle:VisibilityModifier")
public class CurrencyAmount {

    /** Amount value. */
    @Column(precision = PRECISION, scale = SCALE)
    BigDecimal value;

    /** Amount currency. */
    String currency;

}
