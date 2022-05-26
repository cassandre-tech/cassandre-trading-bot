package tech.cassandre.trading.bot.api.graphql.test.util.base;

import tech.cassandre.trading.bot.api.graphql.client.generated.types.CurrencyPair;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Base for data fetcher test.
 */
public abstract class BaseDataFetcherTest {

    /** BTC/USDT. */
    public static final CurrencyPair BTC_USDT = new CurrencyPair(BTC, USDT);

}
