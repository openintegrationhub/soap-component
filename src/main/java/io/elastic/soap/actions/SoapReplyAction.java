package io.elastic.soap.actions;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.Module;
import javax.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoapReplyAction  implements Module {
  private static final Logger LOGGER = LoggerFactory.getLogger(SoapReplyAction.class);

  @Override
  public void execute(ExecutionParameters parameters) {
    JsonObject headers = parameters.getMessage().getHeaders();
    LOGGER.trace("Message headers: {}", headers);
  }
}
