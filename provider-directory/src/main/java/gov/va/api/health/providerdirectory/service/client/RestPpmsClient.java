package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.providerdirectory.service.ProviderContacts;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import java.util.Collections;
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

/** A rest implementation of the Mr. Anderson client. */
@Component
public class RestPpmsClient implements PpmsClient {
  private final RestTemplate restTemplate;

  private final String baseUrl;

  public RestPpmsClient(
      @Value("${ppms.url}") String baseUrl, @Autowired RestTemplate restTemplate) {
    this.baseUrl = baseUrl;
    this.restTemplate = restTemplate;
  }

  private static HttpHeaders headers() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList((MediaType.APPLICATION_JSON)));
    return headers;
  }

  @Override
  public ProviderContacts providerContactsSearch(String id) {
    try {
      String url =
          UriComponentsBuilder.fromHttpUrl(baseUrl + "Providers(" + id + ")/ProviderContacts")
              .build()
              .toUriString();
      HttpEntity<?> requestEntity = new HttpEntity<>(headers());
      ResponseEntity<ProviderContacts> entity =
          restTemplate.exchange(url, HttpMethod.GET, requestEntity, ProviderContacts.class);
      return entity.getBody();
    } catch (HttpClientErrorException.NotFound e) {
      throw new NotFound(id);
    } catch (HttpClientErrorException.BadRequest e) {
      throw new BadRequest(id);
    } catch (HttpStatusCodeException e) {
      throw new SearchFailed(id);
    }
  }

  @Override
  public ProviderResponse providerResponseSearch(String id, boolean identifier) {
    try {
      String url;
      if (identifier == true) {
        url =
            UriComponentsBuilder.fromHttpUrl(baseUrl + "Providers(" + id + ")")
                .build()
                .toUriString();
      } else {
        url =
            UriComponentsBuilder.fromHttpUrl(baseUrl + "GetProviderByName?name=" + id)
                .build()
                .toUriString();
      }
      HttpEntity<?> requestEntity = new HttpEntity<>(headers());
      ResponseEntity<ProviderResponse> entity =
          restTemplate.exchange(url, HttpMethod.GET, requestEntity, ProviderResponse.class);
      return entity.getBody();
    } catch (HttpClientErrorException.NotFound e) {
      throw new NotFound(id);
    } catch (HttpClientErrorException.BadRequest e) {
      throw new BadRequest(id);
    } catch (HttpStatusCodeException e) {
      throw new SearchFailed(id);
    }
  }
}
