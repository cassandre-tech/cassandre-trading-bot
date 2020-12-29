package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.dto.trade.LimitOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

/**
 * Order mapper.
 */
@Mapper(uses = {UtilMapper.class, CurrencyMapper.class, TradeMapper.class})
public interface OrderMapper {

    // =================================================================================================================
    // XChange to DTO.

    /**
     * Map Order to OrderDTO.
     *
     * @param source LimitOrder
     * @return OrderDTO
     */
    @Mapping(source = "id", target = "orderId")
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapLimitOrderToOrderDTOAmount")
    @Mapping(source = "source", target = "cumulativeAmount", qualifiedByName = "mapLimitOrderToOrderDTOCumulativeAmount")
    @Mapping(source = "source", target = "averagePrice", qualifiedByName = "mapLimitOrderToOrderDTOAveragePrice")
    @Mapping(source = "source", target = "limitPrice", qualifiedByName = "mapLimitOrderToOrderDTOLimitPrice")
    OrderDTO mapToOrderDTO(LimitOrder source);

    @Named("mapLimitOrderToOrderDTOAmount")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOAmount(LimitOrder source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        if (source.getOriginalAmount() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getOriginalAmount(), cp.getBaseCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapLimitOrderToOrderDTOCumulativeAmount")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOCumulativeAmount(LimitOrder source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        if (source.getCumulativeAmount() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getCumulativeAmount(), cp.getBaseCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapLimitOrderToOrderDTOAveragePrice")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOAveragePrice(LimitOrder source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        if (source.getAveragePrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getAveragePrice(), cp.getQuoteCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapLimitOrderToOrderDTOLimitPrice")
    default CurrencyAmountDTO mapLimitOrderToOrderDTOLimitPrice(LimitOrder source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        if (source.getLimitPrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getLimitPrice(), cp.getQuoteCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    // =================================================================================================================
    // DTO to domain.

    /**
     * Map OrderDTO to Order.
     *
     * @param source source
     * @return Order
     */
    @Mapping(target = "id", ignore = true)
    tech.cassandre.trading.bot.domain.Order mapToOrder(OrderDTO source);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "strategy", ignore = true)
    void updateOrder(OrderDTO source, @MappingTarget tech.cassandre.trading.bot.domain.Order target);

    // =================================================================================================================
    // Domain to DTO.

    /**
     * Map Order to OrderDTO.
     *
     * @param source order
     * @return OrderDTO
     */
    @Mapping(source = "trades", target = "trades")
    OrderDTO mapToOrderDTO(tech.cassandre.trading.bot.domain.Order source);

}
