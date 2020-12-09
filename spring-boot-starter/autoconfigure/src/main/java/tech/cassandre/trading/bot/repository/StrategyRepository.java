package tech.cassandre.trading.bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Strategy;

/**
 * Strategy repository.
 */
@Repository
public interface StrategyRepository extends CrudRepository<Strategy, String> {

}
