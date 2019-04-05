package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.api.resources.PractitionerRole;
import gov.va.api.health.providerdirectory.api.resources.PractitionerRole.Bundle;
import gov.va.api.health.providerdirectory.service.PpmsProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.ProviderContacts;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.ConfigurableBaseUrlPageLinks;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@SuppressWarnings("WeakerAccess")
public class PractitionerRoleControllerTest {
  Bundle expected;

  Bundle actual;

  PractitionerRoleController.Transformer tx = new PractitionerRoleTransformer();

  PractitionerRoleController controller;

  ConfigurableBaseUrlPageLinks configurableBaseUrlPageLinks =
      new ConfigurableBaseUrlPageLinks("", "");

  Bundler bundler = new Bundler(configurableBaseUrlPageLinks);

  @Mock PpmsClient ppmsClient;

  @Before
  public void _init() {
    MockitoAnnotations.initMocks(this);
    controller = new PractitionerRoleController(ppmsClient, tx, bundler);
  }

  @Test
  @SneakyThrows
  public void searchByFamilyAndGiven() {
    ProviderResponse response =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/ppmsPractitionerRole/ppms-provider-by-identifier-response.json"),
                ProviderResponse.class);
    ProviderContacts contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/ppmsPractitionerRole/ppms-provider-contact-response.json"),
                ProviderContacts.class);
    PpmsProviderSpecialtiesResponse specialties =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/ppmsPractitionerRole/ppms-provider-specialties-response.json"),
                PpmsProviderSpecialtiesResponse.class);

    when(ppmsClient.providersForName("Klingerman, Michael")).thenReturn(response);
    when(ppmsClient.providerContactsForId("1285621557")).thenReturn(contacts);
    when(ppmsClient.providerSpecialtySearch("1285621557")).thenReturn(specialties);
    expected = controller.searchByFamilyAndGiven("Klingerman", "Michael", 1, 1);
    actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/ppmsPractitionerRole/actual-search-by-family-and-given.json"),
                PractitionerRole.Bundle.class);
    Assertions.assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByIdentifier() {
    ProviderResponse response =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/ppmsPractitionerRole/ppms-provider-by-identifier-response.json"),
                ProviderResponse.class);
    ProviderContacts contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/ppmsPractitionerRole/ppms-provider-contact-response.json"),
                ProviderContacts.class);
    PpmsProviderSpecialtiesResponse specialties =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/ppmsPractitionerRole/ppms-provider-specialties-response.json"),
                PpmsProviderSpecialtiesResponse.class);
    when(ppmsClient.providersForId("identifier")).thenReturn(response);
    when(ppmsClient.providerContactsForId("1285621557")).thenReturn(contacts);
    when(ppmsClient.providerSpecialtySearch("1285621557")).thenReturn(specialties);
    expected = controller.searchByIdentifier("identifier", 1, 1);
    actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/ppmsPractitionerRole/actual-search-by-identifier.json"),
                PractitionerRole.Bundle.class);
    Assertions.assertThat(actual).isEqualTo(expected);
  }
}
