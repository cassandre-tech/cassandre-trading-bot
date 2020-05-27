#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.trading.rules.OverIndicatorRule;
import org.ta4j.core.trading.rules.UnderIndicatorRule;

import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.strategy.BasicTa4jCassandreStrategy;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.BTC;
import static tech.cassandre.trading.bot.util.dto.CurrencyDTO.USDT;

/**
 * Simple strategy.
 * Please, create your own Kucoin sandbox account and do not make orders with this account.
 * How to do it : https://trading-bot.cassandre.tech/how_to_create_an_exchange_sandbox_for_kucoin.html
 */
@CassandreStrategy(name = "Simple ta4j strategy")
public final class SimpleTa4jStrategy extends BasicTa4jCassandreStrategy {

	@Override
	public CurrencyPairDTO getRequestedCurrencyPair() {
		return new CurrencyPairDTO(BTC, USDT);
	}

	@Override
	public int getMaximumBarCount() {
		return 8;
	}

	@Override
	public Strategy getStrategy() {
		ClosePriceIndicator closePrice = new ClosePriceIndicator(getSeries());
		SMAIndicator sma = new SMAIndicator(closePrice, 3);
		return new BaseStrategy(new UnderIndicatorRule(sma, closePrice), new OverIndicatorRule(sma, closePrice));
	}

	@Override
	public void shouldEnter() {
		System.out.println("Enter signal at " + getSeries().getLastBar().getClosePrice());
	}

	@Override
	public void shouldExit() {
		System.out.println("Exit signal at " + getSeries().getLastBar().getClosePrice());
	}

}
