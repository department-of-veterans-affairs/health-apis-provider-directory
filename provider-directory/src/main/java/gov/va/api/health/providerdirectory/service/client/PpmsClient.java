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
  public static final class BadRequest extends PpmsException {
    public BadRequest(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }

  /** The resource requested was not found. */
  public static final class NotFound extends PpmsException {
    public NotFound(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }

  /** The generic exception for working. */
  public static class PpmsException extends RuntimeException {
    public PpmsException(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }

    public PpmsException(String message) {
      super(message);
    }
  }

  /** An unspecified error occurred while performing a search. */
  public static final class SearchFailed extends PpmsException {
    public SearchFailed(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }
}
