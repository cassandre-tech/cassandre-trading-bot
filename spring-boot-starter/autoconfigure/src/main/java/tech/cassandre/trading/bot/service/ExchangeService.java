package tech.cassandre.trading.bot.service;

import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.Set;

/**
 * Service giving information about the exchange.
 */
public interface ExchangeService {

    /**
     * Get the list of available currency pairs for trading.
     *
     * @return list of currency pairs
     */
    Set<CurrencyPairDTO> getAvailableCurrencyPairs();

}
