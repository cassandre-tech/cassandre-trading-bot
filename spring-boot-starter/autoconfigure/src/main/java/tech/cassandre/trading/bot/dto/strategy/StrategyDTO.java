package tech.cassandre.trading.bot.dto.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import static lombok.AccessLevel.PRIVATE;

/**
 * DTO representing a strategy.
 */
@Value
@Builder
@AllArgsConstructor(access = PRIVATE)
@SuppressWarnings("checkstyle:VisibilityModifier")
public class StrategyDTO {

    /** Technical id. */
    Long id;

    /** An identifier that uniquely identifies the strategy. */
    String strategyId;

    /** Strategy type. */
    StrategyTypeDTO type;

    /** Strategy name. */
    String name;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StrategyDTO that = (StrategyDTO) o;
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
                .append(strategyId)
                .toHashCode();
    }

}
