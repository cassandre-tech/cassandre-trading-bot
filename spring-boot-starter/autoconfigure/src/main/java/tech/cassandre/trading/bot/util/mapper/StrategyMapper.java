package tech.cassandre.trading.bot.util.mapper;

import org.mapstruct.Mapper;
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;

/**
 * Strategy mapper.
 */
@Mapper
public interface StrategyMapper {

    // =================================================================================================================
    // DTO to Domain.

    /**
     * Map StrategyDTO to Strategy.
     *
     * @param source StrategyDTO
     * @return Strategy
     */
    Strategy mapToStrategy(StrategyDTO source);

    // =================================================================================================================
    // Domain to DTO.

    /**
     * Map Strategy to StrategyDTO.
     *
     * @param source Strategy
     * @return StrategyDTO
     */
    StrategyDTO mapToStrategyDTO(Strategy source);

}
