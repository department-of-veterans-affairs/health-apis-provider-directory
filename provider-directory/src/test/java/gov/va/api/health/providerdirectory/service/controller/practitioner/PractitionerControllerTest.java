package gov.va.api.health.providerdirectory.service.controller.practitioner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.service.ProviderContacts;
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
  public void searchByFamilyAndGiven() {
    ProviderResponse response =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-by-identifier-response.json"),
                ProviderResponse.class);
    ProviderContacts contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-contact-response.json"),
                ProviderContacts.class);
    when(ppmsClient.providersForName("Klingerman, Michael")).thenReturn(response);
    when(ppmsClient.providerContactsForId("1285621557")).thenReturn(contacts);
    Bundle expected = controller.searchByFamilyAndGiven("Klingerman", "Michael", 1, 1);
    Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/test-search-by-family-and-given.json"),
                Practitioner.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByIdentifier() {
    ProviderResponse response =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-by-identifier-response.json"),
                ProviderResponse.class);
    ProviderContacts contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-contact-response.json"),
                ProviderContacts.class);
    when(ppmsClient.providersForId("identifier")).thenReturn(response);
    when(ppmsClient.providerContactsForId("1285621557")).thenReturn(contacts);
    Bundle expected = controller.searchByIdentifier("identifier", 1, 1);
    Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/test-search-by-identifier.json"),
                Practitioner.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void searchByName() {
    ProviderResponse response =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-by-identifier-response.json"),
                ProviderResponse.class);
    ProviderContacts contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-contact-response.json"),
                ProviderContacts.class);
    when(ppmsClient.providersForName("Klingerman, Michael")).thenReturn(response);
    when(ppmsClient.providerContactsForId("1285621557")).thenReturn(contacts);
    Bundle expected = controller.searchByName("Klingerman, Michael", 1, 1);
    Bundle actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/test-search-by-name.json"),
                Practitioner.Bundle.class);
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  public void validateAcceptsValidBundle() {
    Bundle resource =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/test-search-by-name.json"),
                Practitioner.Bundle.class);
    assertThat(controller.validate(resource)).isEqualTo(Validator.ok());
  }

  @SneakyThrows
  @Test(expected = ConstraintViolationException.class)
  public void validateThrowsExceptionForInvalidBundle() {
    Bundle resource =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/test-search-by-name.json"),
                Practitioner.Bundle.class);
    resource.resourceType(null);
    controller.validate(resource);
  }
}
