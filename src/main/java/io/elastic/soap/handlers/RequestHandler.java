package io.elastic.soap.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import io.elastic.soap.compilers.model.SoapBodyDescriptor;
import io.elastic.soap.utils.Utils;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.json.JsonObject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

/**
 * Class handles JSON request (and its Java generic class representation) and marshals it to XML
 */
public class RequestHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

  /**
   * Marshalling {@code request} to {@link SOAPMessage} object
   *
   * @param request Java {@link Object} representation of SOAP request structure
   * @param soapBodyDescriptor {@link SoapBodyDescriptor} object for service WSDL
   * @param clazz The {@link Class} of the {@code request}
   * @return {@link SOAPMessage} representation of {@code request} object
   */
  public <T> SOAPMessage getSoapRequestMessage(final Object request,
      final SoapBodyDescriptor soapBodyDescriptor, final Class<T> clazz)
      throws SOAPException, IOException, JAXBException, ParserConfigurationException {
    final QName qName = new QName(soapBodyDescriptor.getRequestBodyNameSpace(),
        soapBodyDescriptor.getRequestBodyElementName());
    final JAXBElement<T> myRootElement = new JAXBElement<T>(qName, clazz, clazz.cast(request));

    final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .newDocument();
    final Marshaller marshaller = JAXBContext.newInstance(clazz).createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    marshaller.marshal(myRootElement, document);
    final SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
    final MimeHeaders headers = soapMessage.getMimeHeaders();
    headers.addHeader("SOAPAction", soapBodyDescriptor.getSoapAction());
    soapMessage.getSOAPBody().addDocument(document);
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    soapMessage.writeTo(outputStream);
    LOGGER.info("Request to SOAP service: {}",
        new String(outputStream.toByteArray(), StandardCharsets.UTF_8));
    return soapMessage;
  }

  /**
   * Deserialization {@code request} to {@link Object} object
   *
   * @param request Java {@link Object} representation of SOAP request structure
   * @param soapBodyDescriptor {@link SoapBodyDescriptor} object for service WSDL
   * @param clazz The {@link Class} of the {@code request} object
   * @return {@link Object} representation of {@link JsonObject} {@code request} object
   */
  public <T> T getRequestObject(final JsonObject request,
      final SoapBodyDescriptor soapBodyDescriptor, final Class<T> clazz) throws IOException {
    LOGGER.info("About to start deserialization JsonObject: {} to {} class", request,
        clazz.getSimpleName());
    final JaxbAnnotationModule module = new JaxbAnnotationModule();
    final ObjectMapper objectMapper = Utils.getConfiguredObjectMapper();
    objectMapper.registerModule(module);
    final T requestObject = objectMapper
        .readValue(request.getJsonObject(soapBodyDescriptor.getRequestBodyElementName()).toString(),
            clazz);
    LOGGER.info("Deserialization JsonObject: {} to {} class successfully done", request,
        clazz.getSimpleName());
    return requestObject;
  }
}
