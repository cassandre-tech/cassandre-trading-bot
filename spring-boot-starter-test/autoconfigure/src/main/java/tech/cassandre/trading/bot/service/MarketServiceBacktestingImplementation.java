package tech.cassandre.trading.bot.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.BacktestingCandleRepository;
import tech.cassandre.trading.bot.repository.OrderRepository;
import tech.cassandre.trading.bot.repository.TradeRepository;
import tech.cassandre.trading.bot.util.exception.DryModeException;
import tech.cassandre.trading.bot.util.mapper.BacktestingTickerMapper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.awaitility.Awaitility.await;

/**
 * Market service - Backtesting implementation.
 */
@RequiredArgsConstructor
@Getter
public class MarketServiceBacktestingImplementation implements MarketService {

    /** Test session id. */
    private final String testSessionId = UUID.randomUUID().toString();

    /** Sequence - Which round of tickers are we treating. */
    private final AtomicLong sequence = new AtomicLong(1);

    /** Flux size of each currency pair. */
    private final Map<CurrencyPairDTO, Integer> fluxSize = new LinkedHashMap<>();

    /** Order flux. */
    private final OrderFlux orderFlux;

    /** Trade flux. */
    private final TradeFlux tradeFlux;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Backtesting tickers repository. */
    private final BacktestingCandleRepository backtestingCandleRepository;

    /** Backtesting mapper. */
    private static final BacktestingTickerMapper BACKTESTING_TICKER_MAPPER = Mappers.getMapper(BacktestingTickerMapper.class);

    @Override
    public final Optional<TickerDTO> getTicker(final CurrencyPairDTO currencyPair) {
        throw new DryModeException("getTicker() method is not available in dry mode. Use getTickers()");
    }

    @Override
    public final Set<TickerDTO> getTickers(final Set<CurrencyPairDTO> currencyPairs) {
        // Before replying, we check that all trades and orders arrived.
        await().until(() -> {
            orderFlux.update();
            tradeFlux.update();
            return orderRepository.count() == tradeRepository.count();
        });

        // We get the result for the corresponding sequence, and we only select the replies for the request currency pairs.
        return backtestingCandleRepository
                .findByIdTestSessionIdAndIdResponseSequenceId(testSessionId, sequence.getAndIncrement())
                .stream()
                .filter(ticker -> currencyPairs.contains(ticker.getCurrencyPairDTO()))
                .map(BACKTESTING_TICKER_MAPPER::mapToTickerDTO)
                .collect(Collectors.toSet());
    }

    /**
     * Return true if a specific flux is done.
     *
     * @param currencyPair currency pair
     * @return true if nothing left
     */
    public boolean isFluxDone(final CurrencyPairDTO currencyPair) {
        final Integer size = fluxSize.get(currencyPair);
        if (size == null) {
            return true;
        } else {
            // 7 calls >= 6 tickers => Flux done
            return sequence.get() >= size;
        }
    }

    /**
     * Return true if all flux are done.
     *
     * @return true if nothing left
     */
    public boolean isFluxDone() {
        return fluxSize.keySet()
                .stream()
                .filter(currencyPair -> sequence.get() <= fluxSize.get(currencyPair))
                .collect(Collectors.toSet())
                .isEmpty();
    }

}
