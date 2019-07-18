package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.providerdirectory.service.PpmsCareSites;
import gov.va.api.health.providerdirectory.service.PpmsProviderServices;
import gov.va.api.health.providerdirectory.service.PpmsProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.ProviderContacts;
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
  public PpmsCareSites careSitesByCity(String city) {
    return handlePpmsExceptions(
        city,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "GetCareSiteByCity?City=" + city)
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<PpmsCareSites> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, PpmsCareSites.class);
          return entity.getBody();
        });
  }

  @Override
  public PpmsProviderServices careSitesById(String id) {
    return handlePpmsExceptions(
        id,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "Providers(" + id + ")/ProviderServices")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<PpmsProviderServices> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, PpmsProviderServices.class);
          return entity.getBody();
        });
  }

  @Override
  public PpmsProviderServices careSitesByName(String name) {
    return handlePpmsExceptions(
        name,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(
                      baseUrl + "CareSites('" + name + "')/ProviderServices")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<PpmsProviderServices> entity = null;
          try {
            entity =
                restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, PpmsProviderServices.class);
          } catch (Exception e) {
            System.out.println("PPMS failed to return data for " + name);
          }
          return entity == null ? PpmsProviderServices.builder().build() : entity.getBody();
        });
  }

  @Override
  public PpmsCareSites careSitesByState(String state) {
    return handlePpmsExceptions(
        state,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "GetCareSiteByState?State=" + state)
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<PpmsCareSites> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, PpmsCareSites.class);
          return entity.getBody();
        });
  }

  @Override
  public PpmsCareSites careSitesByZip(String zip) {
    return handlePpmsExceptions(
        zip,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "GetCareSiteByZip?Zip=" + zip)
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<PpmsCareSites> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, PpmsCareSites.class);
          return entity.getBody();
        });
  }

  @Override
  public ProviderContacts providerContactsForId(String id) {
    return handlePpmsExceptions(
        id,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "Providers(" + id + ")/ProviderContacts")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<ProviderContacts> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, ProviderContacts.class);
          return entity.getBody();
        });
  }

  @Override
  public PpmsProviderSpecialtiesResponse providerSpecialtySearch(String id) {
    return handlePpmsExceptions(
        id,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(
                      baseUrl + "Providers(" + id + ")/ProviderSpecialties")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<PpmsProviderSpecialtiesResponse> entity =
              restTemplate.exchange(
                  url, HttpMethod.GET, requestEntity, PpmsProviderSpecialtiesResponse.class);
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
