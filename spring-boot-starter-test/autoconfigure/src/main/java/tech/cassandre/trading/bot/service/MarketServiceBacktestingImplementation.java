package tech.cassandre.trading.bot.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import tech.cassandre.trading.bot.batch.OrderFlux;
import tech.cassandre.trading.bot.batch.TradeFlux;
import tech.cassandre.trading.bot.domain.BacktestingTicker;
import tech.cassandre.trading.bot.domain.BacktestingTickerId;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.repository.BacktestingTickerRepository;
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

    /** Flux size for each currency pair. */
    private final Map<CurrencyPairDTO, Long> fluxSize = new LinkedHashMap<>();

    /** Order flux. */
    private final OrderFlux orderFlux;

    /** Trade flux. */
    private final TradeFlux tradeFlux;

    /** Order repository. */
    private final OrderRepository orderRepository;

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /** Backtesting tickers repository. */
    private final BacktestingTickerRepository backtestingTickerRepository;

    /** Backtesting mapper. */
    private final BacktestingTickerMapper backtestingTickerMapper = Mappers.getMapper(BacktestingTickerMapper.class);

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
        return backtestingTickerRepository
                .findByIdTestSessionIdAndIdResponseSequenceId(testSessionId, sequence.getAndIncrement())
                .stream()
                .filter(ticker -> currencyPairs.contains(ticker.getCurrencyPairDTO()))
                .map(backtestingTickerMapper::mapToTickerDTO)
                .collect(Collectors.toSet());
    }

    /**
     * Add a ticker to load in database.
     *
     * @param tickerSequence sequence
     * @param tickerDTO      ticker
     */
    public void addTickerToDatabase(final long tickerSequence, final TickerDTO tickerDTO) {
        BacktestingTicker ticker = backtestingTickerMapper.mapToBacktestingTicker(tickerDTO);
        // Specific fields in BacktestingTicker.
        BacktestingTickerId id = new BacktestingTickerId();
        id.setTestSessionId(testSessionId);
        id.setResponseSequenceId(tickerSequence);
        id.setCurrencyPair(tickerDTO.getCurrencyPair().toString());
        ticker.setId(id);
        // Save in database.
        backtestingTickerRepository.save(ticker);
        // Update the size of the currency pair flux.
        fluxSize.put(tickerDTO.getCurrencyPair(), tickerSequence);
    }

    /**
     * Return true if a specific flux is done.
     *
     * @param currencyPair currency pair
     * @return true if nothing left
     */
    public boolean isFluxDone(final CurrencyPairDTO currencyPair) {
        final Long size = fluxSize.get(currencyPair);
        if (size == null) {
            return true;
        } else {
            // 7 calls <= 6 tickers => Flux done
            return sequence.get() <= size;
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
