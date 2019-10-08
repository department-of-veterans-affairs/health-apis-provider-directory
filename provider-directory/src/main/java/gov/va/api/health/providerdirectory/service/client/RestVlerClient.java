package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.providerdirectory.service.AddressResponse;
import gov.va.api.health.providerdirectory.service.VlerResponse;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.concurrent.Callable;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/** REST implementation of PPMS client. */
@Component
public class RestVlerClient implements VlerClient {
  private final String baseUrl;

  private final String publicKey;

  private final String privateKey;

  private final RestTemplate restTemplate;

  /** Autowired constructor. */
  public RestVlerClient(
      @Value("${vler.url}") String baseUrl,
      @Value("${vler.key.public}") String publicKey,
      @Value("${vler.key.private}") String privateKey,
      @Autowired RestTemplate restTemplate) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    this.publicKey = publicKey;
    this.privateKey = privateKey;
    this.restTemplate = restTemplate;
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
      throw new ProviderDirectoryException(message, e);
    }
    if (response == null) {
      throw new ProviderDirectoryException(message + ", no VLER response");
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

  /**
   * Calls the VLER Direct API by email address search. This will always return an unfiltered body.
   */
  @Override
  public AddressResponse endpointByAddress(String address) {
    return handleVlerExceptions(
        address,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "/direct/addresses").build().toUriString();
          String dateString =
              DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss z").format(ZonedDateTime.now());
          HttpHeaders headers = new HttpHeaders();
          headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
          headers.setContentType(MediaType.APPLICATION_JSON);
          headers.add("Authorization", authHeader(baseUrl, url, dateString));
          headers.add("Date", dateString);
          ResponseEntity<AddressResponse> entity =
              restTemplate.exchange(
                  url, HttpMethod.GET, new HttpEntity<>(headers), AddressResponse.class);
          return entity.getBody();
        });
  }
}
