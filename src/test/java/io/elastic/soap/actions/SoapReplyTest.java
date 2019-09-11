package io.elastic.soap.actions;

import static io.elastic.soap.actions.SoapReplyAction.CONTENT_TYPE;
import static io.elastic.soap.actions.SoapReplyAction.HEADER_CONTENT_TYPE;
import static io.elastic.soap.actions.SoapReplyAction.HEADER_ROUTING_KEY;

import io.elastic.api.EventEmitter;
import io.elastic.api.ExecutionParameters;
import io.elastic.api.Message;
import io.elastic.soap.AppConstants;
import io.elastic.soap.TestCallback;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import net.joshka.junit.json.params.JsonFileSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoapReplyTest {

  private static JsonObject cfg;
  private static JsonObject body;
  private static JsonObject provider;
  private static final Logger logger = LoggerFactory.getLogger(EventEmitter.class);
  private static SoapReplyAction soapReplyAction = new SoapReplyAction();

  @BeforeAll
  public static void initConfig() throws IOException {

    cfg = Json.createObjectBuilder()
        .add(AppConstants.BINDING_CONFIG_NAME, "ProposalResponseServiceSoap")
        .add(AppConstants.OPERATION_CONFIG_NAME, "SetProposalResponse")
        .add(AppConstants.WSDL_CONFIG_NAME, "src/test/resources/ProposalResponseService.wsdl")
        .add("auth",
            Json.createObjectBuilder().add("type", "No Auth")
                .add("basic", Json.createObjectBuilder().add("username", "")
                    .add("password", "")
                    .build())
        )
        .build();

    soapReplyAction.init(cfg);
  }


  @ParameterizedTest
  @JsonFileSource(resources = "/soapResponseJsonSample.json")
  @DisplayName("Send SOAP Response message")
  public void soapReplyAction(JsonObject body) {
    JsonObject headers = Json.createObjectBuilder()
        .add("reply_to", "testReplyId")
        .add(HEADER_CONTENT_TYPE, CONTENT_TYPE)
        .build();
    Message msg = new Message.Builder().body(body).headers(headers).build();
    EventEmitter eventEmitter = new EventEmitter.Builder()
        .onData(new TestCallback())
        .onError(new TestCallback())
        .onSnapshot(new TestCallback())
        .onRebound(new TestCallback())
        .onHttpReplyCallback(new TestCallback())
        .build();
    ExecutionParameters executionParameters = new ExecutionParameters.Builder(msg, eventEmitter).configuration(cfg).build();
    soapReplyAction.execute(executionParameters);
  }
}
