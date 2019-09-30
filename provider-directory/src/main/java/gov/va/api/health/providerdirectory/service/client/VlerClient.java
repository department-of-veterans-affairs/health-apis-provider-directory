package gov.va.api.health.providerdirectory.service.client;

import gov.va.api.health.providerdirectory.service.AddressResponse;

/** This is the abstraction for communicating with PPMS through their Restful API. */
public interface VlerClient {

  AddressResponse endpointByAddress(String address);

  /** A request was malformed, such as missing required search parameters. */
  public static final class BadRequest extends VlerException {

    public BadRequest(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }

  /** The resource requested was not found. */
  public static final class NotFound extends VlerException {

    public NotFound(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }

  /** The generic exception for working. */
  public static class VlerException extends RuntimeException {

    public VlerException(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }

    public VlerException(String message) {
      super(message);
    }
  }

  /** An unspecified error occurred while performing a search. */
  public static final class SearchFailed extends VlerException {

    public SearchFailed(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }
}
