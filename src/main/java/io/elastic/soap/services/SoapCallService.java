package io.elastic.soap.services;

import io.elastic.soap.AppConstants;
import io.elastic.soap.compilers.JaxbCompiler;
import io.elastic.soap.compilers.model.SoapBodyDescriptor;
import io.elastic.soap.handlers.RequestHandler;
import io.elastic.soap.handlers.ResponseHandler;
import io.elastic.soap.utils.Utils;
import java.net.URL;
import javax.json.JsonObject;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class which controls all the process of retrieving input data, input configuration, its
 * unmarshalling to Java object and its marshalling to XML afterwards. As well as the opposite
 * process of unmarshalling the returned XML into Java object and marshalling it once again into
 * JSON.
 */
public class SoapCallService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SoapCallService.class);

  public JsonObject call(final JsonObject inputJsonObject, final JsonObject configuration)
      throws SOAPFaultException, Throwable {

    String wsdlUrl;
    String binding;
    String operation;

    try {
      wsdlUrl = Utils.getWsdlUrl(configuration);
      binding = Utils.getBinding(configuration);
      operation = Utils.getOperation(configuration);
    } catch (NullPointerException npe) {
      LOGGER.error("WSDL URL, Binding and Operation can not be empty.");
      throw new IllegalArgumentException("WSDL URL, Binding and Operation can not be empty.");
    }

    final SoapBodyDescriptor soapBodyDescriptor = JaxbCompiler
        .getSoapBodyDescriptor(wsdlUrl, binding, operation);
    LOGGER.info("Got SOAP Body Descriptor: {}", soapBodyDescriptor);
    LOGGER.info("Got WSDL URL: {}", wsdlUrl);

    JaxbCompiler.generateAndLoadJaxbStructure(wsdlUrl);

    final RequestHandler requestHandler = new RequestHandler();
    final Class requestClass = Class
        .forName(soapBodyDescriptor.getRequestBodyClassName());
    final Object requestObject = requestHandler
        .getRequestObject(inputJsonObject, soapBodyDescriptor, requestClass);
    final SOAPMessage requestSoapMessage = requestHandler
        .getSoapRequestMessage(requestObject, soapBodyDescriptor, requestClass);

    // Adding basic auth header if basic auth is enabled
    if (Utils.isBasicAuth(configuration)) {
      final String encodedAuthHeader = Utils.getBasicAuthHeader(configuration);
      LOGGER.info(
          "Basic authorization enabled. Base64 encoded auth header was generated from the credentials");
      requestSoapMessage.getMimeHeaders()
          .addHeader(AppConstants.AUTH_KEYWORD, encodedAuthHeader);
    }

    final URL endPoint = new URL(soapBodyDescriptor.getSoapEndPoint());

    LOGGER.info("About to start SOAP call...");
    final SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
    final SOAPConnection con = factory.createConnection();
    final SOAPMessage response = con.call(requestSoapMessage, endPoint);
    LOGGER.info("SOAP call successfully done");

    final ResponseHandler responseHandler = new ResponseHandler();
    final Class responseClass = Class
        .forName(soapBodyDescriptor.getResponseBodyClassName());
    final Object responseObject = responseHandler.getResponseObject(response, responseClass);
    final JsonObject jsonObject = responseHandler
        .getJsonObject(responseObject, soapBodyDescriptor);
    con.close();
    return jsonObject;
  }
}
