package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;
import tech.cassandre.trading.bot.strategy.GenericCassandreStrategy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Service allowing you to manage positions.
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
    PositionCreationResultDTO createLongPosition(GenericCassandreStrategy strategy,
                                                 CurrencyPairDTO currencyPair,
                                                 BigDecimal amount,
                                                 PositionRulesDTO rules);

    /**
     * Creates a short position with its associated rules.
     * Short position is nothing but selling share.
     * If you are bearish (means you think that price of X share are going to fall) at that time you sell some amount of share is called taking Short Position in share.
     *
     * @param strategy     strategy
     * @param currencyPair currency pair
     * @param amount       amount
     * @param rules        rules
     * @return position creation result
     */
    PositionCreationResultDTO createShortPosition(GenericCassandreStrategy strategy,
                                                  CurrencyPairDTO currencyPair,
                                                  BigDecimal amount,
                                                  PositionRulesDTO rules);

    /**
     * Update position rules.
     *
     * @param id       position id
     * @param newRules new rules
     */
    void updatePositionRules(long id, PositionRulesDTO newRules);

    /**
     * Close position (no matter the rules).
     *
     * @param id position id
     */
    void closePosition(long id);

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
     * Method called by streams on orders updates.
     *
     * @param orders orders updates
     */
    void ordersUpdates(Set<OrderDTO> orders);

    /**
     * Method called by streams on trades updates.
     *
     * @param trades trades updates
     */
    void tradesUpdates(Set<TradeDTO> trades);

    /**
     * Method called by streams on tickers updates.
     *
     * @param tickers tickers updates
     */
    void tickersUpdates(Set<TickerDTO> tickers);

    /**
     * Returns the amounts locked by every position.
     *
     * @return amounts locked by every position
     */
    Map<Long, CurrencyAmountDTO> amountsLockedByPosition();

    /**
     * Return the gains made by all closed positions.
     *
     * @return gains by currency.
     */
    Map<CurrencyDTO, GainDTO> getGains();

}
