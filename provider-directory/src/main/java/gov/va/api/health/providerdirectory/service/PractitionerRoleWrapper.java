package gov.va.api.health.providerdirectory.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class PractitionerRoleWrapper {
  ProviderResponse providerResponse;

  ProviderContactsResponse providerContactsResponse;

  ProviderSpecialtiesResponse providerSpecialtiesResponse;
}
