package gov.va.api.health.providerdirectory.tests;

import static gov.va.api.health.providerdirectory.tests.Requests.doGet;
import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;

import gov.va.api.health.sentinel.Environment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
public class SmokeIT {
  // SystemDefinitions.SystemDefinition def = SystemDefinitions.systemDefinition();

  @BeforeAll
  static void setup() {
    assumeEnvironmentNotIn(Environment.LOCAL);
  }

  @Test
  void healthCheckIsUnprotected() {
    doGet(null, "health", 200);
  }
}
