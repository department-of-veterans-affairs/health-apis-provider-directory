package gov.va.api.health.providerdirectory.service;

import lombok.Builder;

@Builder
public final class PpmsPractitionerRole {
  ProviderResponse providerResponse;

  PpmsProviderSpecialtiesResponse providerSpecialtiesResponse;
}
