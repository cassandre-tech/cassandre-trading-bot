package tech.cassandre.trading.bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Position;

import java.util.List;

/**
 * Position repository.
 */
@Repository
public interface PositionRepository extends CrudRepository<Position, Long> {

    /**
     * Find all position by id.
     * @return positions
     */
    List<Position> findByOrderById();

}
