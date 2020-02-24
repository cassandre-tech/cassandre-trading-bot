package tech.cassandre.trading.bot.test.util.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.dto.trade.OrderDTO;
import tech.cassandre.trading.bot.dto.user.AccountDTO;
import tech.cassandre.trading.bot.strategy.CassandreStrategy;
import tech.cassandre.trading.bot.strategy.Strategy;
import tech.cassandre.trading.bot.util.dto.CurrencyDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Testable strategy (used for tests).
 */
@SuppressWarnings("unused")
@Strategy(name = "Testable strategy")
@ConditionalOnProperty(
		value = "testableStrategy.enabled",
		havingValue = "true")
public class TestableStrategy extends CassandreStrategy {

	/** Method duration. */
	private static final long METHOD_DURATION = 1000;

	/** Logger. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/** Accounts update received. */
	private final List<AccountDTO> accountsUpdateReceived = new LinkedList<>();

	/** Tickers update received. */
	private final List<TickerDTO> tickersUpdateReceived = new LinkedList<>();

	/** Orders update received. */
	private final List<OrderDTO> ordersUpdateReceived = new LinkedList<>();

	@Override
	public final Set<CurrencyPairDTO> getRequestedCurrencyPairs() {
		Set<CurrencyPairDTO> requestedTickers = new LinkedHashSet<>();
		requestedTickers.add(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.BTC));
		requestedTickers.add(new CurrencyPairDTO(CurrencyDTO.ETH, CurrencyDTO.USDT));
		return requestedTickers;
	}

	@Override
	public final void onAccountUpdate(final AccountDTO account) {
		accountsUpdateReceived.add(account);
		logger.info("> TestableStrategy-onAccountUpdate " + getCount(accountsUpdateReceived) + " : " + account);
		try {
			Thread.sleep(METHOD_DURATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public final void onTickerUpdate(final TickerDTO ticker) {
		tickersUpdateReceived.add(ticker);
		logger.info("> TestableStrategy-onTickerUpdate " + getCount(tickersUpdateReceived) + " : " + ticker);
		try {
			Thread.sleep(METHOD_DURATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public final void onOrderUpdate(final OrderDTO order) {
		ordersUpdateReceived.add(order);
		logger.info("> TestableStrategy-onOrderUpdate " + getCount(ordersUpdateReceived) + " : " + order);
		try {
			Thread.sleep(METHOD_DURATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return list count with format.
	 *
	 * @param list list to count
	 * @return int value with format
	 */
	private String getCount(final List list) {
		return String.format("%03d", list.size());
	}

	/**
	 * Getter lastAccountsReceived.
	 *
	 * @return last accounts received.
	 */
	public final List<AccountDTO> getAccountsUpdatesReceived() {
		return accountsUpdateReceived;
	}

	/**
	 * Getter lastTickersReceived.
	 *
	 * @return lastTickersReceived
	 */
	public final List<TickerDTO> getTickersUpdateReceived() {
		return tickersUpdateReceived;
	}

	/**
	 * Getter lastOrdersReceived.
	 *
	 * @return lastOrderReceived
	 */
	public final List<OrderDTO> getOrdersUpdateReceived() {
		return ordersUpdateReceived;
	}

}
