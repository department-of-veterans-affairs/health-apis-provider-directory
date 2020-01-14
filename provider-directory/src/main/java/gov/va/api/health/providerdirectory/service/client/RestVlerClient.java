package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.service.AddressResponse;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import lombok.SneakyThrows;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.UriComponentsBuilder;

/** REST implementation of VLER client. */
@Component
public class RestVlerClient implements VlerClient {
  private final String baseUrl;

  private final String publicKey;

  private final String privateKey;

  private final String keyStorePath;

  private final String keyStorePassword;

  /** Autowired constructor. */
  public RestVlerClient(
      @Value("${vler.url}") String baseUrl,
      @Value("${vler.key.public}") String publicKey,
      @Value("${vler.key.private}") String privateKey,
      @Value("${ssl.key-store}") String keyStorePath,
      @Value("${ssl.key-store-password}") String keyStorePassword) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    this.publicKey = publicKey;
    this.privateKey = privateKey;
    this.keyStorePath = keyStorePath;
    this.keyStorePassword = keyStorePassword;
  }

  private static String fileOrClasspath(String path) {
    if (StringUtils.startsWith(path, "file:") || StringUtils.startsWith(path, "classpath:")) {
      return path;
    }
    throw new IllegalArgumentException("Expected file or classpath resources. Got " + path);
  }

  @SneakyThrows
  @EventListener(ApplicationStartedEvent.class)
  public void initSsl() {
    try (InputStream keystoreInputStream =
        ResourceUtils.getURL(fileOrClasspath(keyStorePath)).openStream()) {
      KeyStore ts = KeyStore.getInstance("JKS");
      ts.load(keystoreInputStream, keyStorePassword.toCharArray());
      TrustManagerFactory trustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(ts);
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }
  }

  @SneakyThrows
  private String authHeader(String url, String dateString) {
    String endpoint;
    try {
      endpoint = "/" + url.substring(url.indexOf(baseUrl) + baseUrl.length());
    } catch (Exception e) {
      throw new Exceptions.BadRequest(
          "Base URL [" + baseUrl + "] not found within url [" + url + "]", e);
    }
    String reqStr = "GET\n" + dateString + "\napplication/json\n" + endpoint;
    Mac encoding = Mac.getInstance("HmacSHA256");
    encoding.init(new SecretKeySpec(privateKey.getBytes(Charset.forName("UTF-8")), "HmacSHA256"));
    byte[] sha = encoding.doFinal(reqStr.getBytes(Charset.forName("UTF-8")));
    String encSha = Base64.getEncoder().encodeToString(sha);
    return "DAAS " + publicKey + ":" + encSha;
  }

  /**
   * Calls the VLER Direct API by email address search. This will always return an unfiltered body.
   */
  @Override
  public AddressResponse endpointByAddress(String address) {
    String url =
        UriComponentsBuilder.fromHttpUrl(baseUrl + "/direct/addresses").build().toUriString();
    String dateString =
        DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss z").format(ZonedDateTime.now());
    HttpURLConnection conn = null;
    try {
      conn = (HttpURLConnection) new URL(url).openConnection();
      conn.setRequestProperty("Authorization", authHeader(url, dateString));
      conn.setRequestProperty("Date", dateString);
      conn.setRequestProperty("Accept", "application/json");
      conn.setRequestProperty("content-type", "application/json");
      conn.setRequestMethod("GET");
      String result = StreamUtils.copyToString(conn.getInputStream(), StandardCharsets.UTF_8);
      return JacksonConfig.createMapper().readValue(result, AddressResponse.class);
    } catch (Exception e) {
      throw new Exceptions.VlerException(address, e);
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }
}
