package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.dto.trade.UserTrade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import tech.cassandre.trading.bot.domain.Trade;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Trade mapper.
 */
@Mapper(uses = {UtilMapper.class, CurrencyMapper.class})
public interface TradeMapper {

    // =================================================================================================================
    // XChange to DTO.

    /**
     * Map UserTrade to TradeDTO.
     *
     * @param source User trade
     * @return TradeDTO
     */
    @Mapping(source = "id", target = "tradeId")
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapUserTradeToTradeDTOAmount")
    @Mapping(source = "source", target = "price", qualifiedByName = "mapUserTradeToTradeDTOPrice")
    @Mapping(source = "source", target = "fee", qualifiedByName = "mapUserTradeToTradeDTOFee")
    @Mapping(source = "orderUserReference", target = "userReference")
    @Mapping(source = "instrument", target = "currencyPair")
    TradeDTO mapToTradeDTO(UserTrade source);

    @Named("mapUserTradeToTradeDTOAmount")
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    default CurrencyAmountDTO mapUserTradeToTradeDTOAmount(UserTrade source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getInstrument());
        return CurrencyAmountDTO.builder()
                .value(source.getOriginalAmount())
                .currency(cp.getBaseCurrency())
                .build();
    }

    @Named("mapUserTradeToTradeDTOPrice")
    default CurrencyAmountDTO mapUserTradeToTradeDTOPrice(UserTrade source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getInstrument());
        return CurrencyAmountDTO.builder()
                .value(source.getPrice())
                .currency(cp.getQuoteCurrency())
                .build();
    }

    @Named("mapUserTradeToTradeDTOFee")
    default CurrencyAmountDTO mapUserTradeToTradeDTOFee(UserTrade source) {
        if (source.getFeeAmount() != null && source.getFeeCurrency() != null) {
            return CurrencyAmountDTO.builder()
                    .value(source.getFeeAmount())
                    .currency(new CurrencyDTO(source.getFeeCurrency().toString()))
                    .build();
        } else {
            return null;
        }
    }

    // =================================================================================================================
    // DTO to domain.

    /**
     * Map TradeDTO to Trade.
     *
     * @param source TradeDTO
     * @return Trade
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(target = "order", ignore = true)
    Trade mapToTrade(TradeDTO source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "updatedOn", ignore = true)
    @Mapping(target = "order", ignore = true)
    void updateOrder(TradeDTO source, @MappingTarget Trade target);

    // =================================================================================================================
    // Domain to DTO.

    /**
     * Map Trade to tradeDTO.
     *
     * @param source trade
     * @return tradeDRO
     */
    TradeDTO mapToTradeDTO(Trade source);


    // =================================================================================================================
    // Util.

    /**
     * Map a trade set to a tradeDTO hashmap.
     *
     * @param source Trade set
     * @return TradeDTO hashmap
     */
    default Map<String, TradeDTO> map(Set<Trade> source) {
        Map<String, TradeDTO> results = new LinkedHashMap<>();
        source.forEach(trade -> results.put(trade.getTradeId(), mapToTradeDTO(trade)));
        return results;
    }

}
