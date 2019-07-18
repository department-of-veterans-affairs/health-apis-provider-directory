package gov.va.api.health.providerdirectory.service.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
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
  public void providerContactsForId() {
    ResponseEntity<ProviderContactsResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(ProviderContactsResponse.builder().build());

    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/Providers(123)/ProviderContactsResponse"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(ProviderContactsResponse.class)))
        .thenReturn(response);

    RestPpmsClient client = new RestPpmsClient("http://foo.bar/", restTemplate);
    assertThat(client.providerContactsForId("123")).isEqualTo(ProviderContactsResponse.builder().build());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void providerResponseForId() {
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
  public void providerResponseForName() {
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
  public void providerSpecialtySearch() {
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
}
