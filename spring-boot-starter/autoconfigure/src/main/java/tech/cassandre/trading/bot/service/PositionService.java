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
import tech.cassandre.trading.bot.strategy.internal.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.internal.CassandreStrategyInterface;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Service managing positions.
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
    PositionCreationResultDTO createLongPosition(CassandreStrategy strategy,
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
    PositionCreationResultDTO createShortPosition(CassandreStrategy strategy,
                                                  CurrencyPairDTO currencyPair,
                                                  BigDecimal amount,
                                                  PositionRulesDTO rules);

    /**
     * Update position rules.
     *
     * @param positionUid position uid
     * @param newRules    new rules
     */
    void updatePositionRules(long positionUid, PositionRulesDTO newRules);

    /**
     * Close a position.
     *
     * @param strategy    strategy
     * @param positionUid position uid
     * @param ticker      ticker
     * @return order creation result
     */
    OrderCreationResultDTO closePosition(CassandreStrategyInterface strategy, long positionUid, TickerDTO ticker);

    /**
     * Set auto close value on a specific position.
     * If true, Cassandre will close the position according to rules.
     * if false, Cassandre will never close the position itself.
     *
     * @param positionUid position uid
     * @param value       auto close value
     */
    void setAutoClose(long positionUid, boolean value);

    /**
     * Force a position to close (no matter the rules).
     *
     * @param positionUid position uid
     */
    void forcePositionClosing(long positionUid);

    /**
     * Get position by position uid.
     *
     * @param positionUid position uid
     * @return position
     */
    Optional<PositionDTO> getPositionByUid(long positionUid);

    /**
     * Get positions.
     *
     * @return position list
     */
    Set<PositionDTO> getPositions();

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

    /**
     * Return the gains made by all closed positions of a strategy.
     * If strategyUid equals 0, returns all the gains.
     *
     * @param strategyUid strategy uid
     * @return gains by currency.
     */
    Map<CurrencyDTO, GainDTO> getGains(long strategyUid);

}
