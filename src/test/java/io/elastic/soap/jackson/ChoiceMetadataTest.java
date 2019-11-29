package io.elastic.soap.jackson;

import io.elastic.soap.handlers.RequestHandler;
import java.io.InputStream;
import java.io.InputStreamReader;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChoiceMetadataTest {

  @Test
  public void serializeClassWithXmlElementsAnnotation() throws ClassNotFoundException {
    final RequestHandler handler = new RequestHandler();
    final String weatherDescription = "XmlElementsChoice";
    final Class clazz = XmlElementsChoice.class;
    readResourceFileAsJsonArray("choicesElements.json").stream().map(JsonValue::asJsonObject).forEach(o -> {
      System.out.println(o);
      Object result = this.wrapAndTest(handler, o, weatherDescription, clazz);
      Assertions.assertNotNull(result);
    });
  }

  @Test
  public void serializeClassWithXmlElementRefssAnnotation() throws ClassNotFoundException {
    final RequestHandler handler = new RequestHandler();
    final String weatherDescription = "XmlElementRefsChoice";
    final Class clazz = XmlElementRefsChoice.class;
    readResourceFileAsJsonArray("choicesRefs.json").stream().map(JsonValue::asJsonObject).forEach(o -> {
      System.out.println(o);
      Object result = this.wrapAndTest(handler, o, weatherDescription, clazz);
      Assertions.assertNotNull(result);
    });
  }

  public Object wrapAndTest(RequestHandler handler, JsonObject request, String elementName, Class clazz) {
      try {
        return handler.getObjectFromJson(request, elementName, clazz);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
  }
  public JsonArray readResourceFileAsJsonArray(final String path) {
    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    JsonReader jsonReader = Json.createReader(new InputStreamReader(inputStream));
    JsonArray choices = jsonReader.readArray();
    jsonReader.close();
    return choices;
  }

}
