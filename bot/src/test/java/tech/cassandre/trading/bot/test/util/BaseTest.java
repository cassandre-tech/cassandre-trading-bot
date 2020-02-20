package tech.cassandre.trading.bot.test.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

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

}
