package tech.cassandre.trading.bot.api.graphql.data;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.api.graphql.util.base.BaseDataFetcher;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.repository.TradeRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Trade data fetcher.
 */
@DgsComponent
@RequiredArgsConstructor
public class TradeDataFetcher extends BaseDataFetcher {

    /** Trade repository. */
    private final TradeRepository tradeRepository;

    /**
     * Returns all the trades.
     *
     * @return all trades
     */
    @DgsQuery
    public final List<TradeDTO> trades() {
        return tradeRepository.findAll()
                .stream()
                .map(TRADE_MAPPER::mapToTradeDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns the trade with the corresponding id value.
     *
     * @param id id
     * @return trade
     */
    @DgsQuery
    public final TradeDTO trade(@InputArgument final long id) {
        return tradeRepository.findById(id)
                .map(TRADE_MAPPER::mapToTradeDTO)
                .orElse(null);
    }

    /**
     * Returns the trade with the corresponding tradeId value.
     *
     * @param tradeId trade id
     * @return trade
     */
    @DgsQuery
    public final TradeDTO tradeByTradeId(@InputArgument final String tradeId) {
        return tradeRepository.findByTradeId(tradeId)
                .map(TRADE_MAPPER::mapToTradeDTO)
                .orElse(null);
    }

}
