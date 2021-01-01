package tech.cassandre.trading.bot.util.mapper;

import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.instrument.Instrument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.util.jpa.CurrencyAmount;

/**
 * Currency mapper.
 */
@Mapper
public interface CurrencyMapper {

    // =================================================================================================================
    // XChange to DTO.

    default CurrencyPairDTO mapToCurrencyPairDTO(Instrument source) {
        final CurrencyPair cp = (CurrencyPair) source;
        CurrencyDTO base = new CurrencyDTO(cp.base.getCurrencyCode());
        CurrencyDTO quote = new CurrencyDTO(cp.counter.getCurrencyCode());
        return CurrencyPairDTO.builder().baseCurrency(base).quoteCurrency(quote).build();
    }

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

    @Mapping(source = "value", target = "value")
    @Mapping(source = "currency", target = "currency")
    CurrencyAmountDTO mapToCurrencyAmountDTO(CurrencyAmount source);

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

    default CurrencyDTO mapToCurrencyDTO(String value) {
        return new CurrencyDTO(value);
    }

}
