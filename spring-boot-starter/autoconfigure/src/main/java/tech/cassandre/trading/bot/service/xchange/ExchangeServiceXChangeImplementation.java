package tech.cassandre.trading.bot.service.xchange;

import org.knowm.xchange.Exchange;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.ExchangeService;
import tech.cassandre.trading.bot.util.base.BaseService;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
        logger.debug("ExchangeService - Retrieving available currency pairs");
        return exchange.getExchangeMetaData()
                .getCurrencyPairs()
                .keySet()
                .stream()
                .peek(cp -> logger.debug("ExchangeService - Adding currency pair {} ", cp))
                .map(currencyMapper::mapToCurrencyPairDTO)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
