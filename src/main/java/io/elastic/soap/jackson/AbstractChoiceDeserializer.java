package io.elastic.soap.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.elastic.soap.utils.Utils;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBElement;

public abstract class AbstractJaxbDeserializer extends JsonDeserializer {

  protected final Class rawType;
  protected final JavaType type;
  protected final ObjectMapper mapper;

  protected AbstractJaxbDeserializer(JavaType type) {
    this.type = type;
    this.rawType = type.getRawClass();
    this.mapper = Utils.getConfiguredObjectMapper();
  }

  @Override
  public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
    final JsonNode targetNode = p.getCodec().readTree(p);
    nodeOneOfPossibleTypes(targetNode, this.getPossibleTypes());
    return this.mapper.convertValue(targetNode, this.rawType);
  }

  public abstract List<Class> getPossibleTypes();

  /**
   * Checks that provided JsonNode from choice element has structure of one of choice element.
   * In case of JAXBElement type was generated it impossible to check the structure, in this case true will be returned.
   * @param node node to be checked
   * @param possibleTypes possible types of node
   * @throws IllegalArgumentException if node structure is not one of provided possible types.
   */
  public void nodeOneOfPossibleTypes(final JsonNode node, final List<Class> possibleTypes) throws IllegalArgumentException {
    if (possibleTypes.contains(JAXBElement.class)) {
      return;
    }
    boolean result = false;
    for (Class type : possibleTypes) {
      result = nodeCanBeConvertedToType(node, type);
      if (result) {
        break;
      }
    }
    if (!result && node.isArray() && (this.type.isArrayType() || this.type.isCollectionLikeType())) {
      result = handleArrayNode(node, possibleTypes);
    }
    if (!result) {
      throw new IllegalArgumentException(constructExceptionString(node, possibleTypes));
    }
  }

  /**
   * Checks each item of node over provided possible types.
   * @param arrayNode ArrayNode
   * @param possibleTypes possible types of node
   * @return
   */
  public boolean handleArrayNode(JsonNode arrayNode, List<Class> possibleTypes) {
    Class targetType = null;
    for (JsonNode node : arrayNode) {
      targetType = Optional.ofNullable(targetType).orElse(findNodeType(node, possibleTypes));
      boolean canBeConverted = nodeCanBeConvertedToType(node, targetType);
      if (!canBeConverted) {
        return false;
      }
    }
    return true;
  }

  private Class findNodeType(JsonNode node, List<Class> possibleTypes) {
    for (Class type : possibleTypes) {
      if (nodeCanBeConvertedToType(node, type)) {
        return type;
      }
    }
    throw new IllegalArgumentException(constructExceptionString(node, possibleTypes));
  }


  public boolean nodeCanBeConvertedToType(final JsonNode node, Class type) {
    try {
      this.mapper.convertValue(node, type);
      return true;
    } catch (IllegalArgumentException ex) {
      return false;
    }
  }

  public String constructExceptionString(final JsonNode value, final List<Class> possibleTypes) {
    StringBuilder bd = new StringBuilder("Failed to convert value: ");
    bd.append(value.toPrettyString()).append("\n to one of: ");
    bd.append(possibleTypes.stream().map(Class::getSimpleName).collect(Collectors.joining(","))).append(".");
    return bd.toString();
  }
}
