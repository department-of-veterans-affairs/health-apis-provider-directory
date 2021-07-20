package gov.va.api.health.providerdirectory.tests;

import gov.va.api.health.sentinel.ServiceDefinition;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public final class SystemDefinition {
  @NonNull ServiceDefinition internal;

  @NonNull ServiceDefinition r4;

  @NonNull TestIds publicIds;

  boolean isVfqAvailable;

  boolean isDqAvailable;
}
