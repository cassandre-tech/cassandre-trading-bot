package tech.cassandre.trading.bot.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.cassandre.trading.bot.domain.BacktestingTicker;
import tech.cassandre.trading.bot.dto.market.TickerDTO;

/**
 * Backtesting ticker mapper.
 */
@Mapper(uses = CurrencyMapper.class)
public interface BacktestingTickerMapper {

    // =================================================================================================================
    // TickerDTO to BacktestingTicker.
    @Mapping(target = "id", ignore = true)
    BacktestingTicker mapToBacktestingTicker(TickerDTO source);

    // =================================================================================================================
    // TickerDTO to BacktestingTicker.
    @Mapping(target = "currencyPair", source = "id.currencyPair")
    TickerDTO mapToTickerDTO(BacktestingTicker source);

}
