package tech.cassandre.trading.bot.domain;

import tech.cassandre.trading.bot.util.base.BaseDomain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Strategy.
 */
@Entity
@Table(name = "STRATEGIES")
public class Strategy extends BaseDomain {

    /** An identifier that uniquely identifies the strategy. */
    @Id
    @Column(name = "ID")
    private String id;

    /** Strategy name. */
    @Column(name = "NAME")
    private String name;

    /**
     * Getter id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Setter id.
     *
     * @param newId the id to set
     */
    public void setId(final String newId) {
        id = newId;
    }

    /**
     * Getter name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter name.
     *
     * @param newFeeCurrency the name to set
     */
    public void setName(final String newFeeCurrency) {
        name = newFeeCurrency;
    }

    @Override
    public final String toString() {
        return "Strategy{"
                + " id='" + id + '\''
                + ", name='" + name + '\''
                + "}";
    }

    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Strategy strategy = (Strategy) o;
        return id.equals(strategy.id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(id);
    }

}
