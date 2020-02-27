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

	/** Testable strategy enabled parameter. */
	public static final String PARAMETER_TESTABLE_STRATEGY_ENABLED = "testableStrategy.enabled";

	/** Invalid strategy enabled parameter. */
	public static final String PARAMETER_INVALID_STRATEGY_ENABLED = "invalidStrategy.enabled";

	/** Testable strategy enabled parameter. */
	public static final String PARAMETER_TESTABLE_STRATEGY_DEFAULT_VALUE = "true";

	/** Invalid strategy enabled parameter. */
	public static final String PARAMETER_INVALID_STRATEGY_DEFAULT_VALUE = "false";

	/** Exchange name parameter. */
	public static final String PARAMETER_NAME_DEFAULT_VALUE = "kucoin";

	/** Sandbox parameter. */
	public static final String PARAMETER_SANDBOX_DEFAULT_VALUE = "true";

	/** Username parameter. */
	public static final String PARAMETER_USERNAME_DEFAULT_VALUE = "cassandre.crypto.bot@gmail.com";

	/** Passphrase parameter. */
	public static final String PARAMETER_PASSPHRASE_DEFAULT_VALUE = "cassandre";

	/** Key parameter. */
	public static final String PARAMETER_KEY_DEFAULT_VALUE = "5df8eea30092f40009cb3c6a";

	/** Secret parameter. */
	public static final String PARAMETER_SECRET_DEFAULT_VALUE = "5f6e91e0-796b-4947-b75e-eaa5c06b6bed";

	/** Rate for account parameter. */
	public static final String PARAMETER_RATE_ACCOUNT_DEFAULT_VALUE = "100";

	/** Rate for ticker parameter. */
	public static final String PARAMETER_RATE_TICKER_DEFAULT_VALUE = "101";

	/** Rate for order parameter. */
	public static final String PARAMETER_RATE_ORDER_DEFAULT_VALUE = "102";

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

	/**
	 * Get exception message from parameter exception.
	 *
	 * @param e exception
	 * @return message
	 */
	protected String getParametersExceptionMessage(Exception e) {
		return e.getCause().getCause().getCause().getMessage();
	}

}
