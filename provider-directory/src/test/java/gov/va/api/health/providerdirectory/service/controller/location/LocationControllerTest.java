package gov.va.api.health.providerdirectory.service.controller.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.LocationWrapper;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.ConfigurableBaseUrlPageLinks;
import gov.va.api.health.stu3.api.resources.Location;
import lombok.SneakyThrows;
import org.junit.Test;

@SuppressWarnings("WeakerAccess")
public final class LocationControllerTest {

  LocationController.Transformer tx = new LocationTransformer();

  ConfigurableBaseUrlPageLinks configurableBaseUrlPageLinks =
      new ConfigurableBaseUrlPageLinks("", "");

  Bundler bundler = new Bundler(configurableBaseUrlPageLinks);

  PpmsClient ppmsClient = mock(PpmsClient.class);

  LocationController controller = new LocationController(tx, bundler, ppmsClient);

  @Test
  @SneakyThrows
  public void searchByCity() {
    LocationWrapper locationOne =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-1.json"),
                LocationWrapper.class);
    LocationWrapper locationTwo =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-2.json"),
                LocationWrapper.class);
    CareSitesResponse careSites =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/LocationTestResource/mock-care-sites-response.json"),
                CareSitesResponse.class);
    when(ppmsClient.careSitesByCity("Sharon")).thenReturn(careSites);
    when(ppmsClient.careSitesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(locationOne);
    when(ppmsClient.careSitesByName("Sharon Hospital Medical Practice")).thenReturn(locationTwo);
    Location.Bundle expected = controller.searchByCity("Sharon", 1, 2);
    Location.Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/LocationTestResource/expected-search-by-city.json"),
                Location.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByIdentifier() {
    LocationWrapper response =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-services-response.json"),
                LocationWrapper.class);
    when(ppmsClient.careSitesById("123")).thenReturn(response);
    Location.Bundle expected = controller.searchByIdentifier("123", 1, 15);
    Location.Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/expected-search-location-by-id.json"),
                Location.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByName() {
    LocationWrapper response =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-services-response.json"),
                LocationWrapper.class);
    when(ppmsClient.careSitesByName("A I Advance Imaging of Tulsa LLC")).thenReturn(response);
    Location.Bundle expected = controller.searchByName("A I Advance Imaging of Tulsa LLC", 1, 15);
    Location.Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/expected-search-location-by-name.json"),
                Location.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByState() {
    LocationWrapper locationOne =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-1.json"),
                LocationWrapper.class);
    LocationWrapper locationTwo =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-2.json"),
                LocationWrapper.class);
    CareSitesResponse careSites =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/LocationTestResource/mock-care-sites-response.json"),
                CareSitesResponse.class);
    when(ppmsClient.careSitesByState("Fl")).thenReturn(careSites);
    when(ppmsClient.careSitesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(locationOne);
    when(ppmsClient.careSitesByName("Sharon Hospital Medical Practice")).thenReturn(locationTwo);
    Location.Bundle expected = controller.searchByState("Fl", 1, 2);
    Location.Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/LocationTestResource/expected-search-by-state.json"),
                Location.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByZip() {
    LocationWrapper locationOne =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-1.json"),
                LocationWrapper.class);
    LocationWrapper locationTwo =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-2.json"),
                LocationWrapper.class);
    CareSitesResponse careSites =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/LocationTestResource/mock-care-sites-response.json"),
                CareSitesResponse.class);
    when(ppmsClient.careSitesByZip("45341")).thenReturn(careSites);
    when(ppmsClient.careSitesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(locationOne);
    when(ppmsClient.careSitesByName("Sharon Hospital Medical Practice")).thenReturn(locationTwo);
    Location.Bundle expected = controller.searchByZip("45341", 1, 2);
    Location.Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/LocationTestResource/expected-search-by-zip.json"),
                Location.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }
}
