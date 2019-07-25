package gov.va.api.health.providerdirectory.service.client;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.PpmsResponse;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;
import java.util.Collections;
import java.util.concurrent.Callable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class RestPpmsClient implements PpmsClient {

  private final String baseUrl;

  private final RestTemplate restTemplate;

  public RestPpmsClient(
      @Value("${ppms.url}") String baseUrl, @Autowired RestTemplate restTemplate) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    this.restTemplate = restTemplate;
  }

  @SneakyThrows
  private static <T extends PpmsResponse> T handlePpmsExceptions(
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
      throw new PpmsException(message, e);
    }
    if (response == null) {
      throw new PpmsException(message + ", no PPMS response");
    }
    if (response.error() != null && isNotBlank(response.error().message())) {
      throw new PpmsException(message + ", " + response.error().message());
    }
    return response;
  }

  private static HttpHeaders headers() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList((MediaType.APPLICATION_JSON)));
    return headers;
  }

  @Override
  public CareSitesResponse careSitesByCity(String city) {
    return handlePpmsExceptions(
        city,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "GetCareSiteByCity?City=" + city)
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<CareSitesResponse> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, CareSitesResponse.class);
          return entity.getBody();
        });
  }

  @Override
  public CareSitesResponse careSitesById(String id) {
    return handlePpmsExceptions(
        id,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "Providers(" + id + ")/CareSites")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<CareSitesResponse> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, CareSitesResponse.class);
          return entity.getBody();
        });
  }

  @Override
  public CareSitesResponse careSitesByName(String name) {
    String hackyNameFix = name.split("'")[0].split("/")[0];
    return handlePpmsExceptions(
        hackyNameFix,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "CareSites('" + hackyNameFix + "')")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<CareSitesResponse> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, CareSitesResponse.class);
          return entity.getBody();
        });
  }

  @Override
  public CareSitesResponse careSitesByState(String state) {
    return handlePpmsExceptions(
        state,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "GetCareSiteByState?State=" + state)
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<CareSitesResponse> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, CareSitesResponse.class);
          return entity.getBody();
        });
  }

  @Override
  public CareSitesResponse careSitesByZip(String zip) {
    return handlePpmsExceptions(
        zip,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "GetCareSiteByZip?Zip=" + zip)
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<CareSitesResponse> entity =
              restTemplate.exchange(url, HttpMethod.GET, requestEntity, CareSitesResponse.class);
          return entity.getBody();
        });
  }

  @Override
  public ProviderContactsResponse providerContactsForId(String id) {
    return handlePpmsExceptions(
        "provider ID: " + id,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "Providers(" + id + ")/ProviderContacts")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<ProviderContactsResponse> entity =
              restTemplate.exchange(
                  url, HttpMethod.GET, requestEntity, ProviderContactsResponse.class);
          return entity.getBody();
        });
  }

  @Override
  public ProviderServicesResponse providerServicesById(String id) {
    return handlePpmsExceptions(
        id,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "Providers(" + id + ")/ProviderServices")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<ProviderServicesResponse> entity =
              restTemplate.exchange(
                  url, HttpMethod.GET, requestEntity, ProviderServicesResponse.class);
          return entity.getBody();
        });
  }

  @Override
  public ProviderServicesResponse providerServicesByName(String name) {
    String hackyNameFix = name.split("'")[0].split("/")[0];
    return handlePpmsExceptions(
        hackyNameFix,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(
                      baseUrl + "CareSites('" + hackyNameFix + "')/ProviderServices")
                  .build()
                  .toUriString();
          HttpEntity<?> requestEntity = new HttpEntity<>(headers());
          ResponseEntity<ProviderServicesResponse> entity = null;
          try {
            entity =
                restTemplate.exchange(
                    url, HttpMethod.GET, requestEntity, ProviderServicesResponse.class);
            return entity.getBody();
          } catch (Exception e) {
            log.error("PPMS failed to return data for " + name);
            return ProviderServicesResponse.builder().build();
          }
        });
  }

  @Override
  public ProviderSpecialtiesResponse providerSpecialtySearch(String id) {
    return handlePpmsExceptions(
        "provider ID: " + id,
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
        "provider ID: " + id,
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
        "provider name: " + name,
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
