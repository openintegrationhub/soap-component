package io.elastic.soap.utils;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.bind.api.impl.NameConverter;
import io.elastic.soap.AppConstants;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import javax.json.JsonObject;
import javax.json.JsonString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utils {

  private static final Logger logger = LoggerFactory.getLogger(Utils.class);

  // Private constructor to prevent instantiation. Since utility classes should not be instantiated
  private Utils() {
  }

  /**
   * Create configured ObjectMapper. All the marshall/unmarshal configurations should be added here
   * if needed
   *
   * @return ObjectMapper instance
   */
  public static ObjectMapper getConfiguredObjectMapper() {
    final ObjectMapper objectMapper = new ObjectMapper();

    objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

    // Possible features are coming if needed...

    return objectMapper;
  }

  /**
   * Method to get Binding from the message config.
   *
   * @param config {@link io.elastic.api.Message} configuration object.
   * @return String representation of the Binding.
   */
  public static String getBinding(final JsonObject config) {
    String binding = Utils.getConfigParam(config, AppConstants.BINDING_CONFIG_NAME);
    logger.info("Got '{}' binding from the config", binding);
    return binding;
  }

  /**
   * Method to get Operation from the message config.
   *
   * @param config {@link io.elastic.api.Message} configuration object.
   * @return String representation of the Operation.
   */
  public static String getOperation(final JsonObject config) {
    String operation = Utils.getConfigParam(config, AppConstants.OPERATION_CONFIG_NAME);
    logger.info("Got '{}' operation from the config", operation);
    return operation;
  }

  /**
   * Since many WSDL schemas have XSD elements started from the small letter (getBank), but WSImport
   * utility generates this class starting from a capital letter (GetBank), it should be manually
   * forced to start from the capital letter.
   */
  public static String getWithUpperFirstLetter(String elementName) {
    return elementName.substring(0, 1).toUpperCase() + elementName.substring(1);
  }

  /**
   * Method to get WSDL URL from the message config.
   *
   * @param config {@link io.elastic.api.Message} configuration object.
   * @return String representation of the WSDL URL.
   */
  public static String getWsdlUrl(final JsonObject config) {

    String wsdlUrl = Utils.getConfigParam(config, AppConstants.WSDL_CONFIG_NAME);

    String authType = config.getJsonObject("auth").getJsonString("type").getString();

    if (AppConstants.BASIC_AUTH_CONFIG_NAME.equals(authType)) {
      try {
        wsdlUrl = injectAuthStringIntoUrl(config);
      } catch (MalformedURLException e) {
        logger.error("{} url is not correct. Check the validity and try again", wsdlUrl);
        throw new RuntimeException(
            wsdlUrl + " url is not correct. Check the validity and try again");
      }
    }
    logger.info("Got '{}' WSDL URL from the config", wsdlUrl);
    return wsdlUrl;
  }

  /**
   * If auth type is 'Basic Auth', then we should modificate url in a way like this: https://host ->
   * https://username:password@host
   */
  public static String injectAuthStringIntoUrl(final JsonObject config)
      throws MalformedURLException {
    String wsdlUrl = Utils.getConfigParam(config, AppConstants.WSDL_CONFIG_NAME);
    URL url = new URL(wsdlUrl);
    String username, password;
    username = config.getJsonObject("auth").getJsonObject("basic").getString("username");
    password = config.getJsonObject("auth").getJsonObject("basic").getString("password");
    String host = url.getHost();
    return wsdlUrl.replace(host, username + ":" + password + "@" + host);
  }

  /**
   * Internal common method for getting value from the configuration {@link JsonObject} object.
   *
   * @param config Platform message's config. {@link JsonObject} type.
   * @param key String value of key to find the value of.
   * @return String value
   */
  private static String getConfigParam(final JsonObject config, final String key) {
    final JsonString value = config.getJsonString(key);

    if (value == null) {
      throw new IllegalStateException(String.format("Config parameter '%s' is required", key));
    }

    return value.getString();
  }

  /**
   * Internal common method for getting list of generated java classes {@link JsonObject} object.
   *
   * @param path path to root folder. {@link Path} type.
   * @return List of Path values
   */
  public static List<Path> listGeneratedFiles(String path) throws IOException {
    Path source = Paths.get(path);
    return Files.walk(source).filter(Files::isRegularFile)
        .filter(p -> !p.startsWith("src/main") && !p.startsWith("src/test"))
        .collect(Collectors.toList());
  }

  public static String convertToPackageName(String xmlNamespace) {
    NameConverter nameConverter = new NameConverter.Standard();
    return nameConverter.toPackageName(xmlNamespace);
  }
}

