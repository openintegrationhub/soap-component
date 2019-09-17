package io.elastic.soap.triggers;

import static io.elastic.soap.AppConstants.VALIDATION;
import static io.elastic.soap.AppConstants.VALIDATION_ENABLED;
import static io.elastic.soap.utils.Utils.configLogger;
import static io.elastic.soap.utils.Utils.loadClasses;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.elastic.soap.compilers.model.SoapBodyDescriptor;
import io.elastic.soap.exceptions.ComponentException;
import io.elastic.soap.utils.Utils;
import io.elastic.soap.validation.SOAPValidator;
import io.elastic.soap.validation.impl.WsdlSOAPValidator;
import javax.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trigger to get soap request.
 */
public class ReceiveRequest implements Module {

  static {
    configLogger();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveRequest.class);

  private SOAPValidator validator;

  private SoapBodyDescriptor soapBodyDescriptor;

  @Override
  public void init(JsonObject configuration) {
    try {
      LOGGER.info("On init started");
      soapBodyDescriptor = loadClasses(configuration, soapBodyDescriptor);
      validator = new WsdlSOAPValidator(soapBodyDescriptor.getRequestBodyClassName());
      LOGGER.info("On init finished");
    } catch (ComponentException e) {
      LOGGER.error("Error in init method", e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Error in init method", e);
      throw new ComponentException(e);
    }
  }

  /**
   * @param parameters execution parameters
   */
  @Override
  public void execute(final ExecutionParameters parameters) {
    try {
      final Message message = parameters.getMessage();
      final JsonObject configuration = parameters.getConfiguration();
      final JsonObject body = Utils.getSoapBody(message.getBody());
      LOGGER.info("Received new SOAP message, start processing");
      LOGGER.trace("Input configuration: {}", configuration);
      LOGGER.trace("Input body: {}", body);
      final Message data = new Message.Builder().body(body).build();
      if (VALIDATION.equals(configuration.getString(VALIDATION, VALIDATION_ENABLED))) {
        LOGGER.trace("Validation is required for SOAP message");
        validator.validate(body);
      }
      LOGGER.trace("Emitting data: {}", data);
      parameters.getEventEmitter().emitData(data);
      LOGGER.info("Finished processing SOAP message");
    } catch (ComponentException e) {
      LOGGER.error("Error in receive request trigger", e);
      throw e;
    } catch (Exception e) {
      LOGGER.error("Error in receive request trigger", e);
      throw new ComponentException(e);
    }
  }

  /**
   * For unit testing
   */
  public void setValidator(SOAPValidator validator) {
    this.validator = validator;
  }

}
