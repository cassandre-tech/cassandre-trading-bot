package tech.cassandre.trading.bot.util;

/**
 * HTTP Status.
 */
public final class HttpStatus {

    /**
     * Private constructor.
     */
    private HttpStatus() {
    }

    /**
     * 200.
     */
    public static final int STATUS_OK = 200;

    /**
     * Everything worked as expected.
     */
    public static final String STATUS_OK_MESSAGE = "Everything worked as expected";

    /**
     * Created.
     */
    public static final int STATUS_CREATED = 201;

    /**
     * Created message.
     */
    public static final String STATUS_CREATED_MESSAGE = "Created";

    /**
     * 400.
     */
    public static final int STATUS_BAD_REQUEST = 400;

    /**
     * The request was unacceptable, often due to missing a required parameter.
     */
    public static final String STATUS_BAD_REQUEST_MESSAGE = "The request was unacceptable, often due to missing a required parameter";

    /**
     * 401.
     */
    public static final int STATUS_UNAUTHORIZED = 401;

    /**
     * No valid authorization was provided.
     */
    public static final String STATUS_UNAUTHORIZED_MESSAGE = "No valid authorization was provided";

    /**
     * 102.
     */
    public static final int STATUS_REQUEST_FAILED = 402;

    /**
     * The parameters were valid but the request failed.
     */
    public static final String STATUS_REQUEST_FAILED_MESSAGE = "The parameters were valid but the request failed";

    /**
     * 404.
     */
    public static final int STATUS_NOT_FOUND = 404;

    /**
     * The requested resource doesn't exist.
     */
    public static final String STATUS_NOT_FOUND_MESSAGE = "The requested resource doesn't exist";

    /**
     * Something went wrong on the server.
     */
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    /**
     * Something went wrong on the server.
     */
    public static final String STATUS_INTERNAL_SERVER_ERROR_MESSAGE = "Something went wrong on the server";

}
