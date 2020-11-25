package tech.cassandre.trading.bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;

import java.util.List;

/**
 * Position repository.
 */
@Repository
public interface PositionRepository extends CrudRepository<Position, Long> {

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

}
