package tech.cassandre.trading.bot.test.core.strategy.multiple;

import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.util.Collections;
import java.util.Set;

import static tech.cassandre.trading.bot.test.core.strategy.multiple.MultipleStrategiesTest.BTC_USDT;
import static tech.cassandre.trading.bot.test.core.strategy.multiple.Strategy1.PARAMETER_STRATEGY_1_ENABLED;

/**
 * Strategy 1.
 */
@SuppressWarnings("unused")
@CassandreStrategy(
        strategyId = "01",
        strategyName = "Strategy 1")
@ConditionalOnProperty(
        value = PARAMETER_STRATEGY_1_ENABLED,
        havingValue = "true")
@Getter
public class Strategy1 extends Strategy {

    /** Strategy enabled parameter. */
    public static final String PARAMETER_STRATEGY_1_ENABLED = "strategy1.enabled";

    @Override
    public final Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        return Collections.singleton(BTC_USDT);
    }

}
