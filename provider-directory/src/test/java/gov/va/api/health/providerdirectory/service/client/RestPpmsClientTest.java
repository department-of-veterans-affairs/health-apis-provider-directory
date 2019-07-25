package gov.va.api.health.providerdirectory.service.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public final class RestPpmsClientTest {

  @Test
  @SuppressWarnings("unchecked")
  public void addTrailingSlashToBaseUrl() {
    ResponseEntity<ProviderResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(ProviderResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/Providers(123)"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ProviderResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar", restTemplate);
    assertThat(client.providersForId("123")).isEqualTo(ProviderResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void careSitesByCityTest() {
    ResponseEntity<CareSitesResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(CareSitesResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/GetCareSiteByCity?City=city"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CareSitesResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.careSitesByCity("city")).isEqualTo(CareSitesResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void careSitesByIdTest() {
    ResponseEntity<CareSitesResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(CareSitesResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/Providers(123)/CareSites"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CareSitesResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.careSitesById("123")).isEqualTo(CareSitesResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void careSitesByNameTest() {
    ResponseEntity<CareSitesResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(CareSitesResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/CareSites('Name')"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CareSitesResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.careSitesByName("Name")).isEqualTo(CareSitesResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void careSitesByStateTest() {
    ResponseEntity<CareSitesResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(CareSitesResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/GetCareSiteByState?State=Fl"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CareSitesResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.careSitesByState("Fl")).isEqualTo(CareSitesResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void careSitesByZipTest() {
    ResponseEntity<CareSitesResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(CareSitesResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/GetCareSiteByZip?Zip=12345"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(CareSitesResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.careSitesByZip("12345")).isEqualTo(CareSitesResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void providerContactsForIdTest() {
    ResponseEntity<ProviderContactsResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(ProviderContactsResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/Providers(123)/ProviderContacts"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ProviderContactsResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.providerContactsForId("123"))
        .isEqualTo(ProviderContactsResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void providerForNameTest() {
    ResponseEntity<ProviderResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(ProviderResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/GetProviderByName?name=nelson, bob"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ProviderResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.providersForName("nelson, bob"))
        .isEqualTo(ProviderResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void providerServicesByNameTest() {
    ResponseEntity<ProviderServicesResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(ProviderServicesResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/CareSites('CareSite')/ProviderServices"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ProviderServicesResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.providerServicesByName("CareSite"))
        .isEqualTo(ProviderServicesResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void providerSpecialtySearchTest() {
    ResponseEntity<ProviderSpecialtiesResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(ProviderSpecialtiesResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/Providers(123)/ProviderSpecialties"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ProviderSpecialtiesResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.providerSpecialtySearch("123"))
        .isEqualTo(ProviderSpecialtiesResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void providersForIdTest() {
    ResponseEntity<ProviderResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(ProviderResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/Providers(123)"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ProviderResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.providersForId("123")).isEqualTo(ProviderResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void providersServicesByIdTest() {
    ResponseEntity<ProviderServicesResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(ProviderServicesResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/Providers(123)/ProviderServices"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ProviderServicesResponse.class)))
        .thenReturn(response);
    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.providerServicesById("123"))
        .isEqualTo(ProviderServicesResponse.builder().build());
  }
}
