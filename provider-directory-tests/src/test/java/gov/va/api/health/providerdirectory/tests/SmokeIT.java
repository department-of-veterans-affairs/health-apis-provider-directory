package gov.va.api.health.providerdirectory.tests;

import static gov.va.api.health.providerdirectory.tests.Requests.doGet;
import static gov.va.api.health.sentinel.EnvironmentAssumptions.assumeEnvironmentNotIn;

import gov.va.api.health.sentinel.Environment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SmokeIT {
  @BeforeAll
  static void setup() {
    assumeEnvironmentNotIn(Environment.LOCAL);
  }

  @Test
  void healthCheckIsUnprotected() {
    doGet(null, "health", 200);
  }
}
