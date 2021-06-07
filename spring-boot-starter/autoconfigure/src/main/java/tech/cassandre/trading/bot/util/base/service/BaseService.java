package tech.cassandre.trading.bot.util.base.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
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
        Bandwidth limit = Bandwidth.simple(1, Duration.ofMillis(1));
        bucket = Bucket4j.builder().addLimit(limit).build();
    }

    /**
     * Constructs a base service with a rate limit.
     *
     * @param rate rate in ms
     */
    public BaseService(final long rate) {
        Bandwidth limit = Bandwidth.simple(1, Duration.ofMillis(rate));
        bucket = Bucket4j.builder().addLimit(limit).build();
    }

}
