package io.elastic.soap.triggers;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.elastic.soap.services.SoapCallService;
import javax.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Trigger to get pets by status.
 */
public class CallTrigger implements Module {

  private static final Logger logger = LoggerFactory.getLogger(CallTrigger.class);

  /**
   * Executes the io.elastic.soap.actions's logic by sending a request to the SOAP Service and
   * emitting response to the platform.
   *
   * @param parameters execution parameters
   */
  @Override
  public void execute(final ExecutionParameters parameters) {
    final Message message = parameters.getMessage();
    logger.info("Input message: {}", message);

    final JsonObject body = message.getBody();
    final JsonObject configuration = parameters.getConfiguration();

    JsonObject outputBody = null;
    SoapCallService soapCallService = new SoapCallService();
    try {
      outputBody = soapCallService.call(body, configuration);
    } catch (Throwable throwable) {
      logger.error("Unexpected internal component error: {}", throwable.getMessage());
      throw new RuntimeException(throwable);
      //throw new RuntimeException("Unexpected internal component error: " + throwable.getMessage());
    }

    final Message data = new Message.Builder().body(outputBody).build();
    logger.info("Emitting data: {}", outputBody);

    // emitting the message to the platform
    parameters.getEventEmitter().emitData(data);
  }
}
