package tech.cassandre.trading.bot.test.util;

import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static tech.cassandre.trading.bot.test.util.BaseTest.BTC_USDT;
import static tech.cassandre.trading.bot.test.util.BaseTest.ETH_USDT;
import static tech.cassandre.trading.bot.test.util.BaseTest.KCS_BTC;
import static tech.cassandre.trading.bot.test.util.BaseTest.KCS_USDT;
import static tech.cassandre.trading.bot.test.util.BaseTest.PARAMETER_ONLY_TICKERS_STRATEGY_ENABLED;

@CassandreStrategy
@ConditionalOnProperty(
        value = PARAMETER_ONLY_TICKERS_STRATEGY_ENABLED,
        havingValue = "true")
@Getter
public class OnlyTickersStrategy extends BasicCassandreStrategy {

    /** Sequence - Which service call are we treating. */
    private final AtomicLong sequence = new AtomicLong(1);

    private final Map<Long, Map<CurrencyPairDTO, TickerDTO>> tickersReceived = new LinkedHashMap<>();

    @Override
    public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
        Set<CurrencyPairDTO> list = new LinkedHashSet<>();
        list.add(BTC_USDT);
        list.add(ETH_USDT);
        list.add(KCS_USDT);
        list.add(KCS_BTC);  // This currency pair not in the imported files.
        return list;
    }

    @Override
    public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
        return accounts.stream().filter(a -> "trade".equals(a.getAccountId())).findFirst();
    }

    @Override
    public void onTickersUpdates(Map<CurrencyPairDTO, TickerDTO> tickers) {
        // In this method, to allow testing, we retrieve all the reply for each call.
        if (!tickers.isEmpty()) {
            tickersReceived.put(sequence.getAndIncrement(), tickers);
        }
    }

}
