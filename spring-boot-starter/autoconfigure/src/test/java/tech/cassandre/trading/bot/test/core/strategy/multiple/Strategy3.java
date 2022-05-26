package tech.cassandre.trading.bot.test.core.strategy.multiple;

import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static tech.cassandre.trading.bot.test.core.strategy.multiple.MultipleStrategiesTest.BTC_USDT;
import static tech.cassandre.trading.bot.test.core.strategy.multiple.MultipleStrategiesTest.ETH_USDT;
import static tech.cassandre.trading.bot.test.core.strategy.multiple.Strategy3.PARAMETER_STRATEGY_3_ENABLED;

/**
 * Strategy 3.
 */
@SuppressWarnings("unused")
@CassandreStrategy(
        strategyId = "03",
        strategyName = "Strategy 3")
@ConditionalOnProperty(
        value = PARAMETER_STRATEGY_3_ENABLED,
        havingValue = "true")
@Getter
public class Strategy3 extends Strategy {

    /** Strategy enabled parameter. */
    public static final String PARAMETER_STRATEGY_3_ENABLED = "strategy3.enabled";

    @Override
    public final Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        return Stream.of(BTC_USDT, ETH_USDT).collect(Collectors.toSet());
    }

}
