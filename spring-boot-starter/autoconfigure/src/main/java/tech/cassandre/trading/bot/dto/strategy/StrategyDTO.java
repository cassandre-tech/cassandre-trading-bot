package tech.cassandre.trading.bot.dto.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;

import java.util.concurrent.atomic.AtomicLong;

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

    /** Last position id used. */
    @ToString.Include
    AtomicLong lastPositionIdUsed = new AtomicLong();

    /**
     * This method is used during initialization to set the last position used for this time of strategy.
     *
     * @param value initial value
     */
    public void initializeLastPositionIdUsed(final Long value) {
        lastPositionIdUsed.set(value);
    }

    /**
     * This method returns the next position id to use.
     *
     * @return next position
     */
    public long getNextPositionId() {
        return lastPositionIdUsed.incrementAndGet();
    }

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
