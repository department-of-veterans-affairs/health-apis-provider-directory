package gov.va.api.health.providerdirectory.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class ProviderWrapper {
  ProviderResponse providerResponse;

  ProviderContacts providerContacts;
}
