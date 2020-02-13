package tech.cassandre.trading.bot.test.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Base for tests.
 */
public class BaseTest {

	/** Application context. */
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

}
