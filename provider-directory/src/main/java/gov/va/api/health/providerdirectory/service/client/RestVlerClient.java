package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.providerdirectory.service.AddressResponse;
import gov.va.api.health.providerdirectory.service.VlerResponse;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.Callable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/** REST implementation of PPMS client. */
@Slf4j
@Component
public class RestVlerClient implements VlerClient {

  private final String baseUrl;

  private final RestTemplate restTemplate;

  private final String publicKey;

  private final String privateKey;

  private final String vlerTruststorePassword;

  private final String vlerTruststore;

  /**
   * REST implementation of VLER client. Uses a locally-stored truststore to authenticate with VLER.
   */
  public RestVlerClient(
      @Value("${vler.url}") String baseUrl,
      @Value("${vler.key.public}") String publicKey,
      @Value("${vler.key.private}") String privateKey,
      @Value("${vler.truststore.password}") String vlerTruststorePassword,
      @Value("${vler.truststore.location}") String vlerTruststore) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    this.publicKey = publicKey;
    this.privateKey = privateKey;
    this.vlerTruststorePassword = vlerTruststorePassword;
    this.vlerTruststore = vlerTruststore;
    this.restTemplate = restTemplate();
  }

  @SneakyThrows
  private static <T extends VlerResponse> T handleVlerExceptions(
      String message, Callable<T> callable) {
    T response;
    try {
      response = callable.call();
    } catch (HttpClientErrorException.NotFound e) {
      throw new NotFound(message, e);
    } catch (HttpClientErrorException.BadRequest e) {
      throw new BadRequest(message, e);
    } catch (HttpStatusCodeException e) {
      throw new SearchFailed(message, e);
    } catch (Exception e) {
      throw new VlerException(message, e);
    }
    if (response == null) {
      throw new VlerException(message + ", no VLER response");
    }
    return response;
  }

  @SneakyThrows
  private String authHeader(String baseUrl, String url, String dateString) {
    String endpoint;
    try {
      endpoint = "/" + url.substring(url.indexOf(baseUrl) + baseUrl.length());
    } catch (Exception e) {
      throw new BadRequest("Base URL [" + baseUrl + "] not found within url [" + url + "]", e);
    }
    String reqStr = "GET\n" + dateString + "\napplication/json\n" + endpoint;
    Mac encoding = Mac.getInstance("HmacSHA256");
    encoding.init(new SecretKeySpec(privateKey.getBytes(Charset.forName("UTF-8")), "HmacSHA256"));
    byte[] sha = encoding.doFinal(reqStr.getBytes(Charset.forName("UTF-8")));
    String encSha = Base64.getEncoder().encodeToString(sha);
    return "DAAS " + publicKey + ":" + encSha;
  }

  /** Calls the VLER Direct API by email address search.
   * This will always return an unfiltered body. */
  @Override
  public AddressResponse endpointByAddress(String address) {
    return handleVlerExceptions(
        address,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "/direct/addresses")
                  .build()
                  .toUriString();
          String dateString =
              DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss z").format(ZonedDateTime.now());
          HttpHeaders headers = new HttpHeaders();
          headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
          headers.setContentType(MediaType.APPLICATION_JSON);
          headers.add("Authorization", authHeader(baseUrl, url, dateString));
          headers.add("Date", dateString);
          ResponseEntity<AddressResponse> entity =
              restTemplate()
                  .exchange(url, HttpMethod.GET, new HttpEntity<>(headers), AddressResponse.class);
          return entity.getBody();
        });
  }

  @SneakyThrows
  private RestTemplate restTemplate() throws RuntimeException{
    try (InputStream truststoreInputStream =
        getClass().getClassLoader().getResourceAsStream(FilenameUtils.getName(vlerTruststore))) {
      KeyStore ts = KeyStore.getInstance("JKS");
      ts.load(truststoreInputStream, vlerTruststorePassword.toCharArray());
      TrustManagerFactory trustManagerFactory =
          TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init(ts);
      SSLConnectionSocketFactory socketFactory =
          new SSLConnectionSocketFactory(
              new SSLContextBuilder()
                  .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                  .loadKeyMaterial(ts, vlerTruststorePassword.toCharArray())
                  .build(),
              new String[] {"TLSv1.1"},
              null,
              SSLConnectionSocketFactory.getDefaultHostnameVerifier());
      HttpClient httpsClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
      return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpsClient));
    }
  }

  public static class ClassLoaderException extends RuntimeException {
    ClassLoaderException(String message) {
      super(message);
    }
  }
}
