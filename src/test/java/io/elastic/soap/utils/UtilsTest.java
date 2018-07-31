package io.elastic.soap.utils;

import static junit.framework.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.elastic.soap.AppConstants;
import java.net.MalformedURLException;
import javax.json.Json;
import javax.json.JsonObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UtilsTest {

  private static JsonObject configHttpNoAuth;
  private static JsonObject configHttpBasicAuth;
  private static JsonObject configHttpsNoAuth;
  private static JsonObject configHttpsBasicAuth;
  private static String elementName;

  @BeforeAll
  public static void initTest() {
    elementName = "getBank";
    configHttpNoAuth = Json.createObjectBuilder()
        .add(AppConstants.BINDING_CONFIG_NAME, "XigniteCurrenciesSoap")
        .add(AppConstants.OPERATION_CONFIG_NAME, "GetCurrencyIntradayChartCustom")
        .add(AppConstants.WSDL_CONFIG_NAME, "http://www.xignite.com/xcurrencies.asmx?WSDL")
        .add("auth",
            Json.createObjectBuilder().add("type", "No Auth")
                .add("basic", Json.createObjectBuilder().add("username", "")
                    .add("password", "")
                    .build())
        )
        .build();

    configHttpBasicAuth = Json.createObjectBuilder()
        .add(AppConstants.BINDING_CONFIG_NAME, "XigniteCurrenciesSoap")
        .add(AppConstants.OPERATION_CONFIG_NAME, "GetCurrencyIntradayChartCustom")
        .add(AppConstants.WSDL_CONFIG_NAME, "http://www.xignite.com/xcurrencies.asmx?WSDL")
        .add("auth",
            Json.createObjectBuilder().add("type", "Basic Auth")
                .add("basic", Json.createObjectBuilder().add("username", "Leadtributor")
                    .add("password", "Leadtributor")
                    .build())
        )
        .build();

    configHttpsNoAuth = Json.createObjectBuilder()
        .add(AppConstants.BINDING_CONFIG_NAME, "BLZServiceSOAP11Binding")
        .add(AppConstants.OPERATION_CONFIG_NAME, "getBank")
        .add(AppConstants.WSDL_CONFIG_NAME,
            "https://www.thomas-bayer.com/axis2/services/BLZService?wsdl")
        .add("auth",
            Json.createObjectBuilder().add("type", "No Auth")
                .add("basic", Json.createObjectBuilder().add("username", "")
                    .add("password", "")
                    .build())
        )
        .build();

    configHttpsBasicAuth = Json.createObjectBuilder()
        .add(AppConstants.BINDING_CONFIG_NAME, "BLZServiceSOAP11Binding")
        .add(AppConstants.OPERATION_CONFIG_NAME, "getBank")
        .add(AppConstants.WSDL_CONFIG_NAME,
            "https://www.thomas-bayer.com/axis2/services/BLZService?wsdl")
        .add("auth",
            Json.createObjectBuilder().add("type", "Basic Auth")
                .add("basic", Json.createObjectBuilder().add("username", "Leadtributor")
                    .add("password", "Leadtributor")
                    .build())
        )
        .build();
  }

  @Test
  public void injectAuthStringIntoUrlHttp() throws MalformedURLException {
    assertTrue(Utils.injectAuthStringIntoUrl(configHttpBasicAuth)
        .equals("http://Leadtributor:Leadtributor@www.xignite.com/xcurrencies.asmx?WSDL"));
  }

  @Test
  public void injectAuthStringIntoUrlHttps() throws MalformedURLException {
    assertTrue(Utils.injectAuthStringIntoUrl(configHttpsBasicAuth)
        .equals(
            "https://Leadtributor:Leadtributor@www.thomas-bayer.com/axis2/services/BLZService?wsdl"));
  }

  @Test
  public void getConfiguredObjectMapper() {
    assertTrue(Utils.getConfiguredObjectMapper() instanceof ObjectMapper);
  }

  @Test
  public void getBinding() {
    assertTrue("XigniteCurrenciesSoap".equals(Utils.getBinding(configHttpNoAuth)));
  }

  @Test
  public void getOperation() {
    assertTrue("GetCurrencyIntradayChartCustom".equals(Utils.getOperation(configHttpBasicAuth)));
  }

  @Test
  public void getWithUpperFirstLetter() {
    assertTrue("GetBank".equals(Utils.getWithUpperFirstLetter(elementName)));
  }

  @Test
  public void getHttpWsdlUrlNoAuth() {
    assertTrue(
        "http://www.xignite.com/xcurrencies.asmx?WSDL".equals(Utils.getWsdlUrl(configHttpNoAuth)));
  }

  @Test
  public void getHttpsWsdlUrlNoAuth() {
    assertTrue(
        "https://www.thomas-bayer.com/axis2/services/BLZService?wsdl".equals(Utils.getWsdlUrl(
            configHttpsNoAuth)));
  }

  @Test
  public void getHttpWsdlUrlBasicAuth() {
    assertTrue(
        "http://Leadtributor:Leadtributor@www.xignite.com/xcurrencies.asmx?WSDL".equals(Utils.getWsdlUrl(
            configHttpBasicAuth)));
  }

  @Test
  public void getHttpsWsdlUrlBasicAuth() {
    assertTrue(
        "https://Leadtributor:Leadtributor@www.thomas-bayer.com/axis2/services/BLZService?wsdl".equals(Utils.getWsdlUrl(
            configHttpsBasicAuth)));
  }
}
