package tech.cassandre.trading.bot.service;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.marketdata.MarketDataService;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.util.base.BaseService;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.io.IOException;
import java.util.Optional;

/**
 * Market service - XChange implementation.
 */
public class MarketServiceXChangeImplementation extends BaseService implements MarketService {

	/** XChange Market data service. */
	private final MarketDataService marketDataService;

	/**
	 * Constructor.
	 *
	 * @param newMarketDataService market data service
	 */
	public MarketServiceXChangeImplementation(final MarketDataService newMarketDataService) {
		this.marketDataService = newMarketDataService;
	}

	@Override
	public final Optional<TickerDTO> getTicker(final CurrencyPairDTO currencyPair) {
		try {
			getLogger().debug("MarketServiceXChangeImplementation - Getting ticker for {}", currencyPair);
			CurrencyPair cp = new CurrencyPair(currencyPair.getBaseCurrency().getCode(), currencyPair.getQuoteCurrency().getCode());
			TickerDTO t = getMapper().mapToTickerDTO(marketDataService.getTicker(cp));
			getLogger().debug("MarketServiceXChangeImplementation - Retrieved value is : {}", t);
			return Optional.ofNullable(t);
		} catch (IOException e) {
			getLogger().error("MarketServiceXChangeImplementation + Error retrieving ticker about {} : {}", currencyPair, e.getMessage());
			return Optional.empty();
		}
	}

}
