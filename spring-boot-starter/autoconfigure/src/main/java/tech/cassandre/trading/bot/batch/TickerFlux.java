package tech.cassandre.trading.bot.batch;

import lombok.RequiredArgsConstructor;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.springframework.context.ApplicationContext;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.internal.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ticker flux - push {@link TickerDTO}.
 * Two methods override from super class:
 * - getNewValues(): calling market service to retrieve tickers from exchange.
 * - saveValues(): not implemented as we don't store tickers data in database.
 * To get a deep understanding of how it works, read the documentation of {@link BaseFlux}.
 */
@RequiredArgsConstructor
public class TickerFlux extends BaseFlux<TickerDTO> {

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Market service. */
    private final MarketService marketService;

    @Override
    protected final Set<TickerDTO> getNewValues() {
        // We retrieve the list of currency pairs asked by all strategies.
        // Some users coded a getRequestedCurrencyPairs() that returns different results.
        final LinkedHashSet<CurrencyPairDTO> requestedCurrencyPairs = applicationContext
                .getBeansWithAnnotation(CassandreStrategy.class)
                .values()
                .stream()
                .filter(Objects::nonNull)
                .map(object -> (CassandreStrategyInterface) object)
                .map(CassandreStrategyInterface::getRequestedCurrencyPairs)
                .flatMap(Set::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        // We try to retrieve all tickers at once and if not working, one by one.
        try {
            // Get all tickers at once from market service if the method is implemented.
            return marketService.getTickers(requestedCurrencyPairs).stream()
                    .filter(Objects::nonNull)
                    .peek(tickerDTO -> logger.debug("Retrieved ticker from exchange: {}", tickerDTO))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException e) {
            // If getAllTickers is not implemented, we retrieve tickers one bye one.
            return requestedCurrencyPairs.stream()
                    .map(marketService::getTicker)
                    .filter(Optional::isPresent)
                    .peek(tickerDTO -> logger.debug("Retrieved ticker from exchange: {}", tickerDTO))
                    .map(Optional::get)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
    }

}
