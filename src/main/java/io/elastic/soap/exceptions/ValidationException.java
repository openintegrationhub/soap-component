package io.elastic.soap.exceptions;

public class ValidationException extends ComponentException {

  public ValidationException(Throwable throwable) {
    super(throwable);
  }

  public ValidationException(String s, Throwable throwable) {
    super(s, throwable);
  }
}
