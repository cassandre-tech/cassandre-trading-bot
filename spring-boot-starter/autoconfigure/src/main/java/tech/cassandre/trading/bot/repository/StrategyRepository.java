package tech.cassandre.trading.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.cassandre.trading.bot.domain.Strategy;

import java.util.Optional;

/**
 * Strategy repository.
 */
@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Long> {

    /**
     * Find by strategy id.
     *
     * @param strategyId strategy id
     * @return strategy
     */
    Optional<Strategy> findByStrategyId(String strategyId);

}
