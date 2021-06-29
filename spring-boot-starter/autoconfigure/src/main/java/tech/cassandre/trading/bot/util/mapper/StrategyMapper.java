package tech.cassandre.trading.bot.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.cassandre.trading.bot.domain.Strategy;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;

/**
 * Strategy mapper.
 */
@Mapper
public interface StrategyMapper {

    // =================================================================================================================
    // DTO to Domain.

    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    Strategy mapToStrategy(StrategyDTO source);

    // =================================================================================================================
    // Domain to DTO.

    StrategyDTO mapToStrategyDTO(Strategy source);

}
