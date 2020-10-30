package tech.cassandre.trading.bot.util.base;

import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cassandre.trading.bot.util.mapper.CassandreMapper;

/**
 * Base.
 */
public abstract class Base {

    /** Logger. */
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /** Mapper. */
    private final CassandreMapper mapper = Mappers.getMapper(CassandreMapper.class);

    /**
     * Getter for logger.
     *
     * @return logger
     */
    protected final Logger getLogger() {
        return logger;
    }

    /**
     * Getter for mapper.
     *
     * @return mapper
     */
    protected final CassandreMapper getMapper() {
        return mapper;
    }

}
