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
        return new CurrencyAmountDTO(source.getOriginalAmount(), cp.getBaseCurrency());
    }

    @Named("mapUserTradeToTradeDTOPrice")
    default CurrencyAmountDTO mapUserTradeToTradeDTOPrice(UserTrade source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getInstrument());
        return new CurrencyAmountDTO(source.getPrice(), cp.getQuoteCurrency());
    }

    @Named("mapUserTradeToTradeDTOFee")
    default CurrencyAmountDTO mapUserTradeToTradeDTOFee(UserTrade source) {
        if (source.getFeeAmount() != null && source.getFeeCurrency() != null) {
            return new CurrencyAmountDTO(source.getFeeAmount(), new CurrencyDTO(source.getFeeCurrency().toString()));
        } else {
            return new CurrencyAmountDTO();
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
