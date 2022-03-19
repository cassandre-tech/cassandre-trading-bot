package tech.cassandre.trading.bot.api.graphql.test.util.strategies;

import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.UNI;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Uniswap strategy.
 */
@CassandreStrategy(
        strategyId = "002",
        strategyName = "Uniswap")
public class UNIStrategy extends BasicCassandreStrategy {

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        return Set.of(new CurrencyPairDTO(UNI, USDT));
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        // Empty methods as we only tests the data stored in database.
        return accounts.stream().findAny();
    }

}
