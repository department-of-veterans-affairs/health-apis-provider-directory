package gov.va.api.health.providerdirectory.service.controller.location;

import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class LocationWrapper {
  ProviderServicesResponse providerServicesResponse;

  CareSitesResponse careSitesResponse;

  ProviderResponse providerResponse;
}
