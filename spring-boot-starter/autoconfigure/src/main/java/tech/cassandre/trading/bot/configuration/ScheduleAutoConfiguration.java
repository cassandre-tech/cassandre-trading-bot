package tech.cassandre.trading.bot.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import tech.cassandre.trading.bot.batch.AccountFlux;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TickerFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.util.base.configuration.BaseConfiguration;

import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ScheduleAutoConfiguration configures the flux calls.
 * Three scheduled tasks:
 * - One calling account flux.
 * - One calling ticker flux.
 * - One calling order and trade flux.
 */
@Profile("!schedule-disabled")
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleAutoConfiguration extends BaseConfiguration {

    /** Scheduler pool size. */
    private static final int SCHEDULER_POOL_SIZE = 4;

    /** Start delay in milliseconds (1 000 ms = 1 second). */
    private static final int START_DELAY_IN_MILLISECONDS = 1_000;

    /** Termination delay in milliseconds (10 000 ms = 10 seconds). */
    private static final int TERMINATION_DELAY_IN_MILLISECONDS = 10_000;

    /** Flux continues to run as long as enabled is set to true. */
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
     * Recurrent calls to the account flux.
     */
    @Scheduled(initialDelay = START_DELAY_IN_MILLISECONDS, fixedDelay = 1)
    public void accountFluxUpdate() {
        if (enabled.get()) {
            accountFlux.update();
        }
    }

    /**
     * Recurrent calls to the ticker flux.
     */
    @Scheduled(initialDelay = START_DELAY_IN_MILLISECONDS, fixedDelay = 1)
    public void tickerFluxUpdate() {
        if (enabled.get() && tickerFlux != null) {
            tickerFlux.update();
        }
    }

    /**
     * Recurrent calls to the order and trade flux.
     */
    @Scheduled(initialDelay = START_DELAY_IN_MILLISECONDS, fixedDelay = 1)
    public void orderAndTradeFluxUpdate() {
        if (enabled.get()) {
            orderFlux.update();
            tradeFlux.update();
        }
    }

    /**
     * This method is called before the application shutdown.
     * We stop calling the flux.
     */
    @PreDestroy
    public void shutdown() {
        enabled.set(false);
    }

    /**
     * Configure the task scheduler.
     *
     * @return task scheduler
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationMillis(TERMINATION_DELAY_IN_MILLISECONDS);
        scheduler.setThreadNamePrefix("cassandre-flux-");
        scheduler.setPoolSize(SCHEDULER_POOL_SIZE);
        scheduler.setErrorHandler(throwable -> {
            try {
                logger.error("Error in scheduled tasks: {}", throwable.getMessage());
            } catch (Exception exception) {
                logger.error("Error in scheduled tasks: {}", exception.getMessage());
            }
        });
        return scheduler;
    }

}
