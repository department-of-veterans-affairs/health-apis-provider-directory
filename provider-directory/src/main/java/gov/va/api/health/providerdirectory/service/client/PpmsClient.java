package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.providerdirectory.service.CareSitesResponse;
import gov.va.api.health.providerdirectory.service.ProviderServicesResponse;
import gov.va.api.health.providerdirectory.service.PpmsProviderSpecialtiesResponse;
import gov.va.api.health.providerdirectory.service.ProviderContacts;
import gov.va.api.health.providerdirectory.service.ProviderResponse;

/**
 * This is the abstraction for communicating with the Mr. Anderson service. This service works with
 * a JAXB model that represents the CDW schemas. Queries to Mr. Anderson are contained in type-safe
 * objects.
 */
public interface PpmsClient {

  CareSitesResponse careSitesByCity(String city);

  ProviderServicesResponse providerServicesById(String id);

  ProviderServicesResponse providerServicesByName(String name);

  CareSitesResponse careSitesByState(String state);

  CareSitesResponse careSitesByZip(String zip);

  /** Return the parameters of the failed search. */
  ProviderContacts providerContactsForId(String id);

  /** Return the parameters of the failed search. */
  PpmsProviderSpecialtiesResponse providerSpecialtySearch(String id);

  /** Return the parameters of the failed search. */
  ProviderResponse providersForId(String id);

  ProviderResponse providersForName(String id);

  /** A request to Mr. Anderson was malformed, such as missing required search parameters. */
  class BadRequest extends PpmsServiceException {

    public BadRequest(String id) {
      super(id);
    }
  }

  /** The generic exception for working with Mr. Anderson. */
  class PpmsServiceException extends RuntimeException {

    PpmsServiceException(String id) {
      super(id);
    }
  }

  /** The resource requested was not found. */
  class NotFound extends PpmsServiceException {

    public NotFound(String id) {
      super(id);
    }
  }

  /** An unspecified error occurred while performing a search. */
  class SearchFailed extends PpmsServiceException {

    public SearchFailed(String id) {
      super(id);
    }
  }
}
