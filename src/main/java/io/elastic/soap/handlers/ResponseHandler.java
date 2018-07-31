package io.elastic.soap.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.elastic.soap.compilers.model.SoapBodyDescriptor;
import io.elastic.soap.utils.Utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResponseHandler {

  private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

  /**
   * Unmarshalling  {@code response} {@link SOAPMessage} object to {@link T} object
   *
   * @param response {@link SOAPMessage} response from SOAP service
   * @param clazz The {@link Class} of the {@code response} object
   * @return {@link T} representation of {@link SOAPMessage} {@code response} object
   */
  public <T> T getResponseObject(SOAPMessage response, Class<T> clazz)
      throws JAXBException, SOAPException, IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    response.writeTo(bout);

    logger.info("Response message: {}", bout.toString("UTF-8"));
    logger.info("About to start unmarshalling response SoapMessage to {} class", clazz.getName());
    Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();
    JAXBElement<T> responseObject = unmarshaller
        .unmarshal(response.getSOAPBody().getFirstChild(), clazz);
    logger
        .info("Unmarshalling response SoapMessage to {} class successfully done", clazz.getName());
    return responseObject.getValue();
  }

  /**
   * Deserialization {@code response} {@link JsonObject} to {@link Object} object
   *
   * @param response Java {@link Object} representation of SOAP response structure
   * @return {@link JsonObject} representation of {@link Object} {@code request} object
   */
  public JsonObject getJsonObject(Object response, SoapBodyDescriptor soapBodyDescriptor)
      throws IOException {
    logger.info("About to start serialization response SoapMessage to JSON string");
    ObjectMapper mapper = Utils.getConfiguredObjectMapper();
    StringWriter sw = new StringWriter();
    mapper.writeValue(sw, response);
    logger.info("Serialization response SoapMessage to JSON string successfully done");
    logger.info("About to start parsing JSON string to {} class", JsonObject.class.getName());
    JsonReader jsonReader = Json
        .createReader(new StringReader(sw.toString()));
    JsonObject jsonObject = jsonReader.readObject();
    jsonReader.close();

    /*append body to root element*/
    JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add(soapBodyDescriptor.getResponseBodyElementName(), jsonObject);
    jsonObject = builder.build();

    logger.info("Parsing JSON string to {} class successfully done", JsonObject.class.getName());
    return jsonObject;
  }
}
