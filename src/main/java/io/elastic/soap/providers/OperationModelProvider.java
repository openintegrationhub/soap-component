package io.elastic.soap.providers;

import com.predic8.wsdl.Binding;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.elastic.api.JSON;
import io.elastic.api.SelectModelProvider;
import io.elastic.soap.utils.Utils;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides data for input Operation select box.
 * It is a list of operations available in the provided WSDL for a given Binding
 */

public class OperationModelProvider implements SelectModelProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(OperationModelProvider.class);

  @Override
  public JsonObject getSelectModel(final JsonObject configuration) {

    LOGGER.info("Input model configuration: {}", JSON.stringify(configuration));
    String wsdlUrl;
    String bindingName;
    try {
      bindingName = Utils.getBinding(configuration);
      wsdlUrl = Utils.getWsdlUrl(configuration);
    } catch (NullPointerException npe) {
      throw new RuntimeException("WSDL URL and Binding Name can not be empty");
    }
    LOGGER.info("Input wsdl url: {}, binding: {}", wsdlUrl, bindingName);

    final List<Binding> bindingList = getDefinitionsFromWsdl(wsdlUrl).getBindings();
    final JsonObjectBuilder builder = Json.createObjectBuilder();
    final String finalBindingName = bindingName;
    bindingList.stream()
        .filter(binding -> binding.getName().equals(finalBindingName))
        .collect(Collectors.toList())
        .forEach(binding -> binding.getOperations()
            .forEach(
                bindingOperation -> builder
                    .add(bindingOperation.getName(), bindingOperation.getName())));
    return builder.build();
  }

  /**
   * Method calls external WSDL by its URL and parses it
   *
   * @return {@link Definitions} object
   */
  public Definitions getDefinitionsFromWsdl(final String wsdlUrl) {
    final WSDLParser parser = new WSDLParser();
    return parser.parse(wsdlUrl);
  }
}
