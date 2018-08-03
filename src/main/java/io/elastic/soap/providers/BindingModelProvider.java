package io.elastic.soap.providers;

import com.predic8.wsdl.Binding;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.elastic.api.JSON;
import io.elastic.api.SelectModelProvider;
import io.elastic.soap.AppConstants;
import io.elastic.soap.utils.Utils;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides data for input Binding select box.
 * It is a list of bindings available in the provided WSDL
 */
public class BindingModelProvider implements SelectModelProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(BindingModelProvider.class);

  @Override
  public JsonObject getSelectModel(final JsonObject configuration) {
    LOGGER.info("Input model configuration: {}", JSON.stringify(configuration));
    String wsdlUrl;
    try {
      wsdlUrl = Utils.getWsdlUrl(configuration);
    } catch (NullPointerException npe) {
      throw new RuntimeException("WSDL URL can not be empty");
    }

    final List<Binding> bindingList = getDefinitionsFromWsdl(wsdlUrl).getBindings();
    final JsonObjectBuilder builder = Json.createObjectBuilder();
    bindingList.stream().filter(
        binding ->
            binding.getProtocol().equals(AppConstants.SOAP11_PROTOCOL_NAME) ||
                binding.getProtocol().equals(AppConstants.SOAP12_PROTOCOL_NAME))
        .collect(Collectors.toList())
        .forEach(binding -> builder.add(binding.getName(), binding.getName()));

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
