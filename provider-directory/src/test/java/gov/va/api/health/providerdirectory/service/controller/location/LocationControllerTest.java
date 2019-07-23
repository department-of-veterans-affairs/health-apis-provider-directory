package gov.va.api.health.providerdirectory.service.controller.location;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Iterables;
import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.ConfigurableBaseUrlPageLinks;
import gov.va.api.health.stu3.api.datatypes.ContactPoint;
import gov.va.api.health.stu3.api.resources.Location;
import java.util.stream.Collectors;
import org.junit.Test;

public final class LocationControllerTest {
  LocationController.Transformer tx = new LocationTransformer();

  ConfigurableBaseUrlPageLinks configurableBaseUrlPageLinks =
      new ConfigurableBaseUrlPageLinks("", "");

  Bundler bundler = new Bundler(configurableBaseUrlPageLinks);

  PpmsClient ppmsClient = mock(PpmsClient.class);

  LocationController controller = new LocationController(tx, bundler, ppmsClient);

  @Test
  public void searchByCity() {
    when(ppmsClient.careSitesByCity("Sharon"))
        .thenReturn(
            CareSitesResponse.builder()
                .value(
                    asList(
                        CareSitesResponse.Value.builder()
                            .name("Beacon Orthopaedics & Sports Medicine Ltd")
                            .id("203fa25f-1e06-4811-94cb-0462c480b538")
                            .careSiteType("Facility")
                            .vaCareSite(false)
                            .centerOfExcellence("Unknown")
                            .isHandicapAccessible(false)
                            .isExternal(true)
                            .street("500 E Business Way")
                            .city("Sharonville")
                            .state("OH")
                            .zipCode("45241")
                            .latitude(39.285282)
                            .longitude(-84.365756)
                            .geocoded(true)
                            .build(),
                        CareSitesResponse.Value.builder()
                            .name("Sharon Hospital Medical Practice")
                            .id("71a07d59-1304-4e6b-bfd3-00002299138e")
                            .careSiteType("Facility")
                            .vaCareSite(false)
                            .centerOfExcellence("Unknown")
                            .isHandicapAccessible(false)
                            .isExternal(true)
                            .street("50 Hospital Hill Rd")
                            .city("Sharon")
                            .state("CT")
                            .zipCode("06069")
                            .latitude(41.88094667)
                            .longitude(-73.48159833)
                            .geocoded(true)
                            .build(),
                        CareSitesResponse.Value.builder()
                            .name("WALLACE COUNTY FAMILY PRACTICE CLINIC")
                            .id("5e6029bb-acc2-413c-b500-0577f223afbc")
                            .careSiteType("Facility")
                            .vaCareSite(false)
                            .centerOfExcellence("Unknown")
                            .isHandicapAccessible(false)
                            .isExternal(true)
                            .street("504 E 6th")
                            .city("Sharon Springs")
                            .state("KS")
                            .zipCode("67758")
                            .latitude(38.900732)
                            .longitude(-101.746397)
                            .geocoded(true)
                            .build()))
                .build());

    when(ppmsClient.providerServicesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(
                    asList(
                        ProviderServicesResponse.Value.builder()
                            .name("Razzano Jr, Andrew  - Orthopaedic Surgery - Sports Medicine ")
                            .affiliationName("TriWest - Choice")
                            .relationshipName("Choice")
                            .providerName("Razzano Jr, Andrew")
                            .specialtyName("Orthopaedic Surgery - Sports Medicine")
                            .hpp("Unknown")
                            .highPerformingProvider("TriWest - Choice(U)")
                            .careSiteName("Beacon Orthopaedics & Sports Medicine Ltd")
                            .careSiteLocationAddress("500 E Business Way, Sharonville, OH, 45241")
                            .careSiteAddressStreet("500 E Business Way")
                            .careSiteAddressCity("Sharonville")
                            .careSiteAddressState("OH")
                            .careSiteAddressZipCode("45241")
                            .latitude("39.285282")
                            .longitude("-84.365756")
                            .careSitePhoneNumber("5133543700")
                            .build()))
                .build());
    when(ppmsClient.providerServicesByName("Sharon Hospital Medical Practice"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(
                    asList(
                        ProviderServicesResponse.Value.builder()
                            .name("Clark, Alexander  - Orthopaedic Surgery - Sports Medicine ")
                            .affiliationName("TriWest - Choice")
                            .relationshipName("Choice")
                            .providerName("Clark, Alexander")
                            .specialtyName("Orthopaedic Surgery - Sports Medicine")
                            .hpp("Unknown")
                            .highPerformingProvider("TriWest - Choice(U)")
                            .careSiteName("Sharon Hospital Medical Practice")
                            .careSiteLocationAddress("50 Hospital Hill Rd, Sharon, CT, 06069")
                            .careSiteAddressStreet("50 Hospital Hill Rd")
                            .careSiteAddressCity("Sharon")
                            .careSiteAddressState("CT")
                            .careSiteAddressZipCode("06069")
                            .latitude("41.88094667")
                            .longitude("-73.48159833")
                            .careSitePhoneNumber("8603644511")
                            .build()))
                .build());

    Location.Bundle actual = controller.searchByCity("Sharon", 1, 2);

    assertThat(actual.entry().stream().map(e -> e.resource()).collect(Collectors.toList()))
        .isEqualTo(
            asList(
                Location.builder()
                    .resourceType("Location")
                    .status(Location.Status.active)
                    .name("Beacon Orthopaedics & Sports Medicine Ltd")
                    .telecom(
                        asList(
                            ContactPoint.builder()
                                .system(ContactPoint.ContactPointSystem.phone)
                                .value("5133543700")
                                .build()))
                    .address(
                        Location.LocationAddress.builder()
                            .text("500 E Business Way, Sharonville, OH, 45241")
                            .line(asList("500 E Business Way"))
                            .city("Sharonville")
                            .state("OH")
                            .postalCode("45241")
                            .build())
                    .build(),
                Location.builder()
                    .resourceType("Location")
                    .status(Location.Status.active)
                    .name("Sharon Hospital Medical Practice")
                    .telecom(
                        asList(
                            ContactPoint.builder()
                                .system(ContactPoint.ContactPointSystem.phone)
                                .value("8603644511")
                                .build()))
                    .address(
                        Location.LocationAddress.builder()
                            .text("50 Hospital Hill Rd, Sharon, CT, 06069")
                            .line(asList("50 Hospital Hill Rd"))
                            .city("Sharon")
                            .state("CT")
                            .postalCode("06069")
                            .build())
                    .build()));
  }

  @Test
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
    Location.Bundle actual = controller.searchByIdentifier("123", 1, 15);

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
  public void searchByState() {
    when(ppmsClient.careSitesByState("Fl"))
        .thenReturn(
            CareSitesResponse.builder()
                .value(
                    asList(
                        CareSitesResponse.Value.builder()
                            .name("Beacon Orthopaedics & Sports Medicine Ltd")
                            .id("203fa25f-1e06-4811-94cb-0462c480b538")
                            .careSiteType("Facility")
                            .vaCareSite(false)
                            .centerOfExcellence("Unknown")
                            .isHandicapAccessible(false)
                            .isExternal(true)
                            .street("500 E Business Way")
                            .city("Sharonville")
                            .state("OH")
                            .zipCode("45241")
                            .latitude(39.285282)
                            .longitude(-84.365756)
                            .geocoded(true)
                            .build(),
                        CareSitesResponse.Value.builder()
                            .name("Sharon Hospital Medical Practice")
                            .id("71a07d59-1304-4e6b-bfd3-00002299138e")
                            .careSiteType("Facility")
                            .vaCareSite(false)
                            .centerOfExcellence("Unknown")
                            .isHandicapAccessible(false)
                            .isExternal(true)
                            .street("50 Hospital Hill Rd")
                            .city("Sharon")
                            .state("CT")
                            .zipCode("06069")
                            .latitude(41.88094667)
                            .longitude(-73.48159833)
                            .geocoded(true)
                            .build(),
                        CareSitesResponse.Value.builder()
                            .name("WALLACE COUNTY FAMILY PRACTICE CLINIC")
                            .id("5e6029bb-acc2-413c-b500-0577f223afbc")
                            .careSiteType("Facility")
                            .vaCareSite(false)
                            .centerOfExcellence("Unknown")
                            .isHandicapAccessible(false)
                            .isExternal(true)
                            .street("504 E 6th")
                            .city("Sharon Springs")
                            .state("KS")
                            .zipCode("67758")
                            .latitude(38.900732)
                            .longitude(-101.746397)
                            .geocoded(true)
                            .build()))
                .build());

    when(ppmsClient.providerServicesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(
                    asList(
                        ProviderServicesResponse.Value.builder()
                            .name("Razzano Jr, Andrew  - Orthopaedic Surgery - Sports Medicine ")
                            .affiliationName("TriWest - Choice")
                            .relationshipName("Choice")
                            .providerName("Razzano Jr, Andrew")
                            .specialtyName("Orthopaedic Surgery - Sports Medicine")
                            .hpp("Unknown")
                            .highPerformingProvider("TriWest - Choice(U)")
                            .careSiteName("Beacon Orthopaedics & Sports Medicine Ltd")
                            .careSiteLocationAddress("500 E Business Way, Sharonville, OH, 45241")
                            .careSiteAddressStreet("500 E Business Way")
                            .careSiteAddressCity("Sharonville")
                            .careSiteAddressState("OH")
                            .careSiteAddressZipCode("45241")
                            .latitude("39.285282")
                            .longitude("-84.365756")
                            .careSitePhoneNumber("5133543700")
                            .build()))
                .build());
    when(ppmsClient.providerServicesByName("Sharon Hospital Medical Practice"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(
                    asList(
                        ProviderServicesResponse.Value.builder()
                            .name("Clark, Alexander  - Orthopaedic Surgery - Sports Medicine ")
                            .affiliationName("TriWest - Choice")
                            .relationshipName("Choice")
                            .providerName("Clark, Alexander")
                            .specialtyName("Orthopaedic Surgery - Sports Medicine")
                            .hpp("Unknown")
                            .highPerformingProvider("TriWest - Choice(U)")
                            .careSiteName("Sharon Hospital Medical Practice")
                            .careSiteLocationAddress("50 Hospital Hill Rd, Sharon, CT, 06069")
                            .careSiteAddressStreet("50 Hospital Hill Rd")
                            .careSiteAddressCity("Sharon")
                            .careSiteAddressState("CT")
                            .careSiteAddressZipCode("06069")
                            .latitude("41.88094667")
                            .longitude("-73.48159833")
                            .careSitePhoneNumber("8603644511")
                            .build()))
                .build());

    Location.Bundle expected = controller.searchByState("Fl", 1, 2);

    assertThat(expected.entry().stream().map(e -> e.resource()).collect(Collectors.toList()))
        .isEqualTo(
            asList(
                Location.builder()
                    .resourceType("Location")
                    .status(Location.Status.active)
                    .name("Beacon Orthopaedics & Sports Medicine Ltd")
                    .telecom(
                        asList(
                            ContactPoint.builder()
                                .system(ContactPoint.ContactPointSystem.phone)
                                .value("5133543700")
                                .build()))
                    .address(
                        Location.LocationAddress.builder()
                            .text("500 E Business Way, Sharonville, OH, 45241")
                            .line(asList("500 E Business Way"))
                            .city("Sharonville")
                            .state("OH")
                            .postalCode("45241")
                            .build())
                    .build(),
                Location.builder()
                    .resourceType("Location")
                    .status(Location.Status.active)
                    .name("Sharon Hospital Medical Practice")
                    .telecom(
                        asList(
                            ContactPoint.builder()
                                .system(ContactPoint.ContactPointSystem.phone)
                                .value("8603644511")
                                .build()))
                    .address(
                        Location.LocationAddress.builder()
                            .text("50 Hospital Hill Rd, Sharon, CT, 06069")
                            .line(asList("50 Hospital Hill Rd"))
                            .city("Sharon")
                            .state("CT")
                            .postalCode("06069")
                            .build())
                    .build()));
  }

  @Test
  public void searchByZip() {
    when(ppmsClient.careSitesByZip("45341"))
        .thenReturn(
            CareSitesResponse.builder()
                .value(
                    asList(
                        CareSitesResponse.Value.builder()
                            .name("Beacon Orthopaedics & Sports Medicine Ltd")
                            .id("203fa25f-1e06-4811-94cb-0462c480b538")
                            .careSiteType("Facility")
                            .vaCareSite(false)
                            .centerOfExcellence("Unknown")
                            .isHandicapAccessible(false)
                            .isExternal(true)
                            .street("500 E Business Way")
                            .city("Sharonville")
                            .state("OH")
                            .zipCode("45241")
                            .latitude(39.285282)
                            .longitude(-84.365756)
                            .geocoded(true)
                            .build(),
                        CareSitesResponse.Value.builder()
                            .name("Sharon Hospital Medical Practice")
                            .id("71a07d59-1304-4e6b-bfd3-00002299138e")
                            .careSiteType("Facility")
                            .vaCareSite(false)
                            .centerOfExcellence("Unknown")
                            .isHandicapAccessible(false)
                            .isExternal(true)
                            .street("50 Hospital Hill Rd")
                            .city("Sharon")
                            .state("CT")
                            .zipCode("06069")
                            .latitude(41.88094667)
                            .longitude(-73.48159833)
                            .geocoded(true)
                            .build(),
                        CareSitesResponse.Value.builder()
                            .name("WALLACE COUNTY FAMILY PRACTICE CLINIC")
                            .id("5e6029bb-acc2-413c-b500-0577f223afbc")
                            .careSiteType("Facility")
                            .vaCareSite(false)
                            .centerOfExcellence("Unknown")
                            .isHandicapAccessible(false)
                            .isExternal(true)
                            .street("504 E 6th")
                            .city("Sharon Springs")
                            .state("KS")
                            .zipCode("67758")
                            .latitude(38.900732)
                            .longitude(-101.746397)
                            .geocoded(true)
                            .build()))
                .build());

    when(ppmsClient.providerServicesByName("Beacon Orthopaedics & Sports Medicine Ltd"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(
                    asList(
                        ProviderServicesResponse.Value.builder()
                            .name("Razzano Jr, Andrew  - Orthopaedic Surgery - Sports Medicine ")
                            .affiliationName("TriWest - Choice")
                            .relationshipName("Choice")
                            .providerName("Razzano Jr, Andrew")
                            .specialtyName("Orthopaedic Surgery - Sports Medicine")
                            .hpp("Unknown")
                            .highPerformingProvider("TriWest - Choice(U)")
                            .careSiteName("Beacon Orthopaedics & Sports Medicine Ltd")
                            .careSiteLocationAddress("500 E Business Way, Sharonville, OH, 45241")
                            .careSiteAddressStreet("500 E Business Way")
                            .careSiteAddressCity("Sharonville")
                            .careSiteAddressState("OH")
                            .careSiteAddressZipCode("45241")
                            .latitude("39.285282")
                            .longitude("-84.365756")
                            .careSitePhoneNumber("5133543700")
                            .build()))
                .build());
    when(ppmsClient.providerServicesByName("Sharon Hospital Medical Practice"))
        .thenReturn(
            ProviderServicesResponse.builder()
                .value(
                    asList(
                        ProviderServicesResponse.Value.builder()
                            .name("Clark, Alexander  - Orthopaedic Surgery - Sports Medicine ")
                            .affiliationName("TriWest - Choice")
                            .relationshipName("Choice")
                            .providerName("Clark, Alexander")
                            .specialtyName("Orthopaedic Surgery - Sports Medicine")
                            .hpp("Unknown")
                            .highPerformingProvider("TriWest - Choice(U)")
                            .careSiteName("Sharon Hospital Medical Practice")
                            .careSiteLocationAddress("50 Hospital Hill Rd, Sharon, CT, 06069")
                            .careSiteAddressStreet("50 Hospital Hill Rd")
                            .careSiteAddressCity("Sharon")
                            .careSiteAddressState("CT")
                            .careSiteAddressZipCode("06069")
                            .latitude("41.88094667")
                            .longitude("-73.48159833")
                            .careSitePhoneNumber("8603644511")
                            .build()))
                .build());

    Location.Bundle actual = controller.searchByZip("45341", 1, 2);

    assertThat(actual.entry().stream().map(e -> e.resource()).collect(Collectors.toList()))
        .isEqualTo(
            asList(
                Location.builder()
                    .resourceType("Location")
                    .status(Location.Status.active)
                    .name("Beacon Orthopaedics & Sports Medicine Ltd")
                    .telecom(
                        asList(
                            ContactPoint.builder()
                                .system(ContactPoint.ContactPointSystem.phone)
                                .value("5133543700")
                                .build()))
                    .address(
                        Location.LocationAddress.builder()
                            .text("500 E Business Way, Sharonville, OH, 45241")
                            .line(asList("500 E Business Way"))
                            .city("Sharonville")
                            .state("OH")
                            .postalCode("45241")
                            .build())
                    .build(),
                Location.builder()
                    .resourceType("Location")
                    .status(Location.Status.active)
                    .name("Sharon Hospital Medical Practice")
                    .telecom(
                        asList(
                            ContactPoint.builder()
                                .system(ContactPoint.ContactPointSystem.phone)
                                .value("8603644511")
                                .build()))
                    .address(
                        Location.LocationAddress.builder()
                            .text("50 Hospital Hill Rd, Sharon, CT, 06069")
                            .line(asList("50 Hospital Hill Rd"))
                            .city("Sharon")
                            .state("CT")
                            .postalCode("06069")
                            .build())
                    .build()));
  }
}
