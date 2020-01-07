package gov.va.api.health.providerdirectory.service.client;

import lombok.experimental.UtilityClass;

/** Exception client for Provider Directory integrations. */
@UtilityClass
public final class Exceptions {
  /** A request was malformed, such as missing required search parameters. */
  public static final class BadRequest extends RuntimeException {
    public BadRequest(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }

  /** The resource requested was not found. */
  public static final class NotFound extends RuntimeException {
    public NotFound(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }

  /** An unspecified error occurred while performing a search. */
  public static final class SearchFailed extends RuntimeException {
    public SearchFailed(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }

  /** Generic VLER exception. */
  public static class VlerException extends RuntimeException {
    public VlerException(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }

    public VlerException(String message) {
      super(message);
    }
  }
}
