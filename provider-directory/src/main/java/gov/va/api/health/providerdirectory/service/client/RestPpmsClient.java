package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import java.util.Collections;
import java.util.concurrent.Callable;
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
public class RestPpmsClient implements PpmsClient {
  private final RestTemplate restTemplate;

  private final String baseUrl;

  public RestPpmsClient(
      @Value("${ppms.url}") String baseUrl, @Autowired RestTemplate restTemplate) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    this.restTemplate = restTemplate;
  }

  @SneakyThrows
  private static <T> T handlePpmsExceptions(String message, Callable<T> callable) {
    try {
      return callable.call();
    } catch (HttpClientErrorException.NotFound e) {
      throw new NotFound(message);
    } catch (HttpClientErrorException.BadRequest e) {
      throw new BadRequest(message);
    } catch (HttpStatusCodeException e) {
      throw new SearchFailed(message);
    }
  }

  private static HttpHeaders headers() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList((MediaType.APPLICATION_JSON)));
    return headers;
  }

  @Override
  public ProviderContactsResponse providerContactsForId(String id) {
    return handlePpmsExceptions(
        id,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "Providers(" + id + ")/ProviderContacts")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<ProviderContactsResponse> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, ProviderContactsResponse.class);
          return entity.getBody();
        });
  }

  @Override
  public ProviderSpecialtiesResponse providerSpecialtySearch(String id) {
    return handlePpmsExceptions(
        id,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(
                      baseUrl + "Providers(" + id + ")/ProviderSpecialties")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<ProviderSpecialtiesResponse> entity =
              restTemplate.exchange(
                  url, HttpMethod.GET, requestEntity, ProviderSpecialtiesResponse.class);
          return entity.getBody();
        });
  }

  @Override
  public ProviderResponse providersForId(String id) {
    return handlePpmsExceptions(
        id,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "Providers(" + id + ")")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<ProviderResponse> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, ProviderResponse.class);
          return entity.getBody();
        });
  }

  @Override
  public ProviderResponse providersForName(String name) {
    return handlePpmsExceptions(
        name,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "GetProviderByName?name=" + name)
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<ProviderResponse> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, ProviderResponse.class);
          return entity.getBody();
        });
  }
}
