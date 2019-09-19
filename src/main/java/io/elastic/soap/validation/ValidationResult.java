package io.elastic.soap.validation;

import io.elastic.soap.exceptions.ValidationException;
import org.w3c.dom.Document;

/**
 * Represents result of validation, if message is invalid contains exception with information about invalid fields.
 */
public class ValidationResult {

  private boolean result;
  private ValidationException exception;
  private Document resultXml;

  public ValidationResult() {
    result = true;
  }

  public ValidationResult(Document resultXml) {
    result = true;
    this.resultXml = resultXml;
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

  public Document getResultXml() {
    return resultXml;
  }
}
