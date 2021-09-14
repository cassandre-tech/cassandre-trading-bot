package tech.cassandre.trading.bot.api.graphql.test.util.strategies;

import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicTa4jCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Bitcoin strategy.
 */
@CassandreStrategy(
        strategyId = "001",
        strategyName = "Bitcoin")
public class BTCStrategy extends BasicTa4jCassandreStrategy {

    @Override
    public CurrencyPairDTO getRequestedCurrencyPair() {
        return new CurrencyPairDTO(BTC, USDT);
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        // Empty methods as we only tests the data stored in database.
        return accounts.stream().findAny();
    }

    @Override
    public int getMaximumBarCount() {
        // Empty methods as we only tests the data stored in database.
        return 1;
    }

    @Override
    public Duration getDelayBetweenTwoBars() {
        // Empty methods as we only tests the data stored in database.
        return Duration.ofDays(1);
    }

    @Override
    public Strategy getStrategy() {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(getSeries());
        SMAIndicator sma = new SMAIndicator(closePrice, getMaximumBarCount());
        return new BaseStrategy(new OverIndicatorRule(sma, closePrice), new UnderIndicatorRule(sma, closePrice));
    }

    @Override
    public void shouldEnter() {
        // Empty methods as we only tests the data stored in database.
    }

    @Override
    public void shouldExit() {
        // Empty methods as we only tests the data stored in database.
    }

}
