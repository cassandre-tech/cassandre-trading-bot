package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.dto.marketdata.Ticker;
import org.mapstruct.Mapper;
import tech.cassandre.trading.bot.dto.market.TickerDTO;

/**
 * Ticker mapper.
 */
@Mapper(uses = CurrencyMapper.class)
public interface TickerMapper {

    // =================================================================================================================
    // XChange to DTO.

    /**
     * Map Ticker to TickerDTO.
     *
     * @param source Ticker
     * @return TickerDTO
     */
    TickerDTO mapToTickerDTO(Ticker source);

}
