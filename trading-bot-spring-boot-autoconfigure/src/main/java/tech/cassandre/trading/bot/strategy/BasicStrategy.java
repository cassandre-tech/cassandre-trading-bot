package tech.cassandre.trading.bot.strategy;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.service.TradeService;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.Set;

/**
 * Basic strategy - Cassandre bot will run the first CassandreStrategy implementation found.
 */
@SuppressWarnings("unused")
public abstract class BasicStrategy {

    /** Trade service. */
    private TradeService tradeService;

    /**
     * Getter tradeService.
     *
     * @return tradeService
     */
    public final TradeService getTradeService() {
        return tradeService;
    }

    /**
     * Setter tradeService.
     *
     * @param newTradeService the tradeService to set
     */
    public final void setTradeService(final TradeService newTradeService) {
        tradeService = newTradeService;
    }

    /**
     * Implements this method to tell the bot which currency pairs your strategy will receive.
     *
     * @return the list of currency pairs tickers your want to receive
     */
    public abstract Set<CurrencyPairDTO> getRequestedCurrencyPairs();

    /**
     * Method triggered at every account update.
     *
     * @param account account
     */
    public abstract void onAccountUpdate(AccountDTO account);

    /**
     * Method triggered at every ticker update.
     *
     * @param ticker ticker
     */
    public abstract void onTickerUpdate(TickerDTO ticker);

    /**
     * Method triggered on every order update.
     *
     * @param order order
     */
    public abstract void onOrderUpdate(OrderDTO order);

}
