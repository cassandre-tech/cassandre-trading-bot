package tech.cassandre.trading.bot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;

/**
 * Minimal FXMacroData HTTP client.
 */
public final class FXMacroDataClient {

  /** Default FXMacroData API base URL. */
  private static final String DEFAULT_BASE_URL = "https://api.fxmacrodata.com/v1";

  /** Connection timeout in milliseconds. */
  private static final int CONNECT_TIMEOUT_IN_MILLISECONDS = 10_000;

  /** Read timeout in milliseconds. */
  private static final int READ_TIMEOUT_IN_MILLISECONDS = 20_000;

  /** API key. */
  private final String apiKey;

  /** API base URL. */
  private final String baseUrl;

  /**
   * Constructs the client with the default API URL.
   *
   * @param valueApiKey FXMacroData API key
   */
  public FXMacroDataClient(final String valueApiKey) {
    this(valueApiKey, DEFAULT_BASE_URL);
  }

  /**
   * Constructs the client with a custom API URL.
   *
   * @param valueApiKey FXMacroData API key
   * @param valueBaseUrl FXMacroData API base URL
   */
  public FXMacroDataClient(final String valueApiKey, final String valueBaseUrl) {
    this.apiKey = blankToEmpty(valueApiKey);
    this.baseUrl = trimSlash(valueBaseUrl);
  }

  /**
   * Gets the FXMacroData catalogue for a currency.
   *
   * @param currency ISO 4217 currency code
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String dataCatalogue(final String currency) throws IOException {
    return get("/data_catalogue/" + norm(currency));
  }

  /**
   * Gets announcement history for a currency indicator.
   *
   * @param currency ISO 4217 currency code
   * @param indicator FXMacroData indicator slug
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String announcements(final String currency, final String indicator) throws IOException {
    return get("/announcements/" + norm(currency) + "/" + pathSegment(indicator));
  }

  /**
   * Gets the release calendar for a currency.
   *
   * @param currency ISO 4217 currency code
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String calendar(final String currency) throws IOException {
    return get("/calendar/" + norm(currency));
  }

  /**
   * Gets consensus predictions for a currency indicator.
   *
   * @param currency ISO 4217 currency code
   * @param indicator FXMacroData indicator slug
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String predictions(final String currency, final String indicator) throws IOException {
    return get("/predictions/" + norm(currency) + "/" + pathSegment(indicator));
  }

  /**
   * Gets an FX spot-rate series.
   *
   * @param base base currency
   * @param quote quote currency
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String forex(final String base, final String quote) throws IOException {
    return get("/forex/" + norm(base) + "/" + norm(quote));
  }

  /**
   * Gets latest COT positioning for a currency.
   *
   * @param currency ISO 4217 currency code
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String cot(final String currency) throws IOException {
    return get("/cot/" + norm(currency));
  }

  /**
   * Gets latest supported commodity prices.
   *
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String commoditiesLatest() throws IOException {
    return get("/commodities/latest");
  }

  /**
   * Gets a commodity series.
   *
   * @param indicator FXMacroData commodity indicator slug
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String commodity(final String indicator) throws IOException {
    return get("/commodities/" + pathSegment(indicator));
  }

  /**
   * Gets government bond curve data for a currency.
   *
   * @param currency ISO 4217 currency code
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String curves(final String currency) throws IOException {
    return get("/curves/" + norm(currency));
  }

  /**
   * Gets curve proxy data for a currency.
   *
   * @param currency ISO 4217 currency code
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String curveProxies(final String currency) throws IOException {
    return get("/curve_proxies/" + norm(currency));
  }

  /**
   * Gets forward curve data for a currency.
   *
   * @param currency ISO 4217 currency code
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String forwardCurves(final String currency) throws IOException {
    return get("/forward_curves/" + norm(currency));
  }

  /**
   * Gets current FX market sessions.
   *
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String marketSessions() throws IOException {
    return get("/market_sessions");
  }

  /**
   * Gets current risk-sentiment data.
   *
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String riskSentiment() throws IOException {
    return get("/risk_sentiment");
  }

  /**
   * Gets macro news for a currency.
   *
   * @param currency ISO 4217 currency code
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String news(final String currency) throws IOException {
    return get("/news/" + norm(currency));
  }

  /**
   * Gets central-bank press releases for a currency.
   *
   * @param currency ISO 4217 currency code
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String pressReleases(final String currency) throws IOException {
    return get("/press-releases/" + norm(currency));
  }

  /**
   * Gets central banker reference data for a currency.
   *
   * @param currency ISO 4217 currency code
   * @return raw JSON response
   * @throws IOException if the HTTP request fails
   */
  public String centralBankers(final String currency) throws IOException {
    return get("/central_bankers/" + norm(currency));
  }

  private String get(final String path) throws IOException {
    final HttpURLConnection connection = openConnection(path);
    try {
      final int status = connection.getResponseCode();
      if (status < HttpURLConnection.HTTP_OK || status >= HttpURLConnection.HTTP_MULT_CHOICE) {
        throw new IOException("FXMacroData returned HTTP " + status);
      }
      return readBody(connection);
    } finally {
      connection.disconnect();
    }
  }

  private HttpURLConnection openConnection(final String path) throws IOException {
    final HttpURLConnection connection = (HttpURLConnection) URI.create(buildUrl(path)).toURL().openConnection();
    connection.setRequestMethod("GET");
    connection.setConnectTimeout(CONNECT_TIMEOUT_IN_MILLISECONDS);
    connection.setReadTimeout(READ_TIMEOUT_IN_MILLISECONDS);
    return connection;
  }

  private String readBody(final HttpURLConnection connection) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
      final StringBuilder body = new StringBuilder();
      String line = reader.readLine();
      while (line != null) {
        body.append(line);
        line = reader.readLine();
      }
      return body.toString();
    }
  }

  String buildUrl(final String path) {
    if (apiKey.isEmpty()) {
      return baseUrl + path;
    }
    return baseUrl + path + "?api_key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
  }

  private static String norm(final String value) {
    return pathSegment(value).toLowerCase(Locale.ROOT);
  }

  private static String pathSegment(final String value) {
    return URLEncoder.encode(Objects.requireNonNull(value).trim(), StandardCharsets.UTF_8).replace("+", "%20");
  }

  private static String trimSlash(final String value) {
    final String trimmedValue = Objects.requireNonNull(value).trim();
    if (trimmedValue.endsWith("/")) {
      return trimmedValue.substring(0, trimmedValue.length() - 1);
    }
    return trimmedValue;
  }

  private static String blankToEmpty(final String value) {
    if (value == null) {
      return "";
    }
    return value.trim();
  }
}
