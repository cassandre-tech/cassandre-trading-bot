package tech.cassandre.trading.bot.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tech.cassandre.trading.bot.domain.Position;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

/**
 * Position mapper.
 */
@Mapper(uses = {CurrencyMapper.class, OrderMapper.class})
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
    @Mapping(source = "amount.value", target = "amount")
    @Mapping(source = "lowestPrice.value", target = "lowestPrice")
    @Mapping(source = "highestPrice.value", target = "highestPrice")
    @Mapping(source = "latestPrice.value", target = "latestPrice")
    Position mapToPosition(PositionDTO source);

    // =================================================================================================================
    // Domain to DTO.

    /**
     * Map Position to positionDTO.
     *
     * @param source position
     * @return positionDTO
     */
    @Mapping(source = "source", target = "rules")
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapPositionToPositionDTOAmount")
    @Mapping(source = "source", target = "lowestPrice", qualifiedByName = "mapPositionToPositionDTOLowestPrice")
    @Mapping(source = "source", target = "highestPrice", qualifiedByName = "mapPositionToPositionDTOHighestPrice")
    @Mapping(source = "source", target = "latestPrice", qualifiedByName = "mapPositionToPositionDTOLatestPrice")
    PositionDTO mapToPositionDTO(Position source);

    @Named("mapPositionToPositionDTOAmount")
    default CurrencyAmountDTO mapPositionToPositionDTOAmount(Position source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        return new CurrencyAmountDTO(source.getAmount(), cp.getBaseCurrency());
    }

    @Named("mapPositionToPositionDTOLowestPrice")
    default CurrencyAmountDTO mapPositionToPositionDTOLowestPrice(Position source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        if (source.getLowestPrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getLowestPrice(), cp.getQuoteCurrency());
        } else {
            return null;
        }
    }

    @Named("mapPositionToPositionDTOHighestPrice")
    default CurrencyAmountDTO mapPositionToPositionDTOHighestPrice(Position source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        if (source.getHighestPrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getHighestPrice(), cp.getQuoteCurrency());
        } else {
            return null;
        }
    }

    @Named("mapPositionToPositionDTOLatestPrice")
    default CurrencyAmountDTO mapPositionToPositionDTOLatestPrice(Position source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        if (source.getLatestPrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getLatestPrice(), cp.getQuoteCurrency());
        } else {
            return null;
        }
    }

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
