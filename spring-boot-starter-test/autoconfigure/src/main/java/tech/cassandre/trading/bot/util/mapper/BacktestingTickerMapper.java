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
    @Mapping(target = "id", ignore = true)
    BacktestingCandle mapToBacktestingCandle(ImportedCandle source);

    // =================================================================================================================
    // BacktestingTicker to TickerDTO.
    @Mapping(target = "currencyPair", source = "id.currencyPair")
    @Mapping(target = "last", source = "close")
    @Mapping(target = "bid", ignore = true)
    @Mapping(target = "ask", ignore = true)
    @Mapping(target = "vwap", ignore = true)
    @Mapping(target = "quoteVolume", ignore = true)
    @Mapping(target = "bidSize", ignore = true)
    @Mapping(target = "askSize", ignore = true)
    TickerDTO mapToTickerDTO(BacktestingCandle source);

}
