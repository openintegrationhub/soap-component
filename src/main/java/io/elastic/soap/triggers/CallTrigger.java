package io.elastic.soap.triggers;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.api.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;
import javax.json.JsonString;

/**
 * Trigger to get pets by status.
 */
public class CallTrigger implements Module {

  private static final Logger logger = LoggerFactory.getLogger(CallTrigger.class);

  /**
   * Executes the trigger's logic by sending a request to the Petstore API and emitting response to
   * the platform.
   *
   * @param parameters execution parameters
   */
  @Override
  public void execute(final ExecutionParameters parameters) {
    final JsonObject configuration = parameters.getConfiguration();

    // access the value of the status field defined in trigger's fields section of component.json
    final JsonString status = configuration.getJsonString("status");
    if (status == null) {
      throw new IllegalStateException("status field is required");
    }
    logger.info("About to find pets by status {}", status.getString());

    final String path = "/pet/findByStatus?status=" + status.getString();

    final Message data
        = new Message.Builder().body(configuration).build();

    logger.info("Emitting data");

    // emitting the message to the platform
    parameters.getEventEmitter().emitData(data);
  }
}