package gov.va.api.health.providerdirectory.service.controller.location;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Iterables;
import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.ConfigurableBaseUrlPageLinks;
import gov.va.api.health.stu3.api.datatypes.ContactPoint;
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
    ProviderServicesResponse locationOne =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-1.json"),
                ProviderServicesResponse.class);
    ProviderServicesResponse locationTwo =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-2.json"),
                ProviderServicesResponse.class);
    CareSitesResponse careSites =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/LocationTestResource/mock-care-sites-response.json"),
                CareSitesResponse.class);
    when(ppmsClient.careSitesByCity("Sharon")).thenReturn(careSites);
    when(ppmsClient.providerServicesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(locationOne);
    when(ppmsClient.providerServicesByName("Sharon Hospital Medical Practice"))
        .thenReturn(locationTwo);
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
    when(ppmsClient.providerServicesById("123"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(
                    asList(
                        ProviderServicesResponse.Value.builder()
                            .name("A I Advance Imaging of Tulsa LLC - Clinic/Center - Radiology ")
                            .affiliationName("TriWest - Choice")
                            .relationshipName("Choice")
                            .providerName("A I Advance Imaging of Tulsa LLC")
                            .specialtyName("Clinic/Center - Radiology")
                            .hpp("Unknown")
                            .highPerformingProvider("TriWest - Choice(U)")
                            .careSiteName("A I Advance Imaging of Tulsa LLC")
                            .careSiteLocationAddress("6711 S Yale Ave Ste 212, Tulsa, OK, 74136")
                            .careSiteAddressStreet("6711 S Yale Ave Ste 212")
                            .careSiteAddressCity("Tulsa")
                            .careSiteAddressState("OK")
                            .careSiteAddressZipCode("74136")
                            .latitude("36.066014")
                            .longitude("-95.921907")
                            .careSitePhoneNumber("9185230002")
                            .organiztionGroupName("A I Advance Imaging of Tulsa LLC")
                            .build()))
                .build());
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
    when(ppmsClient.providerServicesByName("A I Advance Imaging of Tulsa LLC"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(
                    asList(
                        ProviderServicesResponse.Value.builder()
                            .name("A I Advance Imaging of Tulsa LLC - Clinic/Center - Radiology ")
                            .affiliationName("TriWest - Choice")
                            .relationshipName("Choice")
                            .providerName("A I Advance Imaging of Tulsa LLC")
                            .specialtyName("Clinic/Center - Radiology")
                            .hpp("Unknown")
                            .highPerformingProvider("TriWest - Choice(U)")
                            .careSiteName("A I Advance Imaging of Tulsa LLC")
                            .careSiteLocationAddress("6711 S Yale Ave Ste 212, Tulsa, OK, 74136")
                            .careSiteAddressStreet("6711 S Yale Ave Ste 212")
                            .careSiteAddressCity("Tulsa")
                            .careSiteAddressState("OK")
                            .careSiteAddressZipCode("74136")
                            .latitude("36.066014")
                            .longitude("-95.921907")
                            .careSitePhoneNumber("9185230002")
                            .organiztionGroupName("A I Advance Imaging of Tulsa LLC")
                            .build()))
                .build());

    Location.Bundle actual = controller.searchByName("A I Advance Imaging of Tulsa LLC", 1, 15);

    assertThat(Iterables.getOnlyElement(actual.entry()).resource())
        .isEqualTo(
            Location.builder()
                .resourceType("Location")
                .status(Location.Status.active)
                .name("A I Advance Imaging of Tulsa LLC")
                .telecom(
                    asList(
                        ContactPoint.builder()
                            .system(ContactPoint.ContactPointSystem.phone)
                            .value("9185230002")
                            .build()))
                .address(
                    Location.LocationAddress.builder()
                        .text("6711 S Yale Ave Ste 212, Tulsa, OK, 74136")
                        .line(asList("6711 S Yale Ave Ste 212"))
                        .city("Tulsa")
                        .state("OK")
                        .postalCode("74136")
                        .build())
                .build());
  }

  @Test
  @SneakyThrows
  public void searchByState() {
    ProviderServicesResponse locationOne =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-1.json"),
                ProviderServicesResponse.class);
    ProviderServicesResponse locationTwo =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-2.json"),
                ProviderServicesResponse.class);
    CareSitesResponse careSites =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/LocationTestResource/mock-care-sites-response.json"),
                CareSitesResponse.class);
    when(ppmsClient.careSitesByState("Fl")).thenReturn(careSites);
    when(ppmsClient.providerServicesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(locationOne);
    when(ppmsClient.providerServicesByName("Sharon Hospital Medical Practice"))
        .thenReturn(locationTwo);
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
    ProviderServicesResponse locationOne =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-1.json"),
                ProviderServicesResponse.class);
    ProviderServicesResponse locationTwo =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/LocationTestResource/mock-provider-response-address-2.json"),
                ProviderServicesResponse.class);
    CareSitesResponse careSites =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/LocationTestResource/mock-care-sites-response.json"),
                CareSitesResponse.class);
    when(ppmsClient.careSitesByZip("45341")).thenReturn(careSites);
    when(ppmsClient.providerServicesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(locationOne);
    when(ppmsClient.providerServicesByName("Sharon Hospital Medical Practice"))
        .thenReturn(locationTwo);
    Location.Bundle expected = controller.searchByZip("45341", 1, 2);
    Location.Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/LocationTestResource/expected-search-by-zip.json"),
                Location.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }
}
