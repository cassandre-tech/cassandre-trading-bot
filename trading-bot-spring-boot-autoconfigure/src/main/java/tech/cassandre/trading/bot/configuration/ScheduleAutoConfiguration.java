package tech.cassandre.trading.bot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;

/**
 * StrategyAutoConfiguration class configures the strategy.
 */
@Configuration
@Profile("!schedule-disabled")
@EnableScheduling
public class ScheduleAutoConfiguration {

	/** Account flux. */
	private final AccountFlux accountFlux;

	/** Ticker flux. */
	private final TickerFlux tickerFlux;

	/** Order flux. */
	private final OrderFlux orderFlux;

	/**
	 * Constructor.
	 *
	 * @param newAccountFlux account flux
	 * @param newTickerFlux  ticker flux
	 * @param newOrderFlux   order flux
	 */
	public ScheduleAutoConfiguration(final AccountFlux newAccountFlux,
	                                 final TickerFlux newTickerFlux,
	                                 final OrderFlux newOrderFlux) {
		this.accountFlux = newAccountFlux;
		this.tickerFlux = newTickerFlux;
		this.orderFlux = newOrderFlux;
	}

	/**
	 * Recurrent calls the account flux.
	 */
	@Scheduled(fixedRateString = "${cassandre.trading.bot.exchange.rates.account}")
	public void setupAccountFlux() {
		accountFlux.update();
	}

	/**
	 * Recurrent calls the ticker flux.
	 */
	@Scheduled(fixedRateString = "${cassandre.trading.bot.exchange.rates.ticker}")
	public void setupTickerFlux() {
		tickerFlux.update();
	}

	/**
	 * Recurrent calls the order flux.
	 */
	@Scheduled(fixedRateString = "${cassandre.trading.bot.exchange.rates.order}")
	public void setupOrderFlux() {
		orderFlux.update();
	}

}
