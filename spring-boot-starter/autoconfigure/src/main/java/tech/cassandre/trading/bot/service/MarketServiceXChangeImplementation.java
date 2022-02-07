package tech.cassandre.trading.bot.service;

import lombok.NonNull;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.service.marketdata.MarketDataService;
import org.knowm.xchange.service.marketdata.params.CurrencyPairsParam;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.base.service.BaseService;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Market service - XChange implementation of {@link MarketService}.
 */
public class MarketServiceXChangeImplementation extends BaseService implements MarketService {

    /** XChange service. */
    private final MarketDataService marketDataService;

    /**
     * Constructor.
     *
     * @param rate                 rate in ms
     * @param newMarketDataService market data service
     */
    public MarketServiceXChangeImplementation(final long rate, final MarketDataService newMarketDataService) {
        super(rate);
        this.marketDataService = newMarketDataService;
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public Optional<TickerDTO> getTicker(@NonNull final CurrencyPairDTO currencyPair) {
        try {
            // Consume a token from the token bucket.
            // If a token is not available this method will block until the refill adds one to the bucket.
            bucket.asBlocking().consume(1);

            logger.debug("Retrieving ticker for {} currency pair", currencyPair);
            TickerDTO t = TICKER_MAPPER.mapToTickerDTO(marketDataService.getTicker(CURRENCY_MAPPER.mapToInstrument(currencyPair)));
            logger.debug(" - New ticker {}", t);
            return Optional.ofNullable(t);
        } catch (IOException e) {
            logger.error("Error retrieving ticker: {}", e.getMessage());
            return Optional.empty();
        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("checkstyle:DesignForExtension")
    public Set<TickerDTO> getTickers(@NonNull final Set<CurrencyPairDTO> currencyPairs) {
        try {
            // We create the currency pairs parameter required by some exchanges.
            CurrencyPairsParam params = () -> currencyPairs
                    .stream()
                    .map(CURRENCY_MAPPER::mapToCurrencyPair)
                    .toList();

            // Consume a token from the token bucket.
            // If a token is not available this method will block until the refill adds one to the bucket.
            bucket.asBlocking().consume(1);

            logger.debug("Retrieving ticker for {} currency pair", currencyPairs.size());
            final List<Ticker> tickers = marketDataService.getTickers(params);
            return tickers.stream()
                    .map(TICKER_MAPPER::mapToTickerDTO)
                    .peek(t -> logger.debug(" - New ticker: {}", t))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (IOException e) {
            logger.error("Error retrieving tickers: {}", e.getMessage());
            return Collections.emptySet();
        } catch (InterruptedException e) {
            return Collections.emptySet();
        }
    }

}
