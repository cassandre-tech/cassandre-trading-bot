package tech.cassandre.trading.bot.web.api;

import org.springframework.web.bind.annotation.RestController;

/**
 * Ping controller.
 */
@RestController
public class SystemAPIController implements SystemAPI {

    /** Ping response. */
    public static final String PING_RESPONSE = "pong";

    /** API Version. */
    public static final String API_VERSION = "1.0.0";

    @Override
    public final String ping() {
        return PING_RESPONSE;
    }

    @Override
    public final String version() {
        return API_VERSION;
    }

}
