package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;

/** This is the abstraction for communicating with PPMS through their Restful API. */
public interface PpmsClient {

  CareSitesResponse careSitesByCity(String city);

  CareSitesResponse careSitesById(String id);

  CareSitesResponse careSitesByName(String zip);

  CareSitesResponse careSitesByState(String state);

  CareSitesResponse careSitesByZip(String zip);

  /** Return the parameters of the failed search. */
  ProviderContactsResponse providerContactsForId(String id);

  ProviderServicesResponse providerServicesById(String id);

  ProviderServicesResponse providerServicesByName(String name);

  /** Return the parameters of the failed search. */
  ProviderSpecialtiesResponse providerSpecialtySearch(String id);

  /** Return the parameters of the failed search. */
  ProviderResponse providersForId(String id);

  ProviderResponse providersForName(String id);
}
