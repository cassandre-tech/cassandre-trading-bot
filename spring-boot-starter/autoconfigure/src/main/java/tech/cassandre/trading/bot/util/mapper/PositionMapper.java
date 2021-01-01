package tech.cassandre.trading.bot.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

/**
 * Position mapper.
 */
@Mapper(uses = {CurrencyMapper.class, OrderMapper.class, UtilMapper.class})
public interface PositionMapper {

    // =================================================================================================================
    // DTO to Domain.

    /**
     * Map PositionDTO to Position.
     * XChange to DTO.
     *
     * @param source PositionDTO
     * @return position
     */
    @Mapping(source = "rules.stopGainPercentage", target = "stopGainPercentageRule")
    @Mapping(source = "rules.stopLossPercentage", target = "stopLossPercentageRule")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(target = "strategy", ignore = true)
    Position mapToPosition(PositionDTO source);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "rules.stopGainPercentage", target = "stopGainPercentageRule")
    @Mapping(source = "rules.stopLossPercentage", target = "stopLossPercentageRule")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(target = "strategy", ignore = true)
    void updatePosition(PositionDTO source, @MappingTarget Position target);

    // =================================================================================================================
    // Domain to DTO.

    /**
     * Map Position to positionDTO.
     *
     * @param source position
     * @return positionDTO
     */
    @Mapping(source = "source", target = "rules")
    PositionDTO mapToPositionDTO(Position source);

    default PositionRulesDTO mapToPositionRulesDTO(Position source) {
        PositionRulesDTO rules = PositionRulesDTO.builder().build();
        boolean stopGainRuleSet = source.getStopGainPercentageRule() != null;
        boolean stopLossRuleSet = source.getStopLossPercentageRule() != null;
        // Two rules set.
        if (stopGainRuleSet && stopLossRuleSet) {
            rules = PositionRulesDTO.builder()
                    .stopGainPercentage(source.getStopGainPercentageRule())
                    .stopLossPercentage(source.getStopLossPercentageRule())
                    .build();
        }
        // Stop gain set.
        if (stopGainRuleSet && !stopLossRuleSet) {
            rules = PositionRulesDTO.builder()
                    .stopGainPercentage(source.getStopGainPercentageRule())
                    .build();
        }
        // Stop loss set.
        if (!stopGainRuleSet && stopLossRuleSet) {
            rules = PositionRulesDTO.builder()
                    .stopLossPercentage(source.getStopLossPercentageRule())
                    .build();
        }
        return rules;
    }

}
