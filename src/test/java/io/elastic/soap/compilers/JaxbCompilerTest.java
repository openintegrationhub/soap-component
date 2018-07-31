package io.elastic.soap.compilers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.WSDLParser;
import io.elastic.soap.AppConstants;
import io.elastic.soap.compilers.generators.IJaxbGenerator;
import io.elastic.soap.compilers.generators.impl.Axis2GeneratorImpl;
import io.elastic.soap.compilers.generators.impl.WsImportGeneratorImpl;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

public class JaxbCompilerTest {

  @Spy
  private static JaxbCompiler jaxbCompilerUT = spy(new JaxbCompiler());

  private final static String WSDL_URL = "src/test/resources/xcurrencies.wsdl";

  private static Definitions definitionsUT;

  private static String[] arrayOfDirsToDelete = {AppConstants.GENERATED_RESOURCES_DIR, "src/com",
      "src/io"};

  @BeforeAll
  public static void init() {
    definitionsUT = JaxbCompilerTest.getDefinitions(WSDL_URL);
    doReturn(definitionsUT).when(jaxbCompilerUT).getDefinitionsFromWsdl(any(String.class));
  }

  @AfterAll
  public static void cleanup() throws IOException {
    for (final String dirName : Arrays.asList(arrayOfDirsToDelete)) {
      final File dir = new File(dirName);
      if (dir.exists()) {
        FileUtils.deleteDirectory(dir);
      }
    }
  }

  // Mock real remote WSDL call method in order to use local WSDL resource
  public static Definitions getDefinitions(final String wsdlPath) {
    final WSDLParser parser = new WSDLParser();
    return parser.parse(wsdlPath);
  }

  @Test
  public void createFolder() {
    JaxbCompiler.createFolder(AppConstants.GENERATED_RESOURCES_DIR);
    assertTrue(new File(AppConstants.GENERATED_RESOURCES_DIR).exists(),String.format("File %s is not exists", AppConstants.GENERATED_RESOURCES_DIR));
  }

  @Test
  public void injectJaxbGeneratorModuleAxis2() {
    final IJaxbGenerator iJaxbGenerator = JaxbCompiler
        .injectJaxbGeneratorModule(Axis2GeneratorImpl.class);
    assertTrue(iJaxbGenerator instanceof Axis2GeneratorImpl, "iJaxbGenerator is not instance of Axis2GeneratorImpl");
  }

  @Test
  public void injectJaxbGeneratorModuleWsImport() {
    final IJaxbGenerator iJaxbGenerator = JaxbCompiler
        .injectJaxbGeneratorModule(WsImportGeneratorImpl.class);
    assertTrue(iJaxbGenerator instanceof WsImportGeneratorImpl, "iJaxbGenerator is not instance of WsImportGeneratorImpl");
  }

  @Test
  public void generateAndLoadJaxbStructure() throws Throwable {
    JaxbCompiler.generateAndLoadJaxbStructure(WSDL_URL);
    Class.forName("com.xignite.services.ArrayOfTick");
    assertTrue(true);
  }
}
