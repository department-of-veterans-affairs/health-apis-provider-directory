package gov.va.api.health.providerdirectory.service.controller.practitioner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.api.resources.Practitioner;
import gov.va.api.health.providerdirectory.api.resources.Practitioner.Bundle;
import gov.va.api.health.providerdirectory.service.ProviderContacts;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.client.PpmsClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.ConfigurableBaseUrlPageLinks;
import gov.va.api.health.providerdirectory.service.controller.Validator;
import javax.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("WeakerAccess")
public class PractitionerControllerTest {

  Bundle expected;

  Bundle actual;

  RestTemplate restTemplate;

  PractitionerController.Transformer tx = new PractitionerTransformer();

  PractitionerController controller;

  @Value("${ppms.url}")
  String baseUrl;

  ConfigurableBaseUrlPageLinks configurableBaseUrlPageLinks =
      new ConfigurableBaseUrlPageLinks("", "");

  Bundler bundler = new Bundler(configurableBaseUrlPageLinks);

  @Mock PpmsClient ppmsClient;

  @Before
  public void _init() {
    MockitoAnnotations.initMocks(this);
    controller = new PractitionerController(baseUrl, restTemplate, tx, bundler, ppmsClient);
  }

  @Test
  @SneakyThrows
  public void searchByFamilyAndGiven() {
    ProviderResponse response;
    ProviderContacts contacts;
    response =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-by-identifier-response.json"),
                ProviderResponse.class);
    contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-contact-response.json"),
                ProviderContacts.class);
    when(ppmsClient.providerResponseSearch("Klingerman, Michael", false)).thenReturn(response);
    when(ppmsClient.providerContactsSearch("1285621557")).thenReturn(contacts);
    expected = controller.searchByFamilyAndGiven("Klingerman", "Michael", 1, 1);
    actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/test-search-by-family-and-given.json"),
                Practitioner.Bundle.class);
    assert (actual.equals(expected));
  }

  @Test
  @SneakyThrows
  public void searchByIdentifier() {
    ProviderResponse response;
    ProviderContacts contacts;
    response =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-by-identifier-response.json"),
                ProviderResponse.class);
    contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-contact-response.json"),
                ProviderContacts.class);
    when(ppmsClient.providerResponseSearch("identifier", true)).thenReturn(response);
    when(ppmsClient.providerContactsSearch("1285621557")).thenReturn(contacts);
    expected = controller.searchByIdentifier("identifier", 1, 1);
    actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/test-search-by-identifier.json"),
                Practitioner.Bundle.class);
    assert (actual.equals(expected));
  }

  @Test
  @SneakyThrows
  public void searchByName() {
    ProviderResponse response;
    ProviderContacts contacts;
    response =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-by-identifier-response.json"),
                ProviderResponse.class);
    contacts =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/ppms-provider-contact-response.json"),
                ProviderContacts.class);
    when(ppmsClient.providerResponseSearch("Klingerman, Michael", false)).thenReturn(response);
    when(ppmsClient.providerContactsSearch("1285621557")).thenReturn(contacts);
    expected = controller.searchByName("Klingerman, Michael", 1, 1);
    actual =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/test-search-by-name.json"),
                Practitioner.Bundle.class);
    assert (actual.equals(expected));
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

  @Test(expected = ConstraintViolationException.class)
  @SneakyThrows
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
