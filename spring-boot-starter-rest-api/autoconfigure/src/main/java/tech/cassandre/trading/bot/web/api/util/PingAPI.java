package tech.cassandre.trading.bot.web.api.util;

import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static tech.cassandre.trading.bot.util.HttpStatus.STATUS_OK;
import static tech.cassandre.trading.bot.util.HttpStatus.STATUS_OK_MESSAGE;

/**
 * Ping API.
 */
public interface PingAPI {

    /**
     * Ping.
     *
     * @return pong
     */
    @RequestMapping(value = "/ping", method = GET)
    @ApiOperation(value = "Returns pong (keep alive method)", response = String.class)
    @ApiImplicitParams({})
    @ApiResponses(value = {
            @ApiResponse(code = STATUS_OK, message = STATUS_OK_MESSAGE)
    })
    String ping();

}
