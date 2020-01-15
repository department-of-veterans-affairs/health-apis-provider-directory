package gov.va.api.health.providerdirectory.service.controller.endpoint;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.Iterables;
import gov.va.api.health.providerdirectory.service.AddressResponse;
import gov.va.api.health.providerdirectory.service.client.VlerClient;
import gov.va.api.health.providerdirectory.service.controller.Bundler;
import gov.va.api.health.providerdirectory.service.controller.ConfigurableBaseUrlPageLinks;
import gov.va.api.health.providerdirectory.service.controller.Validator;
import gov.va.api.health.stu3.api.bundle.AbstractBundle;
import gov.va.api.health.stu3.api.bundle.AbstractEntry;
import gov.va.api.health.stu3.api.datatypes.CodeableConcept;
import gov.va.api.health.stu3.api.datatypes.Coding;
import gov.va.api.health.stu3.api.elements.Reference;
import gov.va.api.health.stu3.api.resources.Endpoint;
import javax.validation.ConstraintViolationException;
import org.junit.Test;

@SuppressWarnings("WeakerAccess")
public class EndpointControllerTest {

  VlerClient vlerClient = mock(VlerClient.class);

  EndpointController controller =
      new EndpointController(
          new EndpointTransformer(),
          new Bundler(new ConfigurableBaseUrlPageLinks("", "")),
          vlerClient);

  @Test
  public void readIdentifier() {
    when(vlerClient.endpointByAddress("test"))
        .thenReturn(
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
                .build());
    Endpoint actual = controller.readByIdentifier("test");
    assertThat(actual)
        .isEqualTo(
            Endpoint.builder()
                .resourceType("Endpoint")
                .id("test.pilot")
                .status(Endpoint.Status.active)
                .connectionType(Coding.builder().code("direct-project").build())
                .name("Pilot, Test")
                .payloadType(
                    asList(
                        CodeableConcept.builder()
                            .coding(
                                asList(
                                    Coding.builder()
                                        .code("VLER Direct")
                                        .display("VLER Direct")
                                        .build()))
                            .build()))
                .address("test.pilot@test2.direct.va.gov")
                .build());
  }

  @Test
  public void searchByIdentifier() {
    when(vlerClient.endpointByAddress("test"))
        .thenReturn(
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
                .build());
    Endpoint.Bundle actual = controller.searchByIdentifier("test", 1, 1);
    assertThat(Iterables.getOnlyElement(actual.entry()).resource())
        .isEqualTo(
            Endpoint.builder()
                .resourceType("Endpoint")
                .id("test.pilot")
                .status(Endpoint.Status.active)
                .connectionType(Coding.builder().code("direct-project").build())
                .name("Pilot, Test")
                .payloadType(
                    asList(
                        CodeableConcept.builder()
                            .coding(
                                asList(
                                    Coding.builder()
                                        .code("VLER Direct")
                                        .display("VLER Direct")
                                        .build()))
                            .build()))
                .address("test.pilot@test2.direct.va.gov")
                .build());
  }

  @Test
  public void searchByName() {
    when(vlerClient.endpointByAddress("test"))
        .thenReturn(
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
                .build());
    Endpoint.Bundle actual = controller.searchByName("test", 1, 1);
    assertThat(Iterables.getOnlyElement(actual.entry()).resource())
        .isEqualTo(
            Endpoint.builder()
                .resourceType("Endpoint")
                .id("test.pilot")
                .status(Endpoint.Status.active)
                .connectionType(Coding.builder().code("direct-project").build())
                .name("Pilot, Test")
                .payloadType(
                    asList(
                        CodeableConcept.builder()
                            .coding(
                                asList(
                                    Coding.builder()
                                        .code("VLER Direct")
                                        .display("VLER Direct")
                                        .build()))
                            .build()))
                .address("test.pilot@test2.direct.va.gov")
                .build());
  }

  @Test
  public void validate() {
    assertThat(
            controller.validate(
                Endpoint.Bundle.builder()
                    .resourceType("Bundle")
                    .type(AbstractBundle.BundleType.searchset)
                    .entry(
                        asList(
                            Endpoint.Entry.builder()
                                .fullUrl("http://fonzy.com/cool/Endpoint/bobnelson")
                                .resource(
                                    Endpoint.builder()
                                        .resourceType("Endpoint")
                                        .id("test.pilot")
                                        .status(Endpoint.Status.active)
                                        .connectionType(
                                            Coding.builder().code("direct-project").build())
                                        .name("Pilot, Test")
                                        .payloadType(
                                            asList(
                                                CodeableConcept.builder()
                                                    .coding(
                                                        asList(
                                                            Coding.builder()
                                                                .code("VLER Direct")
                                                                .display("VLER Direct")
                                                                .build()))
                                                    .build()))
                                        .address("test.pilot@test2.direct.va.gov")
                                        .managingOrganization(Reference.builder().build())
                                        .build())
                                .search(
                                    AbstractEntry.Search.builder()
                                        .mode(AbstractEntry.SearchMode.match)
                                        .build())
                                .build()))
                    .build()))
        .isEqualTo(Validator.ok());
  }

  @Test(expected = ConstraintViolationException.class)
  public void validateThrowsExceptionForInvalidBundle() {
    controller.validate(Endpoint.Bundle.builder().build());
  }
}
