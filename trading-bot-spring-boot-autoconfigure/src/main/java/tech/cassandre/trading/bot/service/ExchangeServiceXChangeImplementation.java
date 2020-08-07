package tech.cassandre.trading.bot.service;

import org.knowm.xchange.Exchange;
import tech.cassandre.trading.bot.util.base.BaseService;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Exchange service - XChange implementation.
 */
public class ExchangeServiceXChangeImplementation extends BaseService implements ExchangeService {

    /** XChange service. */
    private final Exchange exchange;

    /**
     * Constructor.
     *
     * @param newExchange exchange
     */
    public ExchangeServiceXChangeImplementation(final Exchange newExchange) {
        this.exchange = newExchange;
    }

    @Override
    public final Set<CurrencyPairDTO> getAvailableCurrencyPairs() {
        getLogger().debug("ExchangeService - Retrieving available currency pairs");
        Set<CurrencyPairDTO> availableCurrencyPairs = new LinkedHashSet<>();
        exchange.getExchangeMetaData()
                .getCurrencyPairs()
                .forEach((currencyPair, currencyPairMetaData) -> {
                    CurrencyDTO base = getMapper().mapToCurrencyDTO(currencyPair.base);
                    CurrencyDTO counter = getMapper().mapToCurrencyDTO(currencyPair.counter);
                    CurrencyPairDTO cp = new CurrencyPairDTO(base, counter);
                    availableCurrencyPairs.add(cp);
                    getLogger().debug("ExchangeService - Adding currency pair {} ", cp);
                });
        return availableCurrencyPairs;
    }

}
