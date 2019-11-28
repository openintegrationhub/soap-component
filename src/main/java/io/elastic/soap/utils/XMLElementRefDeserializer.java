package io.elastic.soap.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.xml.bind.annotation.XmlElementRefs;

public class XMLElementRefDeserializer extends JsonDeserializer {

  private final Class rawType;
  private final XmlElementRefs annotation;

  public XMLElementRefDeserializer(final JavaType type, final XmlElementRefs annotation) {
    this.annotation = annotation;
    this.rawType = type.getRawClass();
  }

  @Override
  public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    final ObjectMapper m = Utils.getConfiguredObjectMapper();
    final JsonNode targetNode = p.getCodec().readTree(p);
    return m.convertValue(targetNode, this.rawType);
  }

}
