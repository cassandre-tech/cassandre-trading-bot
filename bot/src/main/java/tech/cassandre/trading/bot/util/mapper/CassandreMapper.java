package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

/**
 * Cassandre mapper.
 */
@SuppressWarnings("unused")
@Mapper
public interface CassandreMapper {

	/**
	 * Map CurrencyPair to CurrencyPairDTO.
	 *
	 * @param source CurrencyPair
	 * @return CurrencyPairDTO
	 */
	@Mapping(source = "base", target = "baseCurrency")
	@Mapping(source = "counter", target = "quoteCurrency")
	CurrencyPairDTO mapToCurrencyPairDTO(CurrencyPair source);

	/**
	 * Map Currency to CurrencyDTO.
	 *
	 * @param source Currency
	 * @return CurrencyDTO
	 */
	@Mapping(source = "currencyCode", target = "code")
	CurrencyDTO mapToCurrencyDTO(Currency source);

}
