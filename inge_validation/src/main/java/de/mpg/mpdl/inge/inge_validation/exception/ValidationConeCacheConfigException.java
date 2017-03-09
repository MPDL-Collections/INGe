package de.mpg.mpdl.inge.inge_validation.exception;

@SuppressWarnings("serial")
public class ValidationConeCacheConfigException extends Exception {

  public ValidationConeCacheConfigException() {}

  public ValidationConeCacheConfigException(String message) {
    super(message);
  }

  public ValidationConeCacheConfigException(Throwable cause) {
    super(cause);
  }

  public ValidationConeCacheConfigException(String message, Throwable cause) {
    super(message, cause);
  }
}
