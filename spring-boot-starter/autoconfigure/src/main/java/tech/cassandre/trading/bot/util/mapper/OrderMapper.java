package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.dto.trade.LimitOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

/**
 * Order mapper.
 */
@Mapper(uses = {TypeMapper.class, CurrencyMapper.class, TradeMapper.class})
public interface OrderMapper {

    // =================================================================================================================
    // XChange to DTO.

    /**
     * Map Order to OrderDTO.
     *
     * @param source LimitOrder
     * @return OrderDTO
     */
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
    @Mapping(source = "amount.value", target = "amount")
    @Mapping(source = "cumulativeAmount.value", target = "cumulativeAmount")
    @Mapping(source = "averagePrice.value", target = "averagePrice")
    @Mapping(source = "limitPrice.value", target = "limitPrice")
    tech.cassandre.trading.bot.domain.Order mapToOrder(OrderDTO source);

    // =================================================================================================================
    // Domain to DTO.

    /**
     * Map Order to OrderDTO.
     *
     * @param source order
     * @return OrderDTO
     */
    @Mapping(source = "source", target = "amount", qualifiedByName = "mapOrderToOrderDTOAmount")
    @Mapping(source = "source", target = "cumulativeAmount", qualifiedByName = "mapOrderToOrderDTOCumulativeAmount")
    @Mapping(source = "source", target = "averagePrice", qualifiedByName = "mapOrderToOrderDTOAveragePrice")
    @Mapping(source = "source", target = "limitPrice", qualifiedByName = "mapOrderToOrderDTOLimitPrice")
    @Mapping(source = "trades", target = "trades")
    OrderDTO mapToOrderDTO(tech.cassandre.trading.bot.domain.Order source);

    @Named("mapOrderToOrderDTOAmount")
    default CurrencyAmountDTO mapOrderToOrderDTOAmount(tech.cassandre.trading.bot.domain.Order source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        if (source.getAmount() != null & source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getAmount(), cp.getBaseCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapOrderToOrderDTOCumulativeAmount")
    default CurrencyAmountDTO mapOrderToOrderDTOCumulativeAmount(tech.cassandre.trading.bot.domain.Order source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        if (source.getCumulativeAmount() != null & source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getCumulativeAmount(), cp.getBaseCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapOrderToOrderDTOAveragePrice")
    default CurrencyAmountDTO mapOrderToOrderDTOAveragePrice(tech.cassandre.trading.bot.domain.Order source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        if (source.getAveragePrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getAveragePrice(), cp.getQuoteCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

    @Named("mapOrderToOrderDTOLimitPrice")
    default CurrencyAmountDTO mapOrderToOrderDTOLimitPrice(tech.cassandre.trading.bot.domain.Order source) {
        CurrencyPairDTO cp = new CurrencyPairDTO(source.getCurrencyPair());
        if (source.getLimitPrice() != null && source.getCurrencyPair() != null) {
            return new CurrencyAmountDTO(source.getLimitPrice(), cp.getQuoteCurrency());
        } else {
            return new CurrencyAmountDTO();
        }
    }

}
