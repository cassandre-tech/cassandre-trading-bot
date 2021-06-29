package tech.cassandre.trading.bot.util.base;

import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cassandre.trading.bot.util.mapper.AccountMapper;
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
    protected final UtilMapper utilMapper = Mappers.getMapper(UtilMapper.class);

    /** Currency mapper. */
    protected final CurrencyMapper currencyMapper = Mappers.getMapper(CurrencyMapper.class);

    /** Strategy mapper. */
    protected final StrategyMapper strategyMapper = Mappers.getMapper(StrategyMapper.class);

    /** Account mapper. */
    protected final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    /** Ticker mapper. */
    protected final TickerMapper tickerMapper = Mappers.getMapper(TickerMapper.class);

    /** Order mapper. */
    protected final OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    /** Trade mapper. */
    protected final TradeMapper tradeMapper = Mappers.getMapper(TradeMapper.class);

    /** Position mapper. */
    protected final PositionMapper positionMapper = Mappers.getMapper(PositionMapper.class);

}
