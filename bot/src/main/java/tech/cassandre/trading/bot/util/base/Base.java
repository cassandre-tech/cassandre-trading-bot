package tech.cassandre.trading.bot.util.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base.
 */
@SuppressWarnings("unused")
public class Base {

	/** Logger. */
	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/**
	 * Getter logger.
	 *
	 * @return logger
	 */
	protected final Logger getLogger() {
		return logger;
	}

}
