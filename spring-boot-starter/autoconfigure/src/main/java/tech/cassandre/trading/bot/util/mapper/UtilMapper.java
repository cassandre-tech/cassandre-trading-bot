package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.dto.Order;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;

/**
 * Util mapper.
 */
@Mapper(uses = {CurrencyMapper.class})
public interface UtilMapper {

    // =================================================================================================================
    // XChange to DTO.

    @ValueMappings({
            @ValueMapping(source = "BID", target = "BID"),
            @ValueMapping(source = "ASK", target = "ASK"),
            @ValueMapping(source = "EXIT_BID", target = "BID"),
            @ValueMapping(source = "EXIT_ASK", target = "ASK")
    })
    OrderTypeDTO mapToOrderTypeDTO(Order.OrderType source);

    // =================================================================================================================
    // DTO to XChange.

    @ValueMappings({
            @ValueMapping(source = "BID", target = "BID"),
            @ValueMapping(source = "ASK", target = "ASK")
    })
    Order.OrderType mapToOrderType(OrderTypeDTO source);

}
