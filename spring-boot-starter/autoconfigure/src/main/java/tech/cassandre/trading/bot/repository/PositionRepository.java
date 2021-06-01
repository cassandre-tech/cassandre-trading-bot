package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Position repository.
 */
@Repository
public interface PositionRepository extends CrudRepository<Position, Long> {

    /**
     * Find a position by its id.
     *
     * @param positionId position id
     * @return positions
     */
    Optional<Position> findByPositionId(long positionId);

    /**
     * Find all position (sorted by id).
     *
     * @return positions
     */
    List<Position> findByOrderById();

    /**
     * Find all positions by status.
     *
     * @param status status
     * @return list of positions
     */
    List<Position> findByStatus(PositionStatusDTO status);

    /**
     * Find all positions not having a specific status.
     *
     * @param status status
     * @return list of positions
     */
    List<Position> findByStatusNot(PositionStatusDTO status);

    /**
     * Find all positions with a list of status.
     *
     * @param status list of status
     * @return list of positions
     */
    List<Position> findByStatusIn(Set<PositionStatusDTO> status);

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
     * Update stop gain rule.
     *
     * @param id    position id
     * @param value new value
     */
    @Transactional
    @Modifying
    @Query("update Position p set p.stopLossPercentageRule = :value where p.id = :id")
    void updateStopLossRule(@Param("id") Long id, @Param("value") Float value);

    /**
     * Update force closing.
     *
     * @param id    position id
     * @param value new value
     */
    @Transactional
    @Modifying
    @Query("update Position p set p.forceClosing = :value where p.id = :id")
    void updateForceClosing(@Param("id") Long id, @Param("value") boolean value);

    /**
     * Returns the last position id for a strategy.
     *
     * @param strategyId strategy id
     * @return last position
     */
    @Query("SELECT coalesce(max(p.positionId), 0) FROM Position p where p.strategy.id = :strategyId")
    Long getLastPositionIdUsedByStrategy(@Param("strategyId") Long strategyId);

}
