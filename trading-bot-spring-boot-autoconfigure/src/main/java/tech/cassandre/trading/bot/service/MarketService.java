package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyAmountDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service giving information about market price.
 */
public interface MarketService {

    /**
     * Returns a ticker for a currency pair.
     *
     * @param currencyPair currency pair
     * @return ticker
     */
    Optional<TickerDTO> getTicker(CurrencyPairDTO currencyPair);

    /**
     * Returns the cost of buying an amount of a currency pair.
     *
     * @param currencyPair currency pair
     * @param amount amount
     * @return costs.
     */
    Optional<CurrencyAmountDTO> getEstimatedBuyingCost(CurrencyPairDTO currencyPair, BigDecimal amount);

}
