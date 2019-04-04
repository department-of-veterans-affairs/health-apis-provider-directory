package gov.va.api.health.providerdirectory.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class PpmsPractitionerRole {
  ProviderResponse providerResponse;

  ProviderContacts providerContacts;

  PpmsProviderSpecialtiesResponse providerSpecialtiesResponse;
}
