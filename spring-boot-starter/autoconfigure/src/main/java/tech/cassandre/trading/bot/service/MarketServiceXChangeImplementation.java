package tech.cassandre.trading.bot.service;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.marketdata.MarketDataService;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.util.base.BaseService;
import tech.cassandre.trading.bot.util.dto.CurrencyAmountDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Market service - XChange implementation.
 */
public class MarketServiceXChangeImplementation extends BaseService implements MarketService {

    /** XChange service. */
    private final MarketDataService marketDataService;

    /** Last tickers received. */
    private final Map<CurrencyPairDTO, TickerDTO> lastTickers = new LinkedHashMap<>();

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
    public final Optional<TickerDTO> getTicker(final CurrencyPairDTO currencyPair) {
        try {
            // Consume a token from the token bucket.
            // If a token is not available this method will block until the refill adds one to the bucket.
            getBucket().asScheduler().consume(1);

            getLogger().debug("MarketService - Getting ticker for {}", currencyPair);
            CurrencyPair cp = new CurrencyPair(currencyPair.getBaseCurrency().getCode(), currencyPair.getQuoteCurrency().getCode());
            TickerDTO t = getMapper().mapToTickerDTO(marketDataService.getTicker(cp));
            getLogger().debug("MarketService - Retrieved value is : {}", t);
            lastTickers.put(currencyPair, t);
            return Optional.ofNullable(t);
        } catch (IOException e) {
            getLogger().error("MarketService - Error retrieving ticker about {} : {}", currencyPair, e.getMessage());
            return Optional.empty();
        } catch (InterruptedException e) {
            getLogger().error("MarketService - InterruptedException {} : {}", currencyPair, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public final Optional<CurrencyAmountDTO> getEstimatedBuyingCost(final CurrencyPairDTO currencyPair, final BigDecimal amount) {
        /*
            symbol=ETH-BTC
            {
              "time": 1598626640265,
              "sequence": "1594421123246",
              "price": "0.034227",
              "size": "0.0200088",
              "bestBid": "0.034226",
              "bestBidSize": "6.3384368",
              "bestAsk": "0.034227",
              "bestAskSize": "18.6378851"
            }
            This means 1 Ether can be bought with 0.034227 Bitcoin.
         */

        // We get the last ticker from the last values received.
        final TickerDTO ticker = lastTickers.get(currencyPair);
        if (ticker == null) {
            // No ticker for this currency pair.
            return Optional.empty();
        } else {
            // Make the calculation.
            return Optional.of(new CurrencyAmountDTO(ticker.getLast().multiply(amount),
                    currencyPair.getQuoteCurrency()));
        }
    }

}
