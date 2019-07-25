package gov.va.api.health.providerdirectory.service;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public final class LocationWrapper {
  ProviderServicesResponse providerServicesResponse;

  CareSitesResponse careSitesResponse;

  ProviderResponse providerResponse;
}
