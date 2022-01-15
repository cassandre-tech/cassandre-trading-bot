package tech.cassandre.trading.bot.api.graphql.data;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.api.graphql.util.base.BaseDataFetcher;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionStatusDTO;
import tech.cassandre.trading.bot.repository.PositionRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Position data fetcher.
 */
@DgsComponent
@RequiredArgsConstructor
public class PositionDataFetcher extends BaseDataFetcher {

    /** Position repository. */
    private final PositionRepository positionRepository;

    /**
     * Returns all the positions.
     *
     * @return all positions
     */
    @DgsQuery
    public final List<PositionDTO> positions() {
        return positionRepository.findAll()
                .stream()
                .map(POSITION_MAPPER::mapToPositionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns the position with the corresponding id value.
     *
     * @param id id
     * @return position
     */
    @DgsQuery
    public final PositionDTO position(@InputArgument final long id) {
        return positionRepository.findById(id)
                .map(POSITION_MAPPER::mapToPositionDTO)
                .orElse(null);
    }

    /**
     * Returns the positions of a strategy.
     *
     * @param id id of strategy the position
     * @return positions
     */
    @DgsQuery
    public final List<PositionDTO> positionsByStrategy(@InputArgument final long id) {
        return positionRepository.findAll()
                .stream()
                .filter(position -> position.getStrategy().getId() == id)
                .map(POSITION_MAPPER::mapToPositionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns the positions of a strategy.
     *
     * @param strategyId strategyId of strategy the position
     * @return positions
     */
    @DgsQuery
    public final List<PositionDTO> positionsByStrategyId(@InputArgument final String strategyId) {
        return positionRepository.findAll()
                .stream()
                .filter(position -> Objects.equals(position.getStrategy().getStrategyId(), strategyId))
                .map(POSITION_MAPPER::mapToPositionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns the positions of a strategy with a certain status.
     *
     * @param id     id of strategy the position
     * @param status position status
     * @return positions
     */
    @DgsQuery
    public final List<PositionDTO> positionsByStrategyAndStatus(@InputArgument final long id,
                                                                @InputArgument final PositionStatusDTO status) {
        return positionRepository.findByStatus(status)
                .stream()
                .filter(position -> position.getStrategy().getId() == id)
                .map(POSITION_MAPPER::mapToPositionDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns the positions of a strategy with a certain status.
     *
     * @param strategyId strategyId of strategy the position
     * @param status     position status
     * @return positions
     */
    @DgsQuery
    public final List<PositionDTO> positionsByStrategyIdAndStatus(@InputArgument final String strategyId,
                                                                  @InputArgument final PositionStatusDTO status) {
        return positionRepository.findByStatus(status)
                .stream()
                .filter(position -> Objects.equals(position.getStrategy().getStrategyId(), strategyId))
                .map(POSITION_MAPPER::mapToPositionDTO)
                .collect(Collectors.toList());
    }

}
