package tech.cassandre.trading.bot.dto.strategy;

import java.util.Objects;

/**
 * DTO representing a strategy.
 */
public class StrategyDTO {

    /** An identifier that uniquely identifies the strategy. */
    private String id;

    /** Strategy name. */
    private String name;

    /**
     * Getter id.
     *
     * @return id
     */
    public final String getId() {
        return id;
    }

    /**
     * Setter id.
     *
     * @param newId the id to set
     */
    public final void setId(final String newId) {
        id = newId;
    }

    /**
     * Getter name.
     *
     * @return name
     */
    public final String getName() {
        return name;
    }

    /**
     * Setter name.
     *
     * @param newName the name to set
     */
    public final void setName(final String newName) {
        name = newName;
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StrategyDTO that = (StrategyDTO) o;
        return id.equals(that.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

}
