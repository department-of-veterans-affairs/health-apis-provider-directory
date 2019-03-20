package gov.va.api.health.providerdirectory.service.controller.wellknown;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import gov.va.api.health.autoconfig.configuration.JacksonConfig;
import gov.va.api.health.providerdirectory.api.information.WellKnown;
import gov.va.api.health.providerdirectory.service.controller.capabilitystatement.CapabilityStatementProperties;
import lombok.SneakyThrows;
import org.junit.Test;

public class WellKnownControllerTest {

  private WellKnown actual() {
    return WellKnown.builder()
        .tokenEndpoint("https://fake.pd/token")
        .authorizationEndpoint("https://fake.pd/authorize")
        .capabilities(
            asList(
                "context-standalone-patient",
                "launch-standalone",
                "permission-offline",
                "permission-patient"))
        .responseTypeSupported(asList("code", "refresh-token"))
        .scopesSupported(
            asList("patient/DiagnosticReport.read", "patient/Patient.read", "offline_access"))
        .build();
  }

  private CapabilityStatementProperties conformanceProperties() {
    return CapabilityStatementProperties.builder()
        .security(
            CapabilityStatementProperties.SecurityProperties.builder()
                .authorizeEndpoint("https://fake.pd/authorize")
                .tokenEndpoint("https://fake.pd/token")
                .build())
        .build();
  }

  @SneakyThrows
  private String pretty(WellKnown wellKnown) {
    return JacksonConfig.createMapper()
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(wellKnown);
  }

  @Test
  @SneakyThrows
  public void read() {
    WellKnownController controller =
        new WellKnownController(wellKnownProperties(), conformanceProperties());
    try {
      assertThat(pretty(controller.read())).isEqualTo(pretty(actual()));
    } catch (AssertionError e) {
      System.out.println(e.getMessage());
      throw e;
    }
  }

  private WellKnownProperties wellKnownProperties() {
    return WellKnownProperties.builder()
        .capabilities(
            asList(
                "context-standalone-patient",
                "launch-standalone",
                "permission-offline",
                "permission-patient"))
        .responseTypeSupported(asList("code", "refresh-token"))
        .scopesSupported(
            asList("patient/DiagnosticReport.read", "patient/Patient.read", "offline_access"))
        .build();
  }
}
