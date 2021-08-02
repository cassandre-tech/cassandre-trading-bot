package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionCreationResultDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.position.PositionRulesDTO;
import tech.cassandre.trading.bot.dto.trade.OrderCreationResultDTO;
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
 * Service allowing you to manage your positions.
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
     * Close a position - This method is used by Cassandre internally.
     *
     * @param strategy strategy
     * @param id       position id
     * @param ticker   ticker
     * @return order creation result
     */
    OrderCreationResultDTO closePosition(GenericCassandreStrategy strategy, long id, TickerDTO ticker);

    /**
     * Force a position to close (no matter the rules) - This method can be use by user code.
     *
     * @param id position id
     */
    void forcePositionClosing(long id);

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
     * Returns the amounts locked by each position.
     *
     * @return amounts locked by each position
     */
    Map<Long, CurrencyAmountDTO> getAmountsLockedByPosition();

    /**
     * Return the gains made by all closed positions.
     *
     * @return gains by currency.
     */
    Map<CurrencyDTO, GainDTO> getGains();

}
