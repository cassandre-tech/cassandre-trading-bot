package tech.cassandre.trading.bot.api.graphql.data;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.RequiredArgsConstructor;
import tech.cassandre.trading.bot.api.graphql.util.base.BaseDataFetcher;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.repository.TradeRepository;

import java.util.List;

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
                .toList();
    }

    /**
     * Returns the trade with the corresponding uid value.
     *
     * @param uid trade uid
     * @return trade
     */
    @DgsQuery
    public final TradeDTO trade(@InputArgument final long uid) {
        return tradeRepository.findById(uid)
                .map(TRADE_MAPPER::mapToTradeDTO)
                .orElse(null);
    }

    /**
     * Returns the trade with the corresponding trade id value.
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
