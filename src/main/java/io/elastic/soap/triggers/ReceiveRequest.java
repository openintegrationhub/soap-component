package io.elastic.soap.triggers;

import static io.elastic.soap.utils.Utils.configLogger;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.elastic.soap.utils.Utils;
import javax.json.Json;
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

  /**
   * @param parameters execution parameters
   */
  @Override
  public void execute(final ExecutionParameters parameters) {
    final Message message = parameters.getMessage();
    LOGGER.trace("Input message: {}", message);

    final JsonObject configuration = parameters.getConfiguration();


    final JsonObject body = Utils.getSoapBody(message.getBody());

    final Message data = new Message.Builder().body(body).build();
    LOGGER.trace("Emitting data: {}", data);

    parameters.getEventEmitter().emitData(data);
  }
}
