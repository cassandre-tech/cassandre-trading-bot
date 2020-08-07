#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.position.PositionDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.trade.TradeDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.strategy.BasicCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple strategy.
 * Please, create your own Kucoin sandbox account and do not make orders with this account.
 * How to do it : https://trading-bot.cassandre.tech/how-tos/how-to-create-a-kucoin-sandbox-account
 */
@CassandreStrategy(name = "Simple strategy")
public final class SimpleStrategy extends BasicCassandreStrategy {

	@Override
	public Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
		// We only ask about ETC/BTC (Base currency : ETH / Quote currency : BTC).
		return Set.of(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC));
	}

	@Override
	public void onAccountUpdate(final AccountDTO account) {
		// Here, we will receive an AccountDTO each time there is a change on your account.
		System.out.println("Received information about an account : " + account);
	}

	@Override
	public void onTickerUpdate(final TickerDTO ticker) {
		// Here we will receive a TickerDTO each time a new one is available.
		System.out.println("Received information about a ticker : " + ticker);
	}

	@Override
	public void onOrderUpdate(final OrderDTO order) {
		// Here, we will receive an OrderDTO each time an order data has changed in the exchange.
		System.out.println("Received information about an order : " + order);
	}

	@Override
	public void onTradeUpdate(final TradeDTO trade) {
		// Here, we will receive a TradeDTO each time a trade data has changed in the exchange.
		System.out.println("Received information about a trade : " + trade);
	}

	@Override
	public void onPositionUpdate(final PositionDTO position) {
		// Here, we will receive an PositionDTO each a position has changed.
		System.out.println("Received information about a position : " + position);
	}

}
