package gov.va.api.health.providerdirectory.tests;

import static gov.va.api.health.providerdirectory.tests.Requests.doGet;
import static gov.va.api.health.providerdirectory.tests.Requests.doGet;
import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import gov.va.api.health.sentinel.Environment;
import gov.va.api.health.sentinel.ServiceDefinition;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class DataQueryIT {
  //  SystemDefinition def = SystemDefinitions.systemDefinition();
  //  ServiceDefinition r4 = def.r4();
  //  String apiPath = r4.apiPath();

  @BeforeAll
  static void setup() {
    assumeEnvironmentNotIn(Environment.LOCAL);
  }

  static Stream<Arguments> resourceQueries() {
    var testIds = SystemDefinitions.systemDefinition().publicIds();
    return Stream.of(arguments("Location/" + testIds.location(), 200));

    //        arguments("Condition?patient=" + testIds.patient(), 200),
    //        arguments("Patient/" + testIds.patient(), 200),
    //        arguments("Practitioner/" + testIds.practitioner(), 200)

    // Location, Organization, Practitioner, PractitionerRole
  }

  @ParameterizedTest
  @MethodSource("resourceQueries")
  void routeAppropriateResourceToDataQuery(String request, int expectedStatus) {
    doGet(null, request, expectedStatus);
  }

  @Test
  void unsupportedResource() {
    doGet(null, "Appointment?patient=1011537977V693883", 404);
  }
}
