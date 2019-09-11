package io.elastic.soap.actions;

import static io.elastic.soap.utils.Utils.loadClasses;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.HttpReply;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.elastic.soap.compilers.model.SoapBodyDescriptor;
import io.elastic.soap.handlers.RequestHandler;
import io.elastic.soap.utils.Utils;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SoapReplyAction implements Module {
  static {
    Utils.configLogger();
  }

  private static final Logger LOGGER = LoggerFactory.getLogger(SoapReplyAction.class);

  public static final String HEADER_CONTENT_TYPE = "Content-Type";
  public static final String HEADER_ROUTING_KEY = "X-EIO-Routing-Key";
  public static final String CONTENT_TYPE = "text/xml";
  private SoapBodyDescriptor soapBodyDescriptor;


  @Override
  public void init(JsonObject configuration) {
    LOGGER.info("On init started");
    soapBodyDescriptor = loadClasses(configuration, soapBodyDescriptor);
    LOGGER.info("On init finished");
  }


  @Override
  public void execute(ExecutionParameters parameters) {
    JsonObject headers = parameters.getMessage().getHeaders();
    JsonObject body = parameters.getMessage().getBody();
    JsonObject configuration = parameters.getConfiguration();
    Message inputMsg = parameters.getMessage();

    LOGGER.trace("Input configuration: {}", configuration);
    LOGGER.trace("Input headers: {}", headers);
    LOGGER.trace("Input body: {}", body);

    String replyTo = inputMsg.getHeaders().get("reply_to") != null ? inputMsg.getHeaders().getString("reply_to") : null;

    // Don't emit this message when running sample data
    if (null == replyTo) {
      return;
    }

//    SoapBodyDescriptor soapBodyDescriptor = JaxbCompiler
//        .getSoapBodyDescriptor(configuration.getString("wsdlURI"), configuration.getString("binding"), configuration.getString("operation"));
//
//    if (null != body.getJsonObject(soapBodyDescriptor.getResponseBodyElementName())) {
//      outputBody = body;
//    }

    JsonObject outputHeaders = Json.createObjectBuilder()
        .add(HEADER_ROUTING_KEY, replyTo)
        .add(HEADER_CONTENT_TYPE, CONTENT_TYPE)
        .build();
    try {

      RequestHandler requestHandler = new RequestHandler();
      Object response = requestHandler.getResponseObject(body, soapBodyDescriptor, Class.forName(soapBodyDescriptor.getResponseBodyClassName()));
      SOAPMessage message = requestHandler.getSoapRequestMessage(response, soapBodyDescriptor, Class.forName(soapBodyDescriptor.getResponseBodyClassName()));
      PipedInputStream in = new PipedInputStream();
      final PipedOutputStream outputStream = new PipedOutputStream(in);
      message.writeTo(outputStream);
      HttpReply httpReply = new HttpReply.Builder()
          .content(in)
          .header(HEADER_ROUTING_KEY, replyTo)
          .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
          .build();
      parameters.getEventEmitter().emitHttpReply(httpReply);
    } catch (IOException | ClassNotFoundException | SOAPException | JAXBException | ParserConfigurationException e) {
      e.printStackTrace();
    }
  }
}
