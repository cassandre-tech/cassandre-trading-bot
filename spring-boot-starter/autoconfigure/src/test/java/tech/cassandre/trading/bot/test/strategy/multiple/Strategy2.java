package tech.cassandre.trading.bot.test.strategy.multiple;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.util.LinkedHashSet;
import java.util.Set;

import static tech.cassandre.trading.bot.test.strategy.multiple.MultipleStrategiesTest.BTC_ETH;
import static tech.cassandre.trading.bot.test.strategy.multiple.Strategy2.PARAMETER_STRATEGY_2_ENABLED;

/**
 * Strategy 2.
 */
@SuppressWarnings("unused")
@CassandreStrategy(
        strategyId = "02",
        strategyName = "Strategy 2")
@ConditionalOnProperty(
        value = PARAMETER_STRATEGY_2_ENABLED,
        havingValue = "true")
public class Strategy2 extends Strategy {

    /** Strategy enabled parameter. */
    public static final String PARAMETER_STRATEGY_2_ENABLED = "strategy2.enabled";

    @Override
    public final Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        Set<CurrencyPairDTO> requestedTickers = new LinkedHashSet<>();
        requestedTickers.add(BTC_ETH);
        return requestedTickers;
    }

}
