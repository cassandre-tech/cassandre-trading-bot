package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;

/**
 * Currency mapper.
 */
@Mapper
public interface CurrencyMapper {

    // =================================================================================================================
    // XChange to DTO.

    /**
     * Map Currency to CurrencyDTO.
     *
     * @param source Currency
     * @return CurrencyDTO
     */
    @Mapping(source = "currencyCode", target = "code")
    CurrencyDTO mapToCurrencyDTO(Currency source);

    /**
     * Map CurrencyPair to CurrencyPairDTO.
     *
     * @param source CurrencyPair
     * @return CurrencyPairDTO
     */
    @Mapping(source = "base", target = "baseCurrency")
    @Mapping(source = "counter", target = "quoteCurrency")
    CurrencyPairDTO mapToCurrencyPairDTO(CurrencyPair source);


    /***
     * Map String to CurrencyPairDTO.
     *
     * @param source string
     * @return CurrencyPairDTO
     */
    default CurrencyPairDTO mapToCurrencyPairDTO(String source) {
        return new CurrencyPairDTO(source);
    }

    /**
     * Map CurrencyPairDTO to String.
     *
     * @param source CurrencyPairDTO
     * @return String
     */
    default String mapToCurrencyPair(CurrencyPairDTO source) {
        return source.toString();
    }

    /**
     * Map CurrencyDTO to String.
     *
     * @param source CurrencyDTO
     * @return String
     */
    default String mapToCurrency(CurrencyDTO source) {
        if (source != null) {
            return source.toString();
        } else {
            return null;
        }
    }

}
