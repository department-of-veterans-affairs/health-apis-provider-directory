package gov.va.api.health.providerdirectory.service.controller.practitioner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.ConfigurableBaseUrlPageLinks;
import gov.va.api.health.providerdirectory.service.controller.Validator;
import gov.va.api.health.stu3.api.resources.Practitioner;
import gov.va.api.health.stu3.api.resources.Practitioner.Bundle;
import javax.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.Test;

@SuppressWarnings("WeakerAccess")
public final class PractitionerControllerTest {
  PractitionerController.Transformer tx = new PractitionerTransformer();

  ConfigurableBaseUrlPageLinks configurableBaseUrlPageLinks =
      new ConfigurableBaseUrlPageLinks("", "");

  Bundler bundler = new Bundler(configurableBaseUrlPageLinks);

  PpmsClient ppmsClient = mock(PpmsClient.class);

  PractitionerController controller = new PractitionerController(tx, bundler, ppmsClient);

  @Test
  @SneakyThrows
  public void readByIdentifier() {
    ProviderResponse response =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/PractitionerTestResources/mock-provider-by-identifier-response.json"),
                ProviderResponse.class);
    ProviderContactsResponse contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/PractitionerTestResources/mock-provider-contact-response.json"),
                ProviderContactsResponse.class);
    when(ppmsClient.providersForId("identifier")).thenReturn(response);
    when(ppmsClient.providerContactsForId("1285621557")).thenReturn(contacts);
    Practitioner expected = controller.readByIdentifier("identifier");
    Practitioner actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/PractitionerTestResources/expected-read-by-identifier.json"),
                Practitioner.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByFamilyAndGiven() {
    ProviderResponse response =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/PractitionerTestResources/mock-provider-by-identifier-response.json"),
                ProviderResponse.class);
    ProviderContactsResponse contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/PractitionerTestResources/mock-provider-contact-response.json"),
                ProviderContactsResponse.class);
    when(ppmsClient.providersForName("Klingerman")).thenReturn(response);
    when(ppmsClient.providerContactsForId("1285621557")).thenReturn(contacts);
    Bundle expected = controller.searchByFamilyAndGiven("Klingerman", "Michael", 1, 1);
    Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/PractitionerTestResources/expected-search-by-family-and-given.json"),
                Practitioner.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByIdentifier() {
    ProviderResponse response =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/PractitionerTestResources/mock-provider-by-identifier-response.json"),
                ProviderResponse.class);
    ProviderContactsResponse contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/PractitionerTestResources/mock-provider-contact-response.json"),
                ProviderContactsResponse.class);
    when(ppmsClient.providersForId("identifier")).thenReturn(response);
    when(ppmsClient.providerContactsForId("1285621557")).thenReturn(contacts);
    Bundle expected = controller.searchByIdentifier("identifier", 1, 1);
    Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/PractitionerTestResources/expected-search-by-identifier.json"),
                Practitioner.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByName() {
    ProviderResponse response =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/PractitionerTestResources/mock-provider-by-identifier-response.json"),
                ProviderResponse.class);
    ProviderContactsResponse contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream(
                        "/PractitionerTestResources/mock-provider-contact-response.json"),
                ProviderContactsResponse.class);
    when(ppmsClient.providersForName("Klingerman, Michael")).thenReturn(response);
    when(ppmsClient.providerContactsForId("1285621557")).thenReturn(contacts);
    Bundle expected = controller.searchByName("Klingerman, Michael", 1, 1);
    Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/PractitionerTestResources/expected-search-by-name.json"),
                Practitioner.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void validateAcceptsValidBundle() {
    Bundle resource =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/PractitionerTestResources/expected-search-by-name.json"),
                Practitioner.Bundle.class);
    assertThat(controller.validate(resource)).isEqualTo(Validator.ok());
  }

  @SneakyThrows
  @Test(expected = ConstraintViolationException.class)
  public void validateThrowsExceptionForInvalidBundle() {
    Bundle resource =
        JacksonConfig.createMapper()
            .readValue(
                getClass()
                    .getResourceAsStream("/PractitionerTestResources/expected-search-by-name.json"),
                Practitioner.Bundle.class);
    resource.resourceType(null);
    controller.validate(resource);
  }
}
