#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.dto.util.CurrencyPairDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.dto.util.CurrencyDTO.USDT;

/**
 * Simple strategy.
 * Please, create your own Kucoin sandbox account and do not make orders with this account.
 * How to do it : https://trading-bot.cassandre.tech/ressources/how-tos/how-to-create-a-kucoin-account.html
 */
@CassandreStrategy(strategyName = "Simple strategy")
public final class SimpleStrategy extends BasicCassandreStrategy {

	@Override
	public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
		// We only ask for BTC/USDT tickers (Base currency : BTC / Quote currency : USDT).
		return Set.of(new CurrencyPairDTO(BTC, USDT));
	}

	@Override
	public Optional<AccountDTO> getTradeAccount(Set<AccountDTO> accounts) {
		// From all the accounts we have on the exchange, we must return the one we use for trading.
		if (accounts.size() == 1) {
			// If there is only one on the exchange, we choose this one.
			return accounts.stream().findFirst();
		} else {
			// If there are several accounts on the exchange, we choose the one whose name is "trade".
			return accounts.stream()
					.filter(a -> "trade".equalsIgnoreCase(a.getName()))
					.findFirst();
		}
	}

	@Override
	public void onAccountsUpdates(final Map<String, AccountDTO> accounts) {
		// Here, we will receive an AccountDTO each time there is a change on your account.
		accounts.values().forEach(account -> System.out.println("Received information about an account: " + account));
	}

	@Override
	public void onTickersUpdates(final Map<CurrencyPairDTO, TickerDTO> tickers) {
		// Here we will receive all tickers we required from the exchange.
		tickers.values().forEach(ticker -> System.out.println("Received information about a ticker: " + ticker));
	}

	@Override
	public void onOrdersUpdates(final Map<String, OrderDTO> orders) {
		// Here, we will receive an OrderDTO each time order data has changed on the exchange.
		orders.values().forEach(order -> System.out.println("Received information about an order: " + order));
	}

	@Override
	public void onTradesUpdates(final Map<String, TradeDTO> trades) {
		// Here, we will receive a TradeDTO each time trade data has changed on the exchange.
		trades.values().forEach(trade -> System.out.println("Received information about a trade: " + trade));
	}

	@Override
	public void onPositionsUpdates(final Map<Long, PositionDTO> positions) {
		// Here, we will receive a PositionDTO each time a position has changed.
		positions.values().forEach(position -> System.out.println("Received information about a position: " + position));
	}

	@Override
	public void onPositionsStatusUpdates(final Map<Long, PositionDTO> positions) {
		// Here, we will receive a PositionDTO each time a position status has changed.
		positions.values().forEach(position -> System.out.println("Received information about a position status: " + position));
	}

}
