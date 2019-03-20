package gov.va.api.health.providerdirectory.service.controller.capabilitystatement;

import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement;
import gov.va.api.health.providerdirectory.api.resources.CapabilityStatement.Status;
import gov.va.api.health.providerdirectory.service.controller.capabilitystatement.CapabilityStatementProperties.ContactProperties;
import gov.va.api.health.providerdirectory.service.controller.capabilitystatement.CapabilityStatementProperties.SecurityProperties;
import lombok.SneakyThrows;
import org.junit.Test;

public class MetadataControllerTest {
  @SneakyThrows
  private String pretty(CapabilityStatement conformance) {
    return JacksonConfig.createMapper()
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(conformance);
  }

  private CapabilityStatementProperties properties() {
    return CapabilityStatementProperties.builder()
        .id("health-api-argonaut-capability")
        .version("1.4.0")
        .name("API Management Platform | Health - Argonaut Provider Directory")
        .status(Status.draft)
        .publisher("Department of Veterans Affairs")
        .contact(
            ContactProperties.builder()
                .name("Jason Glanville")
                .email("Jason.Glanville@va.gov")
                .build())
        .publicationDate("2019-03-19T12:26:29Z")
        .description("Read and search support Argonaut Provider Directory.")
        .softwareName("provider-directory")
        .fhirVersion("1.0.2")
        .security(
            SecurityProperties.builder()
                .tokenEndpoint("https://fake.va.gov")
                .authorizeEndpoint("https://fake.va.gov")
                .description("http://docs.smarthealthit.org/")
                .build())
        .resourceDocumentation(
            "Implemented per specificaton. See http://hl7.org/fhir/STU3/http.html")
        .build();
  }

  @Test
  @SneakyThrows
  public void read() {
    CapabilityStatementProperties properties = properties();
    MetadataController controller = new MetadataController(properties);
    CapabilityStatement old =
        JacksonConfig.createMapper()
            .readValue(
                getClass().getResourceAsStream("/test-conformance.json"),
                CapabilityStatement.class);
    try {
      assertThat(pretty(controller.read())).isEqualTo(pretty(old));
    } catch (AssertionError e) {
      System.out.println(e.getMessage());
      throw e;
    }
  }
}
