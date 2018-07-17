package providers;


import com.predic8.wsdl.Binding;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.elastic.api.JSON;
import io.elastic.api.SelectModelProvider;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.*;

public class OperationModelProvider implements SelectModelProvider {

  private static final Logger logger = LoggerFactory.getLogger(OperationModelProvider.class);

  @Override
  public JsonObject getSelectModel(final JsonObject configuration) {

    logger.info("input model configuration", JSON.stringify(configuration));
    String wsdlAddress = configuration.getJsonString("wsdlURI").getString();
    String bindingName = configuration.getJsonString("binding").getString();
    logger.info("input wsdl url {}", wsdlAddress);

    WSDLParser parser = new WSDLParser();
    Definitions defs = parser.parse(wsdlAddress);
    List<Binding> bindingList = defs.getBindings();
    final JsonObjectBuilder builder = Json.createObjectBuilder();
    bindingList.stream()
        .filter(binding -> binding.getName().equals(bindingName))
        .collect(Collectors.toList())
        .forEach(binding -> binding.getOperations()
            .forEach(
                bindingOperation -> builder
                    .add(bindingOperation.getName(), bindingOperation.getName())));
    return builder.build();
  }
}
