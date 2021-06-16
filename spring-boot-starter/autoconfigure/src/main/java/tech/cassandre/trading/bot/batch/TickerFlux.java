package tech.cassandre.trading.bot.batch;

import lombok.RequiredArgsConstructor;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.springframework.context.ApplicationContext;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.service.MarketService;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ticker flux - push {@link TickerDTO}.
 */
@RequiredArgsConstructor
public class TickerFlux extends BaseFlux<TickerDTO> {

    /** Application context. */
    private final ApplicationContext applicationContext;

    /** Market service. */
    private final MarketService marketService;

    @Override
    protected final Set<TickerDTO> getNewValues() {
        logger.debug("TickerFlux - Retrieving tickers from exchange");
        Set<TickerDTO> newValues = new LinkedHashSet<>();

        // We retrieve the list of currency pairs asked by all strategies.
        final LinkedHashSet<CurrencyPairDTO> requestedCurrencyPairs = applicationContext
                .getBeansWithAnnotation(CassandreStrategy.class)
                .values()
                .stream()
                .map(o -> ((CassandreStrategyInterface) o))
                .map(CassandreStrategyInterface::getRequestedCurrencyPairs)
                .flatMap(Set::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        try {
            // Get all tickers at once from market service if the method is implemented.
            marketService.getTickers(requestedCurrencyPairs).stream()
                    .filter(Objects::nonNull)
                    .forEach(tickerDTO -> {
                    logger.debug("TickerFlux - New ticker received: {}", tickerDTO);
                    newValues.add(tickerDTO);
            });
        } catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException e) {
            // If getAllTickers is not available, we retrieve tickers one bye one.
            requestedCurrencyPairs.stream()
                    .filter(Objects::nonNull)
                    .forEach(currencyPairDTO -> marketService.getTicker(currencyPairDTO).ifPresent(tickerDTO -> {
                logger.debug("TickerFlux - New ticker received: {}", tickerDTO);
                newValues.add(tickerDTO);
            }));
        }

        return newValues;
    }

    @Override
    protected final Set<TickerDTO> saveValues(final Set<TickerDTO> newValues) {
        // We don't save tickers in database.
        return newValues;
    }

}
