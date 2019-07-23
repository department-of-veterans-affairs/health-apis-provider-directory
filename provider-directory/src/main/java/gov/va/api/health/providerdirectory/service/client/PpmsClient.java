package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.providerdirectory.service.ProviderContactsResponse;
import gov.va.api.health.providerdirectory.service.ProviderResponse;
import gov.va.api.health.providerdirectory.service.ProviderSpecialtiesResponse;

/** This is the abstraction for communicating with PPMS through their Restful API. */
public interface PpmsClient {
  /** Return the parameters of the failed search. */
  ProviderContactsResponse providerContactsForId(String id);

  /** Return the parameters of the failed search. */
  ProviderSpecialtiesResponse providerSpecialtySearch(String id);

  /** Return the parameters of the failed search. */
  ProviderResponse providersForId(String id);

  ProviderResponse providersForName(String id);

  /** A request was malformed, such as missing required search parameters. */
  class BadRequest extends PpmsException {
    public BadRequest(String id) {
      super(id);
    }
  }

  /** The resource requested was not found. */
  class NotFound extends PpmsException {
    public NotFound(String id) {
      super(id);
    }
  }

  /** The generic exception for working. */
  class PpmsException extends RuntimeException {
    PpmsException(String id) {
      super(id);
    }
  }

  /** An unspecified error occurred while performing a search. */
  class SearchFailed extends PpmsException {
    public SearchFailed(String id) {
      super(id);
    }
  }
}
