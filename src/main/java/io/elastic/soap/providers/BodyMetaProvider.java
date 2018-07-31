package io.elastic.soap.providers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.elastic.api.DynamicMetadataProvider;
import io.elastic.soap.compilers.JaxbCompiler;
import io.elastic.soap.compilers.model.SoapBodyDescriptor;
import io.elastic.soap.utils.Utils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BodyMetaProvider implements DynamicMetadataProvider {

  private static final Logger logger = LoggerFactory.getLogger(BodyMetaProvider.class);

  private JsonSchema inSchema;
  private JsonSchema outSchema;
  private JsonElement out, in;

  public byte[] getMeta(JsonObject configuration) throws Throwable {

    String wsdlUrl = null;
    String binding = null;
    String operation = null;

    try {
      wsdlUrl = Utils.getWsdlUrl(configuration);
      binding = Utils.getBinding(configuration);
      operation = Utils.getOperation(configuration);
    } catch (NullPointerException npe) {
      logger.error("WSDL URL, Binding and Operation can not be empty.");
      throw new RuntimeException(npe);
    }
    logger.info("Got configuration: WSDL URL: {}, Binding: {}, Operation: {}", wsdlUrl, binding,
        operation);
    SoapBodyDescriptor soapBodyDescriptor = getSoapBodyDescriptor(wsdlUrl, binding, operation);

    try {
      JaxbCompiler.generateAndLoadJaxbStructure(wsdlUrl);
    } catch (Throwable throwable) {
      logger.error("Internal JAXB structure was failed to generate. Check logs");
      throw new RuntimeException(throwable);
    }

    String className = soapBodyDescriptor.getRequestBodyClassName();
    if (className == null) {
      throw new RuntimeException(
          "Either Element Name or Class Name (Type) of the XSD element MUST be not empty. "
              + "Validate an XSD schema and try again.");
    }
    logger.info("ClassName for the root element of the {} operation is {}", operation, className);
    return generateSchema(className, soapBodyDescriptor).getBytes();
  }

  /**
   * Class that calls external WSDL, parses it and generates JAXB structure, loading it into
   * classloader
   */
  public SoapBodyDescriptor getSoapBodyDescriptor(String wsdlUrl, String binding,
      String operation) {
    return JaxbCompiler.getSoapBodyDescriptor(wsdlUrl, binding, operation);
  }

  private String generateSchema(String className, SoapBodyDescriptor soapBodyDescriptor)
      throws JsonProcessingException {
    JsonParser parser = new JsonParser();
    ObjectMapper objectMapper = Utils.getConfiguredObjectMapper();
    try {
      JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(objectMapper);
      inSchema = schemaGen.generateSchema(Class.forName(className));
    } catch (JsonMappingException e) {
      logger.error("Could not map the Json to deserialize schema");
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      logger.error("{} class can not be found");
      throw new RuntimeException(e);
    }

    String inSchemaString = objectMapper.writeValueAsString(inSchema);
    com.google.gson.JsonObject root = parser.parse("{\"type\":\"object\",\"properties\":{}}")
        .getAsJsonObject();
    com.google.gson.JsonObject body = root.getAsJsonObject("properties");
    body.add(soapBodyDescriptor.getRequestBodyElementName(), parser.parse(inSchemaString));
    logger.info("Generated schema: {}", root);
    in = root;
    com.google.gson.JsonObject gson = new com.google.gson.JsonObject();
    gson.add("in", in);
    return gson.toString();
  }

  @Override
  public javax.json.JsonObject getMetaModel(javax.json.JsonObject configuration) {
    try {
      logger.info("Got configuration: {}", configuration.toString());
      return deserialize(getMeta(configuration));
    } catch (IOException e) {
      logger.error("IOException caught. Check the logs");
      throw new RuntimeException(e);
    } catch (Throwable throwable) {
      logger.error("Throwable caught. Check the logs");
      throw new RuntimeException(throwable);
    }
  }

  javax.json.JsonObject deserialize(byte[] content) throws IOException {
    try (ByteArrayInputStream bais = new ByteArrayInputStream(content);
        JsonReader reader = Json.createReader(bais)) {
      return reader.readObject();
    }
  }
}
