package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

/**
 * Service allowing to create and retrieve positions.
 */
public interface PositionService {

    /**
     * Creates a long position with its associated rules.
     * Long position is nothing but buying share.
     * If you are bullish (means you think that price of X share will rise) at that time you buy some amount of Share is called taking Long Position in share.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @param rules        rules
     * @return position creation result
     */
    PositionCreationResultDTO createLongPosition(StrategyDTO strategy,
                                                 CurrencyPairDTO currencyPair,
                                                 BigDecimal amount,
                                                 PositionRulesDTO rules);

    /**
     * Creates a long position with its associated rules.
     * Short position is nothing but selling share.
     * If you are bearish (means you think that price of xyz share are going to fall) at that time you sell some amount of share is called taking Short Position in share.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @param rules        rules
     * @return position creation result
     */
    PositionCreationResultDTO createShortPosition(StrategyDTO strategy,
                                                  CurrencyPairDTO currencyPair,
                                                  BigDecimal amount,
                                                  PositionRulesDTO rules);

    /**
     * Close position (no matter the rules).
     * The closing will happened when the next ticker arrives.
     *
     * @param positionId position id
     */
    void closePosition(long positionId);

    /**
     * Get positions.
     *
     * @return position list
     */
    Set<PositionDTO> getPositions();

    /**
     * Get position by id.
     *
     * @param id id
     * @return position
     */
    Optional<PositionDTO> getPositionById(long id);

    /**
     * Method called by streams at every order update.
     *
     * @param order order
     */
    void orderUpdate(OrderDTO order);

    /**
     * Method called by streams on every trade update.
     *
     * @param trade trade
     */
    void tradeUpdate(TradeDTO trade);

    /**
     * Method called by streams at every ticker update.
     *
     * @param ticker ticker
     */
    void tickerUpdate(TickerDTO ticker);

    /**
     * Return the gains made by all closed positions.
     *
     * @return gains by currency.
     */
    HashMap<CurrencyDTO, GainDTO> getGains();

}
