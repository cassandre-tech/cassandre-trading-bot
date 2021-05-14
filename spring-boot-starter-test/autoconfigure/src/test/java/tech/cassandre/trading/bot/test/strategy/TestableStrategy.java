package tech.cassandre.trading.bot.test.strategy;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.ETH;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Testable strategy.
 */
@SuppressWarnings("unused")
@CassandreStrategy(strategyName = "Testable strategy")
public final class TestableStrategy extends BasicCassandreStrategy {

    /** Tickers update received. */
    private final List<TickerDTO> tickersUpdateReceived = new LinkedList<>();

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        Set<CurrencyPairDTO> list = new LinkedHashSet<>();
        list.add(new CurrencyPairDTO(BTC, USDT));
        list.add(new CurrencyPairDTO(ETH, BTC));
        return list;
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        return accounts.stream().filter(a -> a.getAccountId().equals("trade")).findFirst();
    }

    @Override
    public final void onTickersUpdate(final Map<CurrencyPairDTO, TickerDTO> tickers) {
        tickersUpdateReceived.addAll(tickers.values());
    }

    /**
     * Getter tickersUpdateReceived.
     *
     * @return tickersUpdateReceived
     */
    public final List<TickerDTO> getTickersUpdateReceived() {
        return tickersUpdateReceived;
    }

}
