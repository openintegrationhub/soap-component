package io.elastic.soap.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class XMLElementsDeserializer extends AbstractJaxbDeserializer {


  private final XmlElements annotation;

  public XMLElementsDeserializer(final JavaType type, final XmlElements annotation) {
    super(type);
    this.annotation = annotation;
  }

  @Override
  public List<Class> getPossibleTypes() {
    return Arrays.stream(this.annotation.value()).map(XmlElement::type).collect(Collectors.toList());
  }
}
