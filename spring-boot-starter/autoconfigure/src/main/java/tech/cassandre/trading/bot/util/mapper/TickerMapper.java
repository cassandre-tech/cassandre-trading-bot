package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.dto.marketdata.Ticker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.cassandre.trading.bot.domain.ImportedTicker;
import tech.cassandre.trading.bot.dto.market.TickerDTO;

/**
 * Ticker mapper.
 */
@Mapper(uses = {CurrencyMapper.class})
public interface TickerMapper {

    // =================================================================================================================
    // XChange to DTO.

    @Mapping(source = "instrument", target = "currencyPair")
    TickerDTO mapToTickerDTO(Ticker source);

    // =================================================================================================================
    // Domain to DTO.

    TickerDTO mapToTickerDTO(ImportedTicker source);

}
