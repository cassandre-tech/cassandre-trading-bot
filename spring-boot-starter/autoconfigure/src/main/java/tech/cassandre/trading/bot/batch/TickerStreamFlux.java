package tech.cassandre.trading.bot.batch;

import info.bitrich.xchangestream.core.StreamingExchange;
import io.reactivex.disposables.Disposable;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.springframework.context.ApplicationContext;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.internal.CassandreStrategyInterface;
import tech.cassandre.trading.bot.util.base.batch.BaseFlux;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ticker stream flux - push {@link TickerDTO}.
 * It registers for tickers streams from the exchange.
 * To get a deep understanding of how it works, read the documentation of {@link BaseFlux}.
 */
//@RequiredArgsConstructor
public class TickerStreamFlux extends BaseFlux<TickerDTO> {

    /** ticker subscriptions. */
    private final Set<Disposable> tickerSubscriptions = new LinkedHashSet<>();

    private void processTicker(final Ticker ticker) {
        TickerDTO tickerDTO = TICKER_MAPPER.mapToTickerDTO(ticker);
        if (this.fluxSink != null) {
            emitValue(tickerDTO);
        }
    }

    public TickerStreamFlux(
            final ApplicationContext applicationContext,
            final StreamingExchange streamingExchange) {

        logger.info("Starting stream ticker flux");
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

        // Subscribe to live trades update.
        for (var currency : requestedCurrencyPairs) {
            CurrencyPair currencyPair = new CurrencyPair(currency.getBaseCurrency().getCurrencyCode(), currency.getQuoteCurrency().getCode());
            Disposable task = streamingExchange.getStreamingMarketDataService()
                    .getTicker(currencyPair)
                    .subscribe(
                            ticker -> processTicker(ticker),
                            throwable -> logger.error("Error in trade subscription", throwable));
            tickerSubscriptions.add(task);
        }
    }

    @Override
    protected final Set<TickerDTO> getNewValues() {

        Set<TickerDTO> result = new LinkedHashSet<>();
        return result;
    }

}
