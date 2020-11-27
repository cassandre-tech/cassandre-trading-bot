package tech.cassandre.trading.bot.util.base;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.ZonedDateTime;

/**
 * Base domain.
 */
@MappedSuperclass
public abstract class BaseDomain {

    /** Data created on. */
    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    private ZonedDateTime createdOn;

    /** Data updated on. */
    @Column(name = "UPDATED_ON", nullable = false)
    private ZonedDateTime updatedOn;

    @PrePersist
    protected final void onCreate() {
        createdOn = ZonedDateTime.now();
    }

    @PreUpdate
    protected final void onUpdate() {
        updatedOn = ZonedDateTime.now();
    }

    /**
     * Getter createdOn.
     *
     * @return createdAt
     */
    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    /**
     * Setter createdOn.
     *
     * @param newCreatedOn the createdOn to set
     */
    public void setCreatedOn(final ZonedDateTime newCreatedOn) {
        createdOn = newCreatedOn;
    }

    /**
     * Getter updatedOn.
     *
     * @return updatedOn
     */
    public ZonedDateTime getUpdatedOn() {
        return updatedOn;
    }

    /**
     * Setter updatedOn.
     *
     * @param newUpdatedOn the updatedOn to set
     */
    public void setUpdatedOn(final ZonedDateTime newUpdatedOn) {
        updatedOn = newUpdatedOn;
    }

}
