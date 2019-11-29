package io.elastic.soap.utils;

import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;

public class XMLElementsIntrospector extends JacksonAnnotationIntrospector {

  @Override
  public List<PropertyName> findPropertyAliases(Annotated a) {
    if (a.hasAnnotation(XmlElements.class) || a.hasAnnotation(XmlElementRefs.class)) {
      final List<PropertyName> result = Optional.ofNullable(super.findPropertyAliases(a)).orElse(new ArrayList<>());
      final List<PropertyName> names = getXMLElementsNames(a);
      result.addAll(names);
      return result;
    }
    if (a.hasAnnotation(XmlElement.class)) {
      final List<PropertyName> result = Optional.ofNullable(super.findPropertyAliases(a)).orElse(new ArrayList<>());
      XmlElement element = a.getAnnotation(XmlElement.class);
      result.add(new PropertyName(element.name()));
      return result;
    }
    return super.findPropertyAliases(a);
  }

  @Override
  public Object findDeserializer(Annotated a) {
    if (a.hasAnnotation(XmlElementRefs.class)) {
      List<String> names = getXMLElementsNames(a).stream().map(PropertyName::getSimpleName).collect(Collectors.toList());
      names.add(a.getName());
      return new XMLElementRefDeserializer(a.getType(), a.getAnnotation(XmlElementRefs.class));
    }
    if (a.hasAnnotation(XmlElements.class)) {
      List<String> names = getXMLElementsNames(a).stream().map(PropertyName::getSimpleName).collect(Collectors.toList());
      names.add(a.getName());
      return new XMLElementsDeserializer(a.getType(), a.getAnnotation(XmlElementRefs.class));
    }
    return super.findDeserializer(a);
  }

  public List<PropertyName> getXMLElementsNames(final Annotated a) {
    if (a.hasAnnotation(XmlElements.class)) {
      return Arrays.stream(a.getAnnotation(XmlElements.class).value()).map(e -> new PropertyName(e.name())).collect(Collectors.toList());
    }
    return Arrays.stream(a.getAnnotation(XmlElementRefs.class).value()).map(e -> new PropertyName(e.name())).collect(Collectors.toList());
  }
}
