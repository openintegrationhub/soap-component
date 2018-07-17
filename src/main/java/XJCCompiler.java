//package io.elastic;


import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

import com.sun.codemodel.JCodeModel;
import com.sun.tools.xjc.api.S2JJAXBModel;
import com.sun.tools.xjc.api.SchemaCompiler;
import com.sun.tools.xjc.api.XJC;
import java.util.List;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.predic8.wsdl.*;

public class XJCCompiler {

  public static void main(String... args) throws Throwable {
//    String schemaPath =      "/Users/paulvoropaiev/Development/elasticio/garry/Development/intellij-projects/EclipseLink-MOXy-Examples/src/main/EntityListService.xsd";
//    String outputDirectory = "/Users/paulvoropaiev/Development/elasticio/garry/Development/intellij-projects/EclipseLink-MOXy-Examples/src/main/java/example/dynamic/convert";
//
//    String fullyQualifiedClassName = "example.dynamic.convert.io.elastic.schema.dnb.schemas";
//
//    generateJAXB(schemaPath, outputDirectory, fullyQualifiedClassName);
//
//    final ObjectMapper objectMapper = createJaxbObjectMapper();
//    final JsonSchema jsonSchema = objectMapper.generateJsonSchema(Class.forName(fullyQualifiedClassName + ".FindCompanyRequest"));
//
//    System.out.println(jsonSchema.toString());

    String wsdlAddress = "http://www.xignite.com/xcurrencies.asmx?WSDL";
    String[] input = new String[]{
//        "-keep",
//        "-B-Xvalue-constructor","-Xnocompile",
        "-d",
        "com",
//        "-p",
//        "io.elastic.soap",
        wsdlAddress};

//    WsImport.doMain(input);

    WSDLParser parser = new WSDLParser();

    Definitions defs = parser.parse(wsdlAddress);
//    List<PortType> portTypesList = defs.getPortTypes();
//
//    for (PortType portType : portTypesList) {
//      System.out.println("PortType: " + portType.getName());
//      for (Operation operation : portType.getOperations()) {
//        System.out.println("Operation: " + operation.getName());
//      }
//    }

    List<Binding> bindingList = defs.getBindings();

    for (Binding binding : bindingList) {
      System.out.println("Binding: " + binding.getName());
      for (BindingOperation bOperation : binding.getOperations()) {
        System.out.println("Operation: " + bOperation.getName());
      }
    }

  }

  private static void generateJAXB(String schemaPath, String outputDirectory,
      String fullyQualifiedClassName) throws IOException {
    // Setup schema compiler
    SchemaCompiler sc = XJC.createSchemaCompiler();
    sc.forcePackageName(fullyQualifiedClassName);

    // Setup SAX InputSource
    File schemaFile = new File(schemaPath);
    InputSource is = new InputSource(new FileInputStream(schemaFile));
    is.setSystemId(schemaFile.getAbsolutePath());

    // Parse & build
    sc.parseSchema(is);
    S2JJAXBModel model = sc.bind();
    JCodeModel jCodeModel = model.generateCode(null, null);
    jCodeModel.build(new File(outputDirectory));
  }

  public static ObjectMapper createJaxbObjectMapper() {
    final ObjectMapper mapper = new ObjectMapper();
    final TypeFactory typeFactory = TypeFactory.defaultInstance();
    final AnnotationIntrospector introspector = new JaxbAnnotationIntrospector(typeFactory);
    mapper.getDeserializationConfig().with(introspector);
    mapper.getSerializationConfig().with(introspector);
    return mapper;
  }
}

