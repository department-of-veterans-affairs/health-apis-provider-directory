package gov.va.api.health.providerdirectory.tests;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public final class TestIds {
  @NonNull String patient;
  @NonNull String practitioner;
}
