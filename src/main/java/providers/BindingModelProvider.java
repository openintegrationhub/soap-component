package providers;

import com.predic8.wsdl.Binding;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.elastic.api.JSON;
import io.elastic.api.SelectModelProvider;
import java.util.List;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
