package gov.va.api.health.providerdirectory.service.controller.endpoint;

import gov.va.api.health.providerdirectory.service.AddressResponse;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class EndpointWrapper {
  AddressResponse addressResponse;
  //@Builder.Default ProviderResponse providerResponse = ProviderResponse.builder().build();
}
