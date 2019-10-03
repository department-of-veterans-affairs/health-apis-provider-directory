package gov.va.api.health.providerdirectory.service.client;

/** Exception client for Provider Directory integrations. */
public interface ExceptionClient {

  /** A request was malformed, such as missing required search parameters. */
  public static final class BadRequest extends ProviderDirectoryException {

    public BadRequest(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }

  /** The resource requested was not found. */
  public static final class NotFound extends ProviderDirectoryException {

    public NotFound(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }

  /** The generic exception for working. */
  public static class ProviderDirectoryException extends RuntimeException {

    public ProviderDirectoryException(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }

    public ProviderDirectoryException(String message) {
      super(message);
    }
  }

  /** An unspecified error occurred while performing a search. */
  public static final class SearchFailed extends ProviderDirectoryException {

    public SearchFailed(String message, Throwable cause) {
      super(message + ", " + cause.getMessage(), cause);
    }
  }
}
