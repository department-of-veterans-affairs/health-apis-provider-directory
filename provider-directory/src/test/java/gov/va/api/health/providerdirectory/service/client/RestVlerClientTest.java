package gov.va.api.health.providerdirectory.service.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.providerdirectory.service.AddressResponse;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public final class RestVlerClientTest {

  @Test
  @SuppressWarnings("unchecked")
  public void endpointByAddressTest() {
    ResponseEntity<AddressResponse> response = mock(ResponseEntity.class);
    when(response.getBody()).thenReturn(AddressResponse.builder().build());
    RestTemplate restTemplate = mock(RestTemplate.class);
    when(restTemplate.exchange(
            eq("http://foo.bar/direct/addresses"),
            eq(HttpMethod.GET),
            any(HttpEntity.class),
            eq(AddressResponse.class)))
        .thenReturn(response);
    RestVlerClient client =
        new RestVlerClient("http://foo.bar/", "testKey", "testKey", restTemplate);
    assertThat(client.endpointByAddress("test")).isEqualTo(AddressResponse.builder().build());
  }
}
