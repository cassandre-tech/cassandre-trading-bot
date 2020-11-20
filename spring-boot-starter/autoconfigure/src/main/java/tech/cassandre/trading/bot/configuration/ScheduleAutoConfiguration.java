package tech.cassandre.trading.bot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;

/**
 * ScheduleAutoConfiguration configures the flux calls.
 */
@Configuration
@Profile("!schedule-disabled")
@EnableScheduling
public class ScheduleAutoConfiguration {

    /** Position update delay. */
    private static final long ONE_SECOND = 1_000;

    /** Account flux. */
    private final AccountFlux accountFlux;

    /** Ticker flux. */
    private final TickerFlux tickerFlux;

    /** Order flux. */
    private final OrderFlux orderFlux;

    /** Trade flux. */
    private final TradeFlux tradeFlux;

    /**
     * Constructor.
     *
     * @param newAccountFlux  account flux
     * @param newTickerFlux   ticker flux
     * @param newOrderFlux    order flux
     * @param newTradeFlux    trade flux
     */
    public ScheduleAutoConfiguration(final AccountFlux newAccountFlux,
                                     final TickerFlux newTickerFlux,
                                     final OrderFlux newOrderFlux,
                                     final TradeFlux newTradeFlux) {
        this.accountFlux = newAccountFlux;
        this.tickerFlux = newTickerFlux;
        this.orderFlux = newOrderFlux;
        this.tradeFlux = newTradeFlux;
    }

    /**
     * Recurrent calls the account flux.
     */
    // TODO Remove initialDelay
    @Scheduled(fixedDelay = 1, initialDelay = ONE_SECOND)
    public void setupAccountFlux() {
        accountFlux.update();
    }

    /**
     * Recurrent calls the ticker flux.
     */
    @Scheduled(fixedDelay = 1, initialDelay = ONE_SECOND)
    public void setupTickerFlux() {
        tickerFlux.update();
    }

    /**
     * Recurrent calls the order flux.
     */
    @Scheduled(fixedDelay = 1, initialDelay = ONE_SECOND)
    public void setupOrderFlux() {
        orderFlux.update();
    }

    /**
     * Recurrent calls the trade flux.
     */
    @Scheduled(fixedDelay = 1, initialDelay = ONE_SECOND)
    public void setupTradeFlux() {
        tradeFlux.update();
    }

}
