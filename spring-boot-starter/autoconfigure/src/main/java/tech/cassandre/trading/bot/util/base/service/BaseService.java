package tech.cassandre.trading.bot.util.base.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import tech.cassandre.trading.bot.util.base.Base;

import java.time.Duration;

/**
 * Base service.
 */
public abstract class BaseService extends Base {

    /** Bucket. */
    protected final Bucket bucket;

    /**
     * Construct a base service without rate limit.
     */
    public BaseService() {
        Bandwidth limit = Bandwidth.builder().capacity(1).refillGreedy(1, Duration.ofMillis(1)).build();
        bucket = Bucket.builder().addLimit(limit).build();
    }

    /**
     * Constructs a base service with a rate limit.
     *
     * @param rate rate in ms
     */
    public BaseService(final long rate) {
        Bandwidth limit = Bandwidth.builder().capacity(1).refillGreedy(1, Duration.ofMillis(rate)).build();
        bucket = Bucket.builder().addLimit(limit).build();
    }

}
