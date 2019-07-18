package gov.va.api.health.providerdirectory.service.controller.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.api.resources.Location;
import gov.va.api.health.providerdirectory.service.PpmsCareSites;
import gov.va.api.health.providerdirectory.service.PpmsProviderServices;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.ConfigurableBaseUrlPageLinks;
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
    PpmsProviderServices locationOne =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/ppmsCareSites/ppms-provider-response-address-1.json"),
                PpmsProviderServices.class);
    PpmsProviderServices locationTwo =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/ppmsCareSites/ppms-provider-response-address-2.json"),
                PpmsProviderServices.class);
    PpmsCareSites careSites =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppmsCareSites/ppms-care-sites-response.json"),
                PpmsCareSites.class);
    when(ppmsClient.careSitesByCity("Sharon")).thenReturn(careSites);
    when(ppmsClient.careSitesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(locationOne);
    when(ppmsClient.careSitesByName("Sharon Hospital Medical Practice")).thenReturn(locationTwo);
    Location.Bundle expected = controller.searchByCity("Sharon", 1, 2);
    Location.Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppmsCareSites/test-search-by-city.json"),
                Location.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByIdentifier() {
    PpmsProviderServices response =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/ppmsCareSites/ppms-provider-services-response.json"),
                PpmsProviderServices.class);
    when(ppmsClient.careSitesById("123")).thenReturn(response);
    Location.Bundle expected = controller.searchByIdentifier("123", 1, 15);
    Location.Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppmsCareSites/test-search-location-by-id.json"),
                Location.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByName() {
    PpmsProviderServices response =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/ppmsCareSites/ppms-provider-services-response.json"),
                PpmsProviderServices.class);
    when(ppmsClient.careSitesByName("A I Advance Imaging of Tulsa LLC")).thenReturn(response);
    Location.Bundle expected = controller.searchByName("A I Advance Imaging of Tulsa LLC", 1, 15);
    Location.Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppmsCareSites/test-search-location-by-name.json"),
                Location.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByState() {
    PpmsProviderServices locationOne =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/ppmsCareSites/ppms-provider-response-address-1.json"),
                PpmsProviderServices.class);
    PpmsProviderServices locationTwo =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/ppmsCareSites/ppms-provider-response-address-2.json"),
                PpmsProviderServices.class);
    PpmsCareSites careSites =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppmsCareSites/ppms-care-sites-response.json"),
                PpmsCareSites.class);
    when(ppmsClient.careSitesByState("Fl")).thenReturn(careSites);
    when(ppmsClient.careSitesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(locationOne);
    when(ppmsClient.careSitesByName("Sharon Hospital Medical Practice")).thenReturn(locationTwo);
    Location.Bundle expected = controller.searchByState("Fl", 1, 2);
    Location.Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppmsCareSites/test-search-by-state.json"),
                Location.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByZip() {
    PpmsProviderServices locationOne =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/ppmsCareSites/ppms-provider-response-address-1.json"),
                PpmsProviderServices.class);
    PpmsProviderServices locationTwo =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/ppmsCareSites/ppms-provider-response-address-2.json"),
                PpmsProviderServices.class);
    PpmsCareSites careSites =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppmsCareSites/ppms-care-sites-response.json"),
                PpmsCareSites.class);
    when(ppmsClient.careSitesByZip("45341")).thenReturn(careSites);
    when(ppmsClient.careSitesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(locationOne);
    when(ppmsClient.careSitesByName("Sharon Hospital Medical Practice")).thenReturn(locationTwo);
    Location.Bundle expected = controller.searchByZip("45341", 1, 2);
    Location.Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppmsCareSites/test-search-by-zip.json"),
                Location.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }
}
