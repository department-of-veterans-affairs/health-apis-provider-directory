package gov.va.api.health.providerdirectory.service.client;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.PpmsResponse;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Collections;
import java.util.concurrent.Callable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.SneakyThrows;
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
@Component
public class RestPpmsClient implements PpmsClient {
  private String baseUrl;

  private String keyStoreName;

  private String keyStorePassword;

  private RestTemplate restTemplate;

  /** Autowired constructor. */
  public RestPpmsClient(
      @Value("${ppms.url}") String baseUrl,
      @Value("${ppms.keystore.name}") String keyStoreName,
      @Value("${ppms.keystore.password}") String keyStorePassword) {
    this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
    this.keyStoreName = keyStoreName;
    this.keyStorePassword = keyStorePassword;
    this.restTemplate = secureRestTemplate();
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
    return handlePpmsExceptions(
        name,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(baseUrl + "CareSites('" + name + "')")
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
    return handlePpmsExceptions(
        name,
        () -> {
          String url =
              UriComponentsBuilder.fromHttpUrl(
                      baseUrl + "CareSites('" + name + "')/ProviderServices")
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

  @SneakyThrows
  private RestTemplate secureRestTemplate() {
    ClassLoader cl = getClass().getClassLoader();
    if (cl == null) {
      throw new RuntimeException("Something went wrong getting the class loader");
    }
    try (InputStream truststoreInputStream = cl.getResourceAsStream(keyStoreName)) {
      KeyStore keystore = KeyStore.getInstance("JKS");
      keystore.load(truststoreInputStream, keyStorePassword.toCharArray());
      SSLConnectionSocketFactory socketFactory =
          new SSLConnectionSocketFactory(
              new SSLContextBuilder()
                  .loadTrustMaterial(keystore, new TrustSelfSignedStrategy())
                  .loadKeyMaterial(keystore, keyStorePassword.toCharArray())
                  .build(),
              SSLConnectionSocketFactory.getDefaultHostnameVerifier());
      HttpClient httpsClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
      return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpsClient));
    }
  }
}
