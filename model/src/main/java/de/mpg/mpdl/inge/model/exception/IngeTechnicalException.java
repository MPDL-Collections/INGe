package de.mpg.mpdl.inge.model.exception;

/**
 * Exception if a service fails
 * 
 * @author walter (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 * 
 */
public class IngeTechnicalException extends Exception {

  private static final long serialVersionUID = -2755845075749766737L;

  public IngeTechnicalException() {}

  public IngeTechnicalException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public IngeTechnicalException(final String message) {
    super(message);
  }

  public IngeTechnicalException(final Throwable cause) {
    super(cause);
  }

}
