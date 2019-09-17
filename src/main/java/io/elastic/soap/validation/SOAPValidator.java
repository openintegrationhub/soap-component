package io.elastic.soap.validation;

import javax.json.JsonObject;

/**
 * Abstract class represents validator for SOAP message
 */
public abstract class SOAPValidator {

  protected final Class clazz;

  /**
   * @param className input/output class of SOAP operation
   */
  public SOAPValidator(final String className) throws ClassNotFoundException {
    this.clazz = Class.forName(className);
  }

  /**
   * @param message input message that validated by validator, usually received from action/trigger
   * @return result of validation
   */
  public abstract ValidationResult validate(final JsonObject message);

}
