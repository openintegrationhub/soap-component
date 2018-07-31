package io.elastic.soap.services;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoapCallService {

  private static final Logger logger = LoggerFactory.getLogger(SoapCallService.class);

  public JsonObject call(JsonObject inputJsonObject, JsonObject configuration) throws Throwable {

    String wsdlUrl = null;
    String binding = null;
    String operation = null;
    try {
      wsdlUrl = Utils.getWsdlUrl(configuration);
      binding = Utils.getBinding(configuration);
      operation = Utils.getOperation(configuration);
    } catch (NullPointerException npe) {
      logger.error("WSDL URL, Binding and Operation can not be empty.");
      throw new IllegalArgumentException("WSDL URL, Binding and Operation can not be empty.");
    }

    SoapBodyDescriptor soapBodyDescriptor = JaxbCompiler
        .getSoapBodyDescriptor(wsdlUrl, binding, operation);
    logger.info("Got SOAP Body Descriptor: {}", soapBodyDescriptor);
    logger.info("Got WSDL URL: {}", wsdlUrl);

    JaxbCompiler.generateAndLoadJaxbStructure(wsdlUrl);

    RequestHandler requestHandler = new RequestHandler();
    Class requestClass = Class
        .forName(soapBodyDescriptor.getRequestBodyClassName());
    Object requestObject = requestHandler
        .getRequestObject(inputJsonObject, soapBodyDescriptor, requestClass);
    SOAPMessage requestSoapMessage = requestHandler
        .getSoapRequestMessage(requestObject, soapBodyDescriptor, requestClass);
    URL endPoint = new URL(soapBodyDescriptor.getSoapEndPoint());

    logger.info("About to start SOAP call...");
    SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
    SOAPConnection con = factory.createConnection();
    SOAPMessage response = con.call(requestSoapMessage, endPoint);
    logger.info("SOAP call successfully done");

    ResponseHandler responseHandler = new ResponseHandler();
    Class responseClass = Class
        .forName(soapBodyDescriptor.getResponseBodyClassName());
    Object responseObject = responseHandler.getResponseObject(response, responseClass);
    JsonObject jsonObject = responseHandler.getJsonObject(responseObject, soapBodyDescriptor);
    con.close();
    return jsonObject;
  }
}
