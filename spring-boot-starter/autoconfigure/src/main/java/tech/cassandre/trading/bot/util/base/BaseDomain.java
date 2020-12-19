package tech.cassandre.trading.bot.util.base;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

/**
 * Base domain.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseDomain {

    /** Data created on. */
    @CreatedDate
    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    private ZonedDateTime createdOn;

    /** Data updated on. */
    @LastModifiedDate
    @Column(name = "UPDATED_ON", insertable = false)
    private ZonedDateTime updatedOn;

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
