package gov.va.api.health.providerdirectory.service.controller.practitionerrole;

import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public final class PractitionerRoleWrapper {
  ProviderResponse providerResponse;

  ProviderContactsResponse providerContactsResponse;

  ProviderSpecialtiesResponse providerSpecialtiesResponse;

  ProviderServicesResponse providerServicesResponse;
}
