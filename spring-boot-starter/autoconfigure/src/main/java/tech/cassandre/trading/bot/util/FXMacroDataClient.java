package tech.cassandre.trading.bot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class FXMacroDataClient {

  private final String apiKey;
  private final String baseUrl;

  public FXMacroDataClient(final String apiKey) {
    this(apiKey, "https://api.fxmacrodata.com/v1");
  }

  public FXMacroDataClient(final String apiKey, final String baseUrl) {
    this.apiKey = apiKey == null ? "" : apiKey;
    this.baseUrl = trimSlash(baseUrl);
  }

  public String dataCatalogue(final String currency) throws IOException { return get("/data_catalogue/" + norm(currency)); }
  public String announcements(final String currency, final String indicator) throws IOException { return get("/announcements/" + norm(currency) + "/" + indicator); }
  public String calendar(final String currency) throws IOException { return get("/calendar/" + norm(currency)); }
  public String predictions(final String currency, final String indicator) throws IOException { return get("/predictions/" + norm(currency) + "/" + indicator); }
  public String forex(final String base, final String quote) throws IOException { return get("/forex/" + norm(base) + "/" + norm(quote)); }
  public String cot(final String currency) throws IOException { return get("/cot/" + norm(currency)); }
  public String commoditiesLatest() throws IOException { return get("/commodities/latest"); }
  public String commodity(final String indicator) throws IOException { return get("/commodities/" + indicator); }
  public String curves(final String currency) throws IOException { return get("/curves/" + norm(currency)); }
  public String curveProxies(final String currency) throws IOException { return get("/curve_proxies/" + norm(currency)); }
  public String forwardCurves(final String currency) throws IOException { return get("/forward_curves/" + norm(currency)); }
  public String marketSessions() throws IOException { return get("/market_sessions"); }
  public String riskSentiment() throws IOException { return get("/risk_sentiment"); }
  public String news(final String currency) throws IOException { return get("/news/" + norm(currency)); }
  public String pressReleases(final String currency) throws IOException { return get("/press-releases/" + norm(currency)); }
  public String centralBankers(final String currency) throws IOException { return get("/central_bankers/" + norm(currency)); }

  private String get(final String path) throws IOException {
    final HttpURLConnection connection = (HttpURLConnection) URI.create(buildUrl(path)).toURL().openConnection();
    connection.setRequestMethod("GET");
    connection.setConnectTimeout(10000);
    connection.setReadTimeout(20000);
    final int status = connection.getResponseCode();
    if (status < 200 || status >= 300) {
      throw new IOException("FXMacroData returned HTTP " + status);
    }
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
      final StringBuilder body = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) body.append(line);
      return body.toString();
    }
  }

  String buildUrl(final String path) throws IOException {
    if (apiKey.isEmpty()) return baseUrl + path;
    return baseUrl + path + "?api_key=" + URLEncoder.encode(apiKey, StandardCharsets.UTF_8.name());
  }

  private static String norm(final String value) {
    return value.trim().toLowerCase(Locale.ROOT);
  }

  private static String trimSlash(final String value) {
    return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
  }
}
