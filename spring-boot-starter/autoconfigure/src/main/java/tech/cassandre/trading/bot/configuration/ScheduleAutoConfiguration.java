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
 */
@Configuration
@Profile("!schedule-disabled")
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleAutoConfiguration extends BaseConfiguration {

    /** Scheduler pool size. */
    private static final int SCHEDULER_POOL_SIZE = 3;

    /** Start delay in milliseconds. */
    private static final int START_DELAY_IN_MILLISECONDS = 1_000;

    /** Termination delay in milliseconds. */
    private static final int TERMINATION_DELAY_IN_MILLISECONDS = 10_000;

    /** Thread prefix for schedulers. */
    private static final String THREAD_NAME_PREFIX = "cassandre-flux-";

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
     * Configure the task scheduler.
     *
     * @return task scheduler
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationMillis(TERMINATION_DELAY_IN_MILLISECONDS);
        scheduler.setThreadNamePrefix(THREAD_NAME_PREFIX);
        scheduler.setPoolSize(SCHEDULER_POOL_SIZE);
        scheduler.setErrorHandler(t -> {
            try {
                logger.error("Error in scheduled tasks: {}", t.getMessage());
            } catch (Exception e) {
                logger.error("Error in scheduled tasks: {}", e.getMessage());
            }
        });
        return scheduler;
    }

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
        if (enabled.get()) {
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
     * We stop the flux.
     */
    @PreDestroy
    public void shutdown() {
        enabled.set(false);
    }

}
