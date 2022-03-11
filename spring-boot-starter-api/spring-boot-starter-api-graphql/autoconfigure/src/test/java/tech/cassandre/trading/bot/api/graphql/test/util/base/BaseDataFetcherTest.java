package tech.cassandre.trading.bot.api.graphql.test.util.base;

import tech.cassandre.trading.bot.dto.strategy.StrategyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyAmountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.dto.util.GainDTO;

import java.math.BigDecimal;
import java.util.Map;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Base for data fetcher test.
 */
public abstract class BaseDataFetcherTest {

    /** UNI. */
    public static final CurrencyDTO UNI = new CurrencyDTO("UNI");

    /** BTC/USDT. */
    public static final CurrencyPairDTO BTC_USDT = new CurrencyPairDTO(BTC, USDT);

    /** UNI/USDT. */
    public static final CurrencyPairDTO UNI_USDT = new CurrencyPairDTO(UNI, USDT);

    // TODO Delete all those ugly methods!

    /**
     * Returns a strategy value from graphql result.
     *
     * @param graphqlResult graphql result
     * @return StrategyDTO
     */
    public final StrategyDTO getStrategyValue(Object graphqlResult) {
        Map<String, String> values = (Map<String, String>) graphqlResult;
        return StrategyDTO.builder()
                .strategyId(values.get("strategyId"))
                .build();
    }

    /**
     * Returns a currency pair value from a graphql result.
     *
     * @param graphqlResult graphql result
     * @return CurrencyPairDTO
     */
    public final CurrencyPairDTO getCurrencyPairValue(Object graphqlResult) {
        Map<String, Map<String, String>> values = (Map<String, Map<String, String>>) graphqlResult;
        final CurrencyDTO baseCurrency = new CurrencyDTO(values.get("baseCurrency").get("code"));
        final CurrencyDTO quoteCurrency = new CurrencyDTO(values.get("quoteCurrency").get("code"));
        return new CurrencyPairDTO(baseCurrency, quoteCurrency);
    }

    /**
     * Returns a currency amount value from a graphql result.
     *
     * @param graphqlResult graphql result
     * @return CurrencyAmountDTO
     */
    public final CurrencyAmountDTO getCurrencyAmountValue(Object graphqlResult) {
        Map<String, Double> amountValue = (Map<String, Double>) graphqlResult;
        Map<String, Map<String, String>> currencyValue = (Map<String, Map<String, String>>) graphqlResult;
        final BigDecimal value = new BigDecimal(String.valueOf(amountValue.get("value")));
        final CurrencyDTO quoteCurrency = new CurrencyDTO(currencyValue.get("currency").get("code"));
        return new CurrencyAmountDTO(value, quoteCurrency);
    }

    /**
     * Returns a gain value from a graphql result.
     *
     * @param graphqlResult graphql result
     * @return GainDTO
     */
    public final GainDTO getGainValue(Object graphqlResult) {
        Map<String, Object> value = (Map<String, Object>) graphqlResult;
        return GainDTO.builder()
                .percentage((Double) value.get("percentage"))
                .amount(getCurrencyAmountValue(value.get("amount")))
                .build();
    }

}
