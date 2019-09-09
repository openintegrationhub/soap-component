package io.elastic.soap.triggers;


import static org.junit.jupiter.api.Assertions.assertEquals;

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

public class ReceiveRequestTest {

  private static JsonObject cfg;
  private static JsonObject body;
  private static JsonObject provider;
  private static final Logger logger = LoggerFactory.getLogger(EventEmitter.class);

  @BeforeAll
  public static void initConfig() throws IOException {

    cfg = Json.createObjectBuilder()
        .add(AppConstants.BINDING_CONFIG_NAME, "ProposalResponseServiceSoap")
        .add(AppConstants.OPERATION_CONFIG_NAME, "SetProposalResponse")
        .add(AppConstants.WSDL_CONFIG_NAME, "/ProposalResponseService.wsdl")
        .add("auth",
            Json.createObjectBuilder().add("type", "No Auth")
                .add("basic", Json.createObjectBuilder().add("username", "")
                    .add("password", "")
                    .build())
        )
        .build();
  }


  @ParameterizedTest
  @JsonFileSource(resources = "/soapJsonSample.json")
  @DisplayName("Receive and parse SOAP message")
  public void receiveRequest(JsonObject body) {
    ReceiveRequest receiveRequest = new ReceiveRequest();

    Message msg = new Message.Builder().body(body).build();
    EventEmitter eventEmitter = new EventEmitter.Builder()
        .onData(new TestCallback())
        .onError(new TestCallback())
        .onSnapshot(new TestCallback())
        .onRebound(new TestCallback())
        .onHttpReplyCallback(new TestCallback())
        .build();
    ExecutionParameters executionParameters = new ExecutionParameters.Builder(msg, eventEmitter).configuration(cfg).build();

    receiveRequest.execute(executionParameters);
    Message result = (Message) TestCallback.getLastCall();
    assertEquals(body.getJsonObject("soapenv-Envelope").getJsonObject("soapenv-Body"), result.getBody());
  }
}
