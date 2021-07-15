package tech.cassandre.trading.bot.web.api.util;

import org.springframework.web.bind.annotation.RestController;

/**
 * Ping controller.
 */
@RestController
public class PingAPIController implements PingAPI {

    /** Ping response. */
    public static final String PING_RESPONSE = "pong";

    @Override
    public final String ping() {
        return PING_RESPONSE;
    }

}
