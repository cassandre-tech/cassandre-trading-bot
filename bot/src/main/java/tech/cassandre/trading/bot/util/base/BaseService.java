package tech.cassandre.trading.bot.util.base;

import org.knowm.xchange.currency.CurrencyPair;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.mapper.CassandreMapper;

/**
 * Base service.
 */
@SuppressWarnings("unused")
public class BaseService {

	/** Logger. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/** Mapper. */
	private final CassandreMapper mapper = Mappers.getMapper(CassandreMapper.class);

	/**
	 * Getter logger.
	 *
	 * @return logger
	 */
	protected final Logger getLogger() {
		return logger;
	}

	/**
	 * Getter mapper.
	 *
	 * @return mapper
	 */
	protected final CassandreMapper getMapper() {
		return mapper;
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
