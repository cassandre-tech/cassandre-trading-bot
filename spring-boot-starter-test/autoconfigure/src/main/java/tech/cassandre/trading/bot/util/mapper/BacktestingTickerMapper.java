package tech.cassandre.trading.bot.util.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.cassandre.trading.bot.domain.BacktestingCandle;
import tech.cassandre.trading.bot.domain.ImportedCandle;
import tech.cassandre.trading.bot.dto.market.TickerDTO;

/**
 * Backtesting candle mapper.
 */
@Mapper(uses = CurrencyMapper.class)
public interface BacktestingTickerMapper {

    // =================================================================================================================
    // ImportedCandle to BacktestingCandle.
    BacktestingCandle mapToBacktestingCandle(ImportedCandle source);

    // =================================================================================================================
    // BacktestingTicker to TickerDTO.
    @Mapping(target = "currencyPair", source = "id.currencyPair")
    @Mapping(target = "last", source = "close")
    TickerDTO mapToTickerDTO(BacktestingCandle source);

}
