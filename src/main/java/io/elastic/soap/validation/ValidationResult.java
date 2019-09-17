package io.elastic.soap.validation;

import io.elastic.soap.exceptions.ValidationException;

/**
 * Represents result of validation, if message is invalid contains exception with information about invalid fields.
 */
public class ValidationResult {

  private boolean result;
  private ValidationException exception;

  public ValidationResult() {
    result = true;
  }

  public ValidationResult(ValidationException e) {
    result = false;
    exception = e;
  }

  public boolean isResult() {
    return result;
  }

  public ValidationException getException() {
    return exception;
  }
}
