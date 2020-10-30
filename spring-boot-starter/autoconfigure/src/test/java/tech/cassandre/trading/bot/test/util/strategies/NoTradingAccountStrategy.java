package tech.cassandre.trading.bot.test.util.strategies;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.test.util.strategies.NoTradingAccountStrategy.PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED;

/**
 * Strategy with not trading account.
 */
@SuppressWarnings("unused")
@CassandreStrategy(name = "Testable strategy without existing trading account")
@ConditionalOnProperty(
        value = PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED,
        havingValue = "true")
public class NoTradingAccountStrategy extends BasicCassandreStrategy {

    /** Strategy without existing account enabled parameter. */
    public static final String PARAMETER_NO_TRADING_ACCOUNT_STRATEGY_ENABLED = "noTradingAccountStrategy.enabled";

    @Override
    public final Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        Set<CurrencyPairDTO> requestedTickers = new LinkedHashSet<>();
        requestedTickers.add(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC));
        requestedTickers.add(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.USDT));
        return requestedTickers;
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        return accounts.stream().filter(a -> "non_existing".equals(a.getId())).findFirst();
    }

}
