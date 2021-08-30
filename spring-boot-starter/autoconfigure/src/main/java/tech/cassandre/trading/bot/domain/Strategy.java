package tech.cassandre.trading.bot.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.Hibernate;
import tech.cassandre.trading.bot.dto.strategy.StrategyTypeDTO;
import tech.cassandre.trading.bot.util.base.domain.BaseDomain;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

/**
 * Strategy.
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "STRATEGIES")
public class Strategy extends BaseDomain {

    /** Technical ID. */
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    /** An identifier that uniquely identifies the strategy - Comes from the Java annotation. */
    @Column(name = "STRATEGY_ID")
    private String strategyId;

    /** Strategy type - Basic or Ta4j. */
    @Enumerated(STRING)
    @Column(name = "TYPE")
    private StrategyTypeDTO type;

    /** Strategy name - Comes from the Java annotation. */
    @Column(name = "NAME")
    private String name;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        final Strategy that = (Strategy) o;
        return new EqualsBuilder()
                .append(this.id, that.id)
                .append(this.strategyId, that.strategyId)
                .append(this.type, that.type)
                .isEquals();
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder()
                .append(id)
                .toHashCode();
    }

}
