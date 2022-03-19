package tech.cassandre.trading.bot.dto.strategy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import tech.cassandre.trading.bot.util.java.EqualsBuilder;
import tech.cassandre.trading.bot.util.test.ExcludeFromCoverageGeneratedReport;

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
    Long uid;

    /** An identifier that uniquely identifies the strategy - Comes from the Java annotation. */
    String strategyId;

    /** Strategy name - Comes from the Java annotation. */
    String name;

    /** Last strategyId used in database - Used to generate the next strategyId when there is a creation. */
    @ToString.Exclude
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
    @ExcludeFromCoverageGeneratedReport
    @SuppressWarnings("checkstyle:DesignForExtension")
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StrategyDTO that = (StrategyDTO) o;
        return new EqualsBuilder()
                .append(this.uid, that.uid)
                .append(this.strategyId, that.strategyId)
                .isEquals();
    }

    @Override
    @ExcludeFromCoverageGeneratedReport
    @SuppressWarnings("checkstyle:DesignForExtension")
    public int hashCode() {
        return new HashCodeBuilder()
                .append(uid)
                .toHashCode();
    }

}
