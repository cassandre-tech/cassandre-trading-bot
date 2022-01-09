package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * {@link Position} repository.
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, Long>, JpaSpecificationExecutor<Position> {

    /**
     * Find a position by its position id.
     *
     * @param positionId position id
     * @return positions
     */
    Optional<Position> findByPositionId(long positionId);

    /**
     * Retrieve all positions (sorted by id).
     *
     * @return positions
     */
    List<Position> findByOrderById();

    /**
     * Find positions with a specific status.
     *
     * @param status status
     * @return positions
     */
    List<Position> findByStatus(PositionStatusDTO status);

    /**
     * Find positions without a specific status.
     *
     * @param status status
     * @return positions
     */
    List<Position> findByStatusNot(PositionStatusDTO status);

    /**
     * Find positions with any of specific status.
     *
     * @param status list of status
     * @return positions
     */
    List<Position> findByStatusIn(Set<PositionStatusDTO> status);

    /**
     * Returns the last position id used by a strategy.
     *
     * @param strategyId strategy id
     * @return positions
     */
    @Query("SELECT coalesce(max(p.positionId), 0) FROM Position p where p.strategy.id = :strategyId")
    Long getLastPositionIdUsedByStrategy(@Param("strategyId") Long strategyId);

    /**
     * Update stop gain rule.
     *
     * @param id    position id
     * @param value new value
     */
    @Transactional
    @Modifying
    @Query("update Position p set p.stopGainPercentageRule = :value where p.id = :id")
    void updateStopGainRule(@Param("id") Long id, @Param("value") Float value);

    /**
     * Update stop loss rule.
     *
     * @param id    position id
     * @param value new value
     */
    @Transactional
    @Modifying
    @Query("update Position p set p.stopLossPercentageRule = :value where p.id = :id")
    void updateStopLossRule(@Param("id") Long id, @Param("value") Float value);

    /**
     * Update autoclose.
     *
     * @param id    position id
     * @param value true to allow autoclose.
     */
    @Transactional
    @Modifying
    @Query("update Position p set p.autoClose = :value where p.id = :id")
    void updateAutoClose(@Param("id") Long id, @Param("value") boolean value);

    /**
     * Update force closing.
     *
     * @param id    position id
     * @param value true to force closing
     */
    @Transactional
    @Modifying
    @Query("update Position p set p.forceClosing = :value where p.id = :id")
    void updateForceClosing(@Param("id") Long id, @Param("value") boolean value);

}
