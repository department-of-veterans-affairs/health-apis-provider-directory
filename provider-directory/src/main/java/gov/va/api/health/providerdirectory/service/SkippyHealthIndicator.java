package gov.va.api.health.providerdirectory.service;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

@Service
public class SkippyHealthIndicator implements HealthIndicator {
  @Override
  public Health health() {
    return Health.up().withDetail("skipped", true).build();
  }
}
