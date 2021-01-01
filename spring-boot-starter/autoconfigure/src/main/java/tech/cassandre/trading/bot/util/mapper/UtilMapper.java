package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.instrument.Instrument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;
import org.mapstruct.ValueMappings;
import tech.cassandre.trading.bot.dto.trade.OrderTypeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.util.jpa.CurrencyAmount;

/**
 * Type mapper.
 */
@Mapper(uses = {CurrencyMapper.class})
public interface UtilMapper {

    // =================================================================================================================
    // DTO to domain.

    /**
     * Map OrderTypeDTO to OrderType.
     *
     * @param source order type
     * @return OrderType
     */
    @ValueMappings({
            @ValueMapping(source = "BID", target = "BID"),
            @ValueMapping(source = "ASK", target = "ASK")
    })
    Order.OrderType mapToOrderType(OrderTypeDTO source);

    // =================================================================================================================
    // Domain to DTO.

    /**
     * Map OrderType to OrderTypeDTO.
     *
     * @param source XChange order type
     * @return OrderTypeDTO
     */
    @ValueMappings({
            @ValueMapping(source = "BID", target = "BID"),
            @ValueMapping(source = "ASK", target = "ASK"),
            @ValueMapping(source = "EXIT_BID", target = "BID"),
            @ValueMapping(source = "EXIT_ASK", target = "ASK")
    })
    OrderTypeDTO mapToOrderTypeDTO(Order.OrderType source);

}
