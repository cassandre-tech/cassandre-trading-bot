package tech.cassandre.trading.bot.test.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import tech.cassandre.trading.bot.dto.market.TickerDTO;
import tech.cassandre.trading.bot.util.dto.CurrencyPairDTO;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Base for tests.
 */
public class BaseTest {

	/** How much we should wait for tests to last. */
	protected static final long MAXIMUM_RESPONSE_TIME_IN_SECONDS = 60;

	/** Application context. */
	@SuppressWarnings("SpringJavaAutowiredMembersInspection")
	@Autowired
	private ApplicationContext context;

	/**
	 * Getter of context.
	 *
	 * @return context
	 */
	public final ApplicationContext getContext() {
		return context;
	}

	/**
	 * Util method to return a fake ticker.
	 *
	 * @param cp  currency pair
	 * @param bid bid price
	 * @return ticket
	 */
	protected static Optional<TickerDTO> getFakeTicker(final CurrencyPairDTO cp, final BigDecimal bid) {
		return Optional.of(TickerDTO.builder()
				.currencyPair(cp)
				.bid(bid)
				.create());
	}

}
