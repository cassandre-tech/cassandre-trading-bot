package tech.cassandre.trading.bot.util.base;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import org.knowm.xchange.currency.CurrencyPair;
import org.mapstruct.factory.Mappers;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.mapper.CassandreMapper;

import java.time.Duration;

/**
 * Base service.
 */
@SuppressWarnings("unused")
public abstract class BaseService extends Base {

    /** Mapper. */
    private final CassandreMapper mapper = Mappers.getMapper(CassandreMapper.class);

    /** Bucket. */
    private final Bucket bucket;

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

    /**
     * Getter for mapper.
     *
     * @return mapper
     */
    protected final CassandreMapper getMapper() {
        return mapper;
    }

    /**
     * Getter for bucket.
     *
     * @return bucket
     */
    public final Bucket getBucket() {
        return bucket;
    }

    /**
     * Returns a XChange currency pair from a currency pair DTO.
     *
     * @param currencyPairDTO currency pair DTO
     * @return XChange currency pair
     */
    protected CurrencyPair getCurrencyPair(final CurrencyPairDTO currencyPairDTO) {
        // TODO Use a mapper instead of this method when MapStruct will allow it.
        // https://github.com/mapstruct/mapstruct/issues/73
        return new CurrencyPair(currencyPairDTO.getBaseCurrency().getCode(), currencyPairDTO.getQuoteCurrency().getCode());
    }

}
