package de.mpg.mpdl.inge.service.exceptions;

import de.mpg.mpdl.inge.model.exception.IngeTechnicalException;

@SuppressWarnings("serial")
public class IngeApplicationException extends Exception {

  public IngeApplicationException() {
    super();
    // TODO Auto-generated constructor stub
  }

  public IngeApplicationException(String message, Throwable cause) {
    super(message, cause);
    // TODO Auto-generated constructor stub
  }

  public IngeApplicationException(String message) {
    super(message);
    // TODO Auto-generated constructor stub
  }

  public IngeApplicationException(Throwable cause) {
    super(cause);
    // TODO Auto-generated constructor stub
  }

}
