package tech.cassandre.trading.bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Position;

/**
 * Position repository.
 */
@Repository
public interface PositionRepository extends CrudRepository<Position, Long> {

}
