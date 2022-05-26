package tech.cassandre.trading.bot.util.base;

import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cassandre.trading.bot.util.mapper.AccountMapper;
import tech.cassandre.trading.bot.util.mapper.CandleMapper;
import tech.cassandre.trading.bot.util.mapper.CurrencyMapper;
import tech.cassandre.trading.bot.util.mapper.OrderMapper;
import tech.cassandre.trading.bot.util.mapper.PositionMapper;
import tech.cassandre.trading.bot.util.mapper.StrategyMapper;
import tech.cassandre.trading.bot.util.mapper.TickerMapper;
import tech.cassandre.trading.bot.util.mapper.TradeMapper;
import tech.cassandre.trading.bot.util.mapper.UtilMapper;

/**
 * Base.
 */
public abstract class Base {

    /** Logger. */
    protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

    /** Type mapper. */
    protected static final UtilMapper UTIL_MAPPER = Mappers.getMapper(UtilMapper.class);

    /** Currency mapper. */
    protected static final CurrencyMapper CURRENCY_MAPPER = Mappers.getMapper(CurrencyMapper.class);

    /** Strategy mapper. */
    protected static final StrategyMapper STRATEGY_MAPPER = Mappers.getMapper(StrategyMapper.class);

    /** Account mapper. */
    protected static final AccountMapper ACCOUNT_MAPPER = Mappers.getMapper(AccountMapper.class);

    /** Candle mapper. */
    protected static final CandleMapper CANDLE_MAPPER = Mappers.getMapper(CandleMapper.class);

    /** Ticker mapper. */
    protected static final TickerMapper TICKER_MAPPER = Mappers.getMapper(TickerMapper.class);

    /** Order mapper. */
    protected static final OrderMapper ORDER_MAPPER = Mappers.getMapper(OrderMapper.class);

    /** Trade mapper. */
    protected static final TradeMapper TRADE_MAPPER = Mappers.getMapper(TradeMapper.class);

    /** Position mapper. */
    protected static final PositionMapper POSITION_MAPPER = Mappers.getMapper(PositionMapper.class);

}
