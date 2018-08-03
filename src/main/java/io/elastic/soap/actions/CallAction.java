package io.elastic.soap.actions;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.elastic.soap.services.SoapCallService;
import javax.json.JsonObject;
import javax.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action to make a SOAP call.
 */
public class CallAction implements Module {

  private static final Logger LOGGER = LoggerFactory.getLogger(CallAction.class);

  /**
   * Executes the io.elastic.soap.actions's logic by sending a request to the SOAP Service and
   * emitting response to the platform.
   *
   * @param parameters execution parameters
   */
  @Override
  public void execute(final ExecutionParameters parameters) {

    Message data;
    final Message message = parameters.getMessage();
    LOGGER.info("Input message: {}", message);

    final JsonObject body = message.getBody();
    final JsonObject configuration = parameters.getConfiguration();

    JsonObject outputBody = null;
    final SoapCallService soapCallService = new SoapCallService();
    try {
      outputBody = soapCallService.call(body, configuration);
    } catch (SOAPFaultException soapFaultException) {
      LOGGER.error("SOAP Fault has occurred. See the logs.");

      // emitting an exception
      parameters.getEventEmitter().emitException(soapFaultException);
    } catch (Throwable throwable) {
      LOGGER.error("Unexpected internal component error: {}", throwable.getMessage());
      throw new RuntimeException(throwable);
    }

    data = new Message.Builder().body(outputBody).build();
    LOGGER.info("Emitting data: {}", outputBody);

    // emitting the message to the platform
    parameters.getEventEmitter().emitData(data);
  }
}
