package gov.va.api.health.providerdirectory.service.client;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.service.AddressResponse;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

public final class RestVlerClientTest {
  @Test
  @SneakyThrows
  public void endpointByAddress() {
    String baseUrl = "http://foo.bar/";
    AddressResponse addressResponse =
        AddressResponse.builder()
            .contacts(
                asList(
                    AddressResponse.Contacts.builder()
                        .displayName("Pilot, Test")
                        .emailAddress("test.pilot@test2.direct.va.gov")
                        .uid("test.pilot")
                        .givenName("Test")
                        .surname("Pilot")
                        .officeCityState("Indianapolis, IN")
                        .companyName("Test Company")
                        .departmentNumber("Test Department")
                        .mobile("123-456-7890")
                        .telephoneNumber("098-765-4321")
                        .title("Testing Analyzer")
                        .commonName("Test Pilot")
                        .facility("Test Facility")
                        .build()))
            .build();

    HttpURLConnection connection = mock(HttpURLConnection.class);
    when(connection.getInputStream())
        .thenReturn(
            new ByteArrayInputStream(
                JacksonConfig.createMapper()
                    .writeValueAsString(addressResponse)
                    .getBytes("UTF-8")));

    // URL cannot be mocked, so its internal URL stream handler must be overridden
    URL url = new URL(baseUrl);
    Field handlerField = URL.class.getDeclaredField("handler");
    ReflectionUtils.makeAccessible(handlerField);
    handlerField.set(url, new MockUrlStreamHandler(connection));

    RestVlerClient client = new RestVlerClient(baseUrl, "unused", "unused", "unused", "unused");
    assertThat(client.endpointByAddress("test", baseUrl, url)).isEqualTo(addressResponse);
  }

  @SneakyThrows
  @Test(expected = Exceptions.VlerException.class)
  public void endpointByAddressError() {
    String baseUrl = "http://foo.bar/";
    HttpURLConnection connection = mock(HttpURLConnection.class);
    when(connection.getInputStream()).thenThrow(new RuntimeException());

    URL url = new URL(baseUrl);
    Field handlerField = URL.class.getDeclaredField("handler");
    ReflectionUtils.makeAccessible(handlerField);
    handlerField.set(url, new MockUrlStreamHandler(connection));

    RestVlerClient client = new RestVlerClient(baseUrl, "unused", "unused", "unused", "unused");
    client.endpointByAddress("test", baseUrl, url);
  }

  @AllArgsConstructor
  private static final class MockUrlStreamHandler extends URLStreamHandler {
    final HttpURLConnection connection;

    @Override
    protected URLConnection openConnection(URL u) {
      return connection;
    }
  }
}
