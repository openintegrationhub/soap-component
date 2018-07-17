package io.elastic.soap.providers;

import io.elastic.api.JSON;
import io.elastic.api.SelectModelProvider;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.predic8.wsdl.*;


import javax.json.*;

public class BindingModelProvider implements SelectModelProvider {

  private static final Logger logger = LoggerFactory.getLogger(BindingModelProvider.class);

  @Override
  public JsonObject getSelectModel(final JsonObject configuration) {

    logger.info("input model configuration", JSON.stringify(configuration));
    String wsdlAddress = configuration.getJsonString("wsdlURI").getString();
    logger.info("input wsdl url {}", wsdlAddress);

    WSDLParser parser = new WSDLParser();
    Definitions defs = parser.parse(wsdlAddress);
    List<Binding> bindingList = defs.getBindings();
    final JsonObjectBuilder builder = Json.createObjectBuilder();
    bindingList.forEach(binding -> builder.add(binding.getName(), binding.getName()));
    return builder.build();
  }
}
