package tech.cassandre.trading.bot.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Strategy;

import java.util.Optional;

/**
 * Strategy repository.
 */
@Repository
public interface StrategyRepository extends CrudRepository<Strategy, Long> {

    /**
     * Find by strategy id.
     *
     * @param strategyId strategy id
     * @return strategy
     */
    Optional<Strategy> findByStrategyId(String strategyId);

}
