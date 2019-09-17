package io.elastic.soap.validation.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.elastic.soap.exceptions.ComponentException;
import io.elastic.soap.exceptions.ValidationException;
import io.elastic.soap.utils.Utils;
import io.elastic.soap.validation.SOAPValidator;
import io.elastic.soap.validation.ValidationResult;
import javax.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validate SOAP message over WSDL.
 */
public class WsdlSOAPValidator extends SOAPValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(WsdlSOAPValidator.class);

  public WsdlSOAPValidator(final String clazzName) throws ClassNotFoundException {
    super(clazzName);
  }

  /**
   * @param message input message that validated by validator, usually received from action/trigger
   * @return result of validation
   */
  @Override
  public ValidationResult validate(final JsonObject message) {
    try {
      LOGGER.trace("Starting message validation");
      final ObjectMapper mapper = Utils.getConfiguredObjectMapper();
      mapper.readValue(message.toString(), clazz);
      LOGGER.trace("Successful finished message validation");
      return new ValidationResult();
    } catch (JsonParseException | JsonMappingException e) {
      LOGGER.error("Failed to validate message", e);
      return new ValidationResult(new ValidationException(e.getLocation().toString(), e));
    } catch (Exception e) {
      LOGGER.error("Failed to validate message", e);
      throw new ComponentException(e);
    }
  }
}
