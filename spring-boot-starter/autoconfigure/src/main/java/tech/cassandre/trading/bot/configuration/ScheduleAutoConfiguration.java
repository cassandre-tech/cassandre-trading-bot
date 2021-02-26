package tech.cassandre.trading.bot.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;

import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ScheduleAutoConfiguration configures the flux calls.
 */
@Configuration
@Profile("!schedule-disabled")
@EnableScheduling
public class ScheduleAutoConfiguration {

    /** Indicate that the batch should be running. */
    private final AtomicBoolean enabled = new AtomicBoolean(true);

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
     * @param newAccountFlux account flux
     * @param newTickerFlux  ticker flux
     * @param newOrderFlux   order flux
     * @param newTradeFlux   trade flux
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
    @Scheduled(fixedDelay = 1)
    public void accountFluxUpdate() {
        if (enabled.get()) {
            accountFlux.update();
        }
    }

    /**
     * Recurrent calls the ticker flux.
     */
    @Scheduled(fixedDelay = 1)
    public void tickerFluxUpdate() {
        if (enabled.get()) {
            tickerFlux.update();
        }
    }

    /**
     * Recurrent calls the trade flux.
     */
    @Scheduled(fixedDelay = 1)
    public void tradeFluxUpdate() {
        if (enabled.get()) {
            orderFlux.update();
            tradeFlux.update();
        }
    }

    /**
     * This method is called before the application shutdown.
     * We stop the flux.
     */
    @PreDestroy
    public void shutdown() {
        enabled.set(false);
    }

}
