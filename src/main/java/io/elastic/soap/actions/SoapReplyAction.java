package io.elastic.soap.actions;

import static io.elastic.soap.AppConstants.VALIDATION;
import static io.elastic.soap.AppConstants.VALIDATION_ENABLED;
import static io.elastic.soap.utils.Utils.loadClasses;

import io.elastic.api.ExecutionParameters;
import io.elastic.api.HttpReply;
import io.elastic.api.Message;
import io.elastic.api.Module;
import io.elastic.soap.compilers.model.SoapBodyDescriptor;
import io.elastic.soap.exceptions.ComponentException;
import io.elastic.soap.handlers.RequestHandler;
import io.elastic.soap.utils.Utils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javax.json.Json;
import javax.json.JsonObject;
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
    try {
      JsonObject headers = parameters.getMessage().getHeaders();
      JsonObject inputBody = parameters.getMessage().getBody();
      JsonObject configuration = parameters.getConfiguration();
      Message inputMsg = parameters.getMessage();
      LOGGER.trace("Input configuration: {}", configuration);
      LOGGER.trace("Input headers: {}", headers);
      LOGGER.trace("Input body: {}", inputBody);
      if (VALIDATION_ENABLED.equals(configuration.getString(VALIDATION, VALIDATION_ENABLED))) {

      }
      String replyTo = inputMsg.getHeaders().get("reply_to") != null ? inputMsg.getHeaders()
          .getString("reply_to") : null;

      // Don't emit this message when running sample data
      LOGGER.info("Creating output message...");
      if (null == replyTo) {
        LOGGER.error("No reply_to id found!");
        return;
      }
//      final String xml = XML.toString(new JSONObject(body));
//      final Document document = Utils.convertStringToXMLDocument(xml);
//
//      final SOAPMessage message = MessageFactory.newInstance().createMessage();
//      final MimeHeaders soapHeaders = message.getMimeHeaders();
//
//      soapHeaders.addHeader("SOAPAction", soapBodyDescriptor.getSoapAction());
//      message.getSOAPBody().addDocument(document);

      LOGGER.info("Creating output message...");
      String message = inputBody.toString();

//      PipedInputStream in = new PipedInputStream();
//      final PipedOutputStream outputStream = new PipedOutputStream(in);
//      message.writeTo(outputStream);

      LOGGER.info("Building HTTP reply object...");
      InputStream in = new ByteArrayInputStream(message.getBytes());
      HttpReply httpReply = new HttpReply.Builder()
          .content(in)
          .header(HEADER_ROUTING_KEY, replyTo)
          .header(HEADER_CONTENT_TYPE, CONTENT_TYPE)
          .status(200)
          .build();

      LOGGER.info("Making HTTP reply...");
      parameters.getEventEmitter().emitHttpReply(httpReply);

      JsonObject body = Json.createObjectBuilder()
//          .add("SoapResponse", Utils.getStringOfSoapMessage(message))
          .add("SoapResponse", message)
          .build();

      LOGGER.info("Emitting data...");
      parameters.getEventEmitter().emitData(new Message.Builder().body(body).build());
    } catch (ComponentException e) {
      LOGGER.error("Got component exception: ", e);
      e.printStackTrace();
      parameters.getEventEmitter().emitException(e);
    } catch (Exception e) {
      LOGGER.error("Got exception: ", e);
      e.printStackTrace();
      parameters.getEventEmitter().emitException(e);
    }
  }
}
