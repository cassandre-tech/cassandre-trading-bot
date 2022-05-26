package tech.cassandre.trading.bot.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.cassandre.trading.bot.domain.ImportedCandle;
import tech.cassandre.trading.bot.dto.market.CandleDTO;

/**
 * Candle mapper.
 */
@Mapper(uses = {CurrencyMapper.class})
public interface CandleMapper {

    // =================================================================================================================
    // Domain to DTO.

    @Mapping(source = "currencyPairDTO", target = "currencyPair")
    CandleDTO mapToCandleDTO(ImportedCandle source);

}
