package gov.va.api.health.providerdirectory.tests;

import static gov.va.api.health.providerdirectory.tests.Requests.doGet;
import static gov.va.api.health.providerdirectory.tests.SystemDefinitions.systemDefinition;
import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import gov.va.api.health.r4.api.resources.Location;
import gov.va.api.health.sentinel.Environment;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class DataQueryIT {
  static Stream<Arguments> queries() {
    var ids = systemDefinition().publicIds();
    return Stream.of(
        arguments("r4/Location/" + ids.location(), 200),
        arguments("r4/Organization/" + ids.organization(), 200),
        arguments("r4/Practitioner/" + ids.practitioner(), 200),
        arguments("r4/PractitionerRole/" + ids.practitionerRole(), 200));
  }

  @BeforeAll
  static void setup() {
    assumeEnvironmentNotIn(Environment.LOCAL);
  }

  @Test
  void doNotReplaceNamingSystemUrl() {
    final String FHIR_STRING =
        "https://api.va.gov/services/fhir/v0/r4/NamingSystem/va-clinic-identifier";

    var ids = systemDefinition().publicIds();

    var locationResponse =
        doGet(null, "r4/Location/" + ids.location(), 200).expectValid(Location.class);
    assertThat(locationResponse.identifier().size()).isEqualTo(1);
    assertThat(locationResponse.identifier().get(0).system()).contains(FHIR_STRING);
  }

  @ParameterizedTest
  @MethodSource("queries")
  void routeAppropriateResourceToDataQuery(String request, int expectedStatus) {
    doGet(null, request, expectedStatus);
  }

  @Test
  void unsupportedResource() {
    doGet(null, "r4/Appointment?patient=1011537977V693883", 404);
  }
}
