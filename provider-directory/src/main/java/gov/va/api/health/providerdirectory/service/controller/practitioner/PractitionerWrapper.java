package gov.va.api.health.providerdirectory.service.controller.practitioner;

import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class PractitionerWrapper {
  ProviderResponse providerResponse;

  ProviderContactsResponse providerContactsResponse;
}
