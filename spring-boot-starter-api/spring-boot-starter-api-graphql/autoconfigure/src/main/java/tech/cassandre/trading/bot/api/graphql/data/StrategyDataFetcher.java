package tech.cassandre.trading.bot.api.graphql.data;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.api.graphql.util.base.BaseDataFetcher;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.repository.StrategyRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Strategy data fetcher.
 */
@DgsComponent
@RequiredArgsConstructor
public class StrategyDataFetcher extends BaseDataFetcher {

    /** Strategy repository. */
    private final StrategyRepository strategyRepository;

    /**
     * Returns all the strategies.
     *
     * @return all strategies
     */
    @DgsQuery
    public final List<StrategyDTO> strategies() {
        return strategyRepository.findAll()
                .stream()
                .map(STRATEGY_MAPPER::mapToStrategyDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns the strategy with the corresponding id value.
     *
     * @param id id
     * @return strategy
     */
    @DgsQuery
    public StrategyDTO strategy(@InputArgument final long id) {
        return strategyRepository.findById(id)
                .map(STRATEGY_MAPPER::mapToStrategyDTO)
                .orElse(null);
    }

    /**
     * Returns the strategy with the corresponding strategyId value.
     *
     * @param strategyId strategy id
     * @return strategy
     */
    @DgsQuery
    public StrategyDTO strategyByStrategyId(@InputArgument final String strategyId) {
        return strategyRepository.findByStrategyId(strategyId)
                .map(STRATEGY_MAPPER::mapToStrategyDTO)
                .orElse(null);
    }

}
