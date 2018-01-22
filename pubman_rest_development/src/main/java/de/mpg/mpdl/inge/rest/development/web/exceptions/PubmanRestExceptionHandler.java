package de.mpg.mpdl.inge.rest.development.web.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.hateoas.VndErrors;
import org.springframework.hateoas.VndErrors.VndError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class PubmanRestExceptionHandler extends ResponseEntityExceptionHandler {

  private final static Logger logger = LogManager.getLogger(PubmanRestExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  VndErrors genreic(Exception exception) {
    logger.error("pech!", exception);
    if (exception.getMessage() != null) {
      VndErrors genericErrors = new VndErrors("500", exception.getClass().getCanonicalName() + ": " + exception.getMessage());
      genericErrors = addTheCause(genericErrors, exception);
      return genericErrors;
    } else {
      return new VndErrors("500", exception.getClass().getCanonicalName() + ": " + "no message available!");
    }
  }

  private VndErrors addTheCause(VndErrors errors, Throwable throwable) {
    Throwable cause = throwable;
    while (cause.getCause() != null) {
      cause = cause.getCause();
      if (cause.getMessage() != null) {
        errors.add(new VndError(cause.getClass().getCanonicalName(), cause.getMessage()));
      } else {
        errors.add(new VndError(cause.getClass().getCanonicalName(), "no message available!"));
      }
    }
    return errors;
  }

  @Override
  protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    Throwable mostSpecificCause = ex.getMostSpecificCause();
    VndError errorMessage;
    if (mostSpecificCause != null) {
      String exceptionName = mostSpecificCause.getClass().getName();
      String message = mostSpecificCause.getMessage();
      errorMessage = new VndError(exceptionName, message);
    } else {
      errorMessage = new VndError("cause?", ex.getMessage());
    }
    return new ResponseEntity<Object>(errorMessage, headers, status);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
    List<ObjectError> globalErrors = ex.getBindingResult().getGlobalErrors();
    List<VndError> errors = new ArrayList<VndError>(fieldErrors.size() + globalErrors.size());
    VndError error;
    for (FieldError fieldError : fieldErrors) {
      error = new VndError("invalid!!!", fieldError.getField() + ", " + fieldError.getDefaultMessage());
      errors.add(error);
    }
    for (ObjectError objectError : globalErrors) {
      error = new VndError("invalid!!!", objectError.getObjectName() + ", " + objectError.getDefaultMessage());
      errors.add(error);
    }
    VndErrors errorMessage = new VndErrors(errors);
    return new ResponseEntity<Object>(errorMessage, headers, status);
  }

}
