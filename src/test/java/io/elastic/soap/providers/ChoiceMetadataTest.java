package io.elastic.soap.providers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.elastic.soap.AppConstants;
import io.elastic.soap.compilers.JaxbCompiler;
import io.elastic.soap.handlers.RequestHandler;
import io.elastic.soap.services.WSDLService;
import io.elastic.soap.services.impls.HttpWSDLService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ChoiceMetadataTest {

  private static BodyMetaProvider provider;
  private static JsonObject config;
  private final static String WSDL_URL = "src/test/resources/choice.wsdl";
  private static String[] arrayOfDirsToDelete = {"src/com", "src/io"};

  @BeforeAll
  public static void beforeAll() throws Throwable {
    Definitions definitions = getDefinitions(WSDL_URL);
    config = Json.createObjectBuilder()
        .add(AppConstants.BINDING_CONFIG_NAME, "WeatherSoap")
        .add(AppConstants.OPERATION_CONFIG_NAME, "GetWeatherInformation")
        .add(AppConstants.WSDL_CONFIG_NAME, "http://weather.com?wsdl")
        .add("auth",
            Json.createObjectBuilder().add("type", "No Auth")
                .add("basic", Json.createObjectBuilder().add("username", "")
                    .add("password", "")
                    .build())
        )
        .build();

    provider = new BodyMetaProvider();
    WSDLService service = spy(new HttpWSDLService());
    provider.setWsdlService(service);
    doReturn(definitions).when(service).getWSDL(any(JsonObject.class));
    JaxbCompiler.generateAndLoadJaxbStructure(WSDL_URL);
    JaxbCompiler.putToCache("http://weather.com?wsdl", AppConstants.GENERATED_RESOURCES_DIR);
  }


  @AfterAll
  public static void cleanup() throws IOException {
    for (final String dirName : Arrays.asList(arrayOfDirsToDelete)) {
      final File dir = new File(dirName);
      if (dir.exists()) {
//        FileUtils.deleteDirectory(dir);
      }
    }

//    FileUtils.cleanDirectory(new File(AppConstants.GENERATED_RESOURCES_DIR));
  }

  public static Definitions getDefinitions(final String wsdlPath) {
    final WSDLParser parser = new WSDLParser();
    return parser.parse(wsdlPath);
  }

  @Test
  public void choiceMetadataGeneration() {
    final JsonObject object = provider.getMetaModel(config);
    Assertions.assertNotNull(object.get("in"));
    Assertions.assertNotNull(object.get("out"));
  }

  @Test
  public void serializerTest() throws ClassNotFoundException, IOException {
    final RequestHandler handler = new RequestHandler();
    final String weatherDescription = "WeatherDescription";
    final Class<?> clazz = Class.forName("com.cdyne.ws.weatherws.WeatherDescription");
    class DummyTester {

      public Object test(JsonObject o) {
        try {
          return handler.getObjectFromJson(o, weatherDescription, clazz);
        } catch (Exception e) {
          throw new RuntimeException(e);
        }

      }
    }
    DummyTester tester = new DummyTester();
    readResourceFileAsJsonArray("choices.json").stream().map(JsonValue::asJsonObject).forEach(o -> {
      System.out.println(o);
      Object result = tester.test(o);
      Assertions.assertNotNull(result);
    });
  }

  private JsonArray readResourceFileAsJsonArray(final String path) {
    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    JsonReader jsonReader = Json.createReader(new InputStreamReader(inputStream));
    JsonArray choices = jsonReader.readArray();
    jsonReader.close();
    return choices;
  }

}
