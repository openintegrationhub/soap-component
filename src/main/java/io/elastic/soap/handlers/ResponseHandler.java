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
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class handles XML response and unmarshals it to specified Java type
 */
public class ResponseHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);

  /**
   * Unmarshalling  {@code response} {@link SOAPMessage} object to {@link T} object
   *
   * @param response {@link SOAPMessage} response from SOAP service
   * @param clazz The {@link Class} of the {@code response} object
   * @return {@link T} representation of {@link SOAPMessage} {@code response} object
   */
  public <T> T getResponseObject(final SOAPMessage response, final Class<T> clazz)
      throws JAXBException, SOAPException, IOException, SOAPFaultException {
    final ByteArrayOutputStream bout = new ByteArrayOutputStream();
    response.writeTo(bout);

    LOGGER.info("Response message: {}", bout.toString("UTF-8"));

    // Check if the response is a soap:Fault and throw an exception if so
    SOAPFault soapFault = response.getSOAPBody().getFault();
    if (soapFault != null) {
      throw new SOAPFaultException(soapFault);
    }

    LOGGER.info("About to start unmarshalling response SoapMessage to {} class", clazz.getName());
    final Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller();

    final JAXBElement<T> responseObject = unmarshaller
        .unmarshal(response.getSOAPBody().getFirstChild(), clazz);
    LOGGER
        .info("Unmarshalling response SoapMessage to {} class successfully done", clazz.getName());
    return responseObject.getValue();
  }

  /**
   * Deserialization {@code response} {@link JsonObject} to {@link Object} object
   *
   * @param response Java {@link Object} representation of SOAP response structure
   * @return {@link JsonObject} representation of {@link Object} {@code request} object
   */
  public JsonObject getJsonObject(final Object response,
      final SoapBodyDescriptor soapBodyDescriptor) throws IOException {
    LOGGER.info("About to start serialization response SoapMessage to JSON string");
    final ObjectMapper mapper = Utils.getConfiguredObjectMapper();
    final StringWriter sw = new StringWriter();
    mapper.writeValue(sw, response);
    LOGGER.info("Serialization response SoapMessage to JSON string successfully done");
    LOGGER.info("About to start parsing JSON string to {} class", JsonObject.class.getName());
    final JsonReader jsonReader = Json
        .createReader(new StringReader(sw.toString()));
    JsonObject jsonObject = jsonReader.readObject();
    jsonReader.close();

    /*append body to root element*/
    final JsonObjectBuilder builder = Json.createObjectBuilder();
    builder.add(soapBodyDescriptor.getResponseBodyElementName(), jsonObject);
    jsonObject = builder.build();

    LOGGER.info("Parsing JSON string to {} class successfully done", JsonObject.class.getName());
    return jsonObject;
  }
}

