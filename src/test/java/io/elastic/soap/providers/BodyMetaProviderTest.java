package io.elastic.soap.providers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.predic8.wsdl.BindingOperation;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Message;
import com.predic8.wsdl.WSDLParser;
import io.elastic.soap.AppConstants;
import io.elastic.soap.compilers.model.SoapBodyDescriptor;
import java.io.File;
import java.io.IOException;
import javax.json.Json;
import javax.json.JsonObject;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

public class BodyMetaProviderTest {

  @Spy
  private static BodyMetaProvider providerUT = spy(new BodyMetaProvider());

  private final static String WSDL_URL = "src/test/resources/xcurrencies.wsdl";

  private static SoapBodyDescriptor mockedDescriptor;
  private static JsonObject config;
  private static String jsonSchema;

  @BeforeAll
  public static void initConfig() throws ClassNotFoundException, JsonMappingException {
    config = Json.createObjectBuilder()
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
    jsonSchema = "{\"in\":{\"type\":\"object\",\"properties\":{\"GetCurrencyIntradayChartCustom\":{\"type\":\"object\",\"id\":\"urn:jsonschema:com:xignite:services:GetCurrencyIntradayChartCustom\",\"properties\":{\"symbol\":{\"type\":\"string\"},\"startTime\":{\"type\":\"string\"},\"endTime\":{\"type\":\"string\"},\"timeZone\":{\"type\":\"string\"},\"style\":{\"type\":\"string\",\"enum\":[\"LINE\",\"CANDLE\",\"STICK\",\"AREA\",\"PERCENTAGE\",\"LINE_PERCENTAGE\",\"CANDLE_PERCENTAGE\",\"STICK_PERCENTAGE\",\"AREA_PERCENTAGE\"]},\"width\":{\"type\":\"integer\"},\"height\":{\"type\":\"integer\"},\"periodType\":{\"type\":\"string\"},\"tickPeriods\":{\"type\":\"integer\"},\"design\":{\"type\":\"object\",\"id\":\"urn:jsonschema:com:xignite:services:ChartDesign\",\"properties\":{\"outcome\":{\"type\":\"string\",\"enum\":[\"SUCCESS\",\"SYSTEM_ERROR\",\"REQUEST_ERROR\",\"REGISTRATION_ERROR\"]},\"message\":{\"type\":\"string\"},\"identity\":{\"type\":\"string\"},\"delay\":{\"type\":\"number\"},\"secure\":{\"type\":\"boolean\"},\"textTitle\":{\"type\":\"string\"},\"textHeader\":{\"type\":\"string\"},\"textFooter\":{\"type\":\"string\"},\"textPriceLine\":{\"type\":\"string\"},\"textVolumeBar\":{\"type\":\"string\"},\"textHighest\":{\"type\":\"string\"},\"textLowest\":{\"type\":\"string\"},\"textOpen\":{\"type\":\"string\"},\"textClose\":{\"type\":\"string\"},\"textUp\":{\"type\":\"string\"},\"textDown\":{\"type\":\"string\"},\"colorBackground\":{\"type\":\"string\"},\"colorBackWall\":{\"type\":\"string\"},\"colorVolumeBackWall\":{\"type\":\"string\"},\"showVolumeBackWall\":{\"type\":\"boolean\"},\"colorHighlight\":{\"type\":\"string\"},\"colorPriceLine\":{\"type\":\"string\"},\"colorVolumeBar\":{\"type\":\"string\"},\"colorVolumeBarFill\":{\"type\":\"string\"},\"colorHigh\":{\"type\":\"string\"},\"colorStickUp\":{\"type\":\"string\"},\"colorStickLow\":{\"type\":\"string\"},\"colorConstant\":{\"type\":\"string\"},\"colorLow\":{\"type\":\"string\"},\"colorPoint\":{\"type\":\"string\"},\"colorTitle\":{\"type\":\"string\"},\"colorFooter\":{\"type\":\"string\"},\"colorHeader\":{\"type\":\"string\"},\"colorAxis\":{\"type\":\"string\"},\"colorGrid\":{\"type\":\"string\"},\"colorFonts\":{\"type\":\"string\"},\"colorStripe\":{\"type\":\"string\"},\"colorOpen\":{\"type\":\"string\"},\"colorClose\":{\"type\":\"string\"},\"colorVerticalGrid\":{\"type\":\"string\"},\"colorHorizontalGrid\":{\"type\":\"string\"},\"colorUp\":{\"type\":\"string\"},\"colorDown\":{\"type\":\"string\"},\"colorHighLowLine\":{\"type\":\"string\"},\"colorCollection\":{\"type\":\"string\"},\"gridHorizontalStyle\":{\"type\":\"string\",\"enum\":[\"SOLID\",\"DOT\",\"DASH\",\"DASH_DOT\",\"DASH_DOT_DOT\"]},\"gridVerticalStyle\":{\"type\":\"string\",\"enum\":[\"SOLID\",\"DOT\",\"DASH\",\"DASH_DOT\",\"DASH_DOT_DOT\"]},\"gridHorizontalWidth\":{\"type\":\"integer\"},\"gridVerticalWidth\":{\"type\":\"integer\"},\"colorFrame\":{\"type\":\"string\"},\"frameBorder\":{\"type\":\"string\"},\"formatPriceLine\":{\"type\":\"string\"},\"formatVolume\":{\"type\":\"string\"},\"formatDate\":{\"type\":\"string\"},\"gradeBackground\":{\"type\":\"boolean\"},\"gradeBackwall\":{\"type\":\"boolean\"},\"waterMark\":{\"type\":\"string\"},\"waterMarkTopMargin\":{\"type\":\"integer\"},\"waterMarkLeftMargin\":{\"type\":\"integer\"},\"waterMarkTransparency\":{\"type\":\"integer\"},\"pointSize\":{\"type\":\"number\"},\"stackVariationLabels\":{\"type\":\"boolean\"},\"showAxisLabelInLegend\":{\"type\":\"boolean\"},\"lineWidth\":{\"type\":\"integer\"},\"splitPercent\":{\"type\":\"integer\"},\"showHigh\":{\"type\":\"boolean\"},\"showLow\":{\"type\":\"boolean\"},\"showOpen\":{\"type\":\"boolean\"},\"showClose\":{\"type\":\"boolean\"},\"showVolume\":{\"type\":\"boolean\"},\"showUpVariation\":{\"type\":\"boolean\"},\"showDownVariation\":{\"type\":\"boolean\"},\"showLegend\":{\"type\":\"boolean\"},\"variationYear\":{\"type\":\"integer\"},\"volumeDivider\":{\"type\":\"integer\"},\"volumeTextOffset\":{\"type\":\"integer\"},\"priceTextOffset\":{\"type\":\"integer\"},\"frameType\":{\"type\":\"string\",\"enum\":[\"NONE\",\"COLONIAL\",\"COMMON\",\"EMBED\",\"EMBOSS\",\"FRAME_OPEN_RIGHT\",\"FRAME_OPEN_RL\",\"ONE_BAR_GRADIENT\",\"ROUNDED_UP\",\"SLIM_ROUNDED_SHADOWED\"]},\"projection\":{\"type\":\"string\",\"enum\":[\"ORTHOGONAL\",\"ORTHOGONAL_ELEVATED\",\"ORTHOGONAL_HORIZONTAL_LEFT\",\"ORTHOGONAL_HORIZONTAL_RIGHT\",\"ORTHOGONAL_HALF\",\"ORTHOGONAL_HALF_HORIZONTAL_LEFT\",\"ORTHOGONAL_HALF_HORIZONTAL_RIGHT\",\"ORTHOGONAL_HALF_ROTATED\",\"ORTHOGONAL_HALF_ELEVATED\",\"PERSPECTIVE\",\"PERSPECTIVE_HORIZONTAL_LEFT\",\"PERSPECTIVE_HORIZONTAL_RIGHT\",\"PERSPECTIVE_ROTATED\",\"PERSPECTIVE_ELEVATED\",\"PERSPECTIVE_TILTED\"]},\"marginTop\":{\"type\":\"integer\"},\"marginBottom\":{\"type\":\"integer\"},\"marginLeft\":{\"type\":\"integer\"},\"marginRight\":{\"type\":\"integer\"},\"fontFamily\":{\"type\":\"string\"},\"fontSizeHeader\":{\"type\":\"integer\"},\"fontSizeFooter\":{\"type\":\"integer\"},\"height\":{\"type\":\"number\"},\"width\":{\"type\":\"number\"},\"zoomPercent\":{\"type\":\"integer\"},\"legendBox\":{\"type\":\"boolean\"},\"colorLegendBackground\":{\"type\":\"string\"},\"colorLegendBorder\":{\"type\":\"string\"},\"legendVerticalPosition\":{\"type\":\"integer\"},\"legendHorizontalPosition\":{\"type\":\"integer\"},\"reload\":{\"type\":\"boolean\"},\"showPriceChartLabels\":{\"type\":\"boolean\"},\"tickPrecision\":{\"type\":\"string\",\"enum\":[\"TICK\",\"MILLISECOND\",\"SECOND\",\"MINUTE\",\"HOUR\",\"DAY\",\"WEEK\",\"MONTH\"]},\"tickPeriods\":{\"type\":\"integer\"},\"waterMarkHorizontalAlign\":{\"type\":\"string\",\"enum\":[\"CENTER\",\"LEFT\",\"RIGHT\"]},\"lightScheme\":{\"type\":\"string\",\"enum\":[\"NONE\",\"SOFT_TOP_LEFT\",\"SOFT_FRONTAL\",\"SOFT_TOP_RIGHT\",\"SHINY_TOP_LEFT\",\"SHINY_FRONTAL\",\"SHINY_TOP_RIGHT\",\"METALLIC_LUSTRE\",\"NORTHERN_LIGHTS\"]},\"fontSizeLegend\":{\"type\":\"integer\"},\"fontSizeAxes\":{\"type\":\"integer\"},\"fontSizeTitle\":{\"type\":\"integer\"},\"daysForHourDisplay\":{\"type\":\"integer\"},\"daysForDayDisplay\":{\"type\":\"integer\"},\"daysForWeekDisplay\":{\"type\":\"integer\"},\"daysForBiWeeklyDisplay\":{\"type\":\"integer\"},\"daysForMonthDisplay\":{\"type\":\"integer\"},\"daysForQuarterDisplay\":{\"type\":\"integer\"},\"daysForSemiAnnualDisplay\":{\"type\":\"integer\"},\"daysForAnnualDisplay\":{\"type\":\"integer\"},\"daysForBiAnnualDisplay\":{\"type\":\"integer\"},\"daysForPentaAnnualDisplay\":{\"type\":\"integer\"}}}}}}},\"out\":{\"type\":\"object\",\"properties\":{\"GetCurrencyIntradayChartCustomResponse\":{\"type\":\"object\",\"id\":\"urn:jsonschema:com:xignite:services:GetCurrencyIntradayChartCustomResponse\",\"properties\":{\"getCurrencyIntradayChartCustomResult\":{\"type\":\"object\",\"id\":\"urn:jsonschema:com:xignite:services:CurrencyChartIntraday\",\"properties\":{\"outcome\":{\"type\":\"string\",\"enum\":[\"SUCCESS\",\"SYSTEM_ERROR\",\"REQUEST_ERROR\",\"REGISTRATION_ERROR\"]},\"message\":{\"type\":\"string\"},\"identity\":{\"type\":\"string\"},\"delay\":{\"type\":\"number\"},\"design\":{\"type\":\"object\",\"id\":\"urn:jsonschema:com:xignite:services:ChartDesign\",\"properties\":{\"outcome\":{\"type\":\"string\",\"enum\":[\"SUCCESS\",\"SYSTEM_ERROR\",\"REQUEST_ERROR\",\"REGISTRATION_ERROR\"]},\"message\":{\"type\":\"string\"},\"identity\":{\"type\":\"string\"},\"delay\":{\"type\":\"number\"},\"secure\":{\"type\":\"boolean\"},\"textTitle\":{\"type\":\"string\"},\"textHeader\":{\"type\":\"string\"},\"textFooter\":{\"type\":\"string\"},\"textPriceLine\":{\"type\":\"string\"},\"textVolumeBar\":{\"type\":\"string\"},\"textHighest\":{\"type\":\"string\"},\"textLowest\":{\"type\":\"string\"},\"textOpen\":{\"type\":\"string\"},\"textClose\":{\"type\":\"string\"},\"textUp\":{\"type\":\"string\"},\"textDown\":{\"type\":\"string\"},\"colorBackground\":{\"type\":\"string\"},\"colorBackWall\":{\"type\":\"string\"},\"colorVolumeBackWall\":{\"type\":\"string\"},\"showVolumeBackWall\":{\"type\":\"boolean\"},\"colorHighlight\":{\"type\":\"string\"},\"colorPriceLine\":{\"type\":\"string\"},\"colorVolumeBar\":{\"type\":\"string\"},\"colorVolumeBarFill\":{\"type\":\"string\"},\"colorHigh\":{\"type\":\"string\"},\"colorStickUp\":{\"type\":\"string\"},\"colorStickLow\":{\"type\":\"string\"},\"colorConstant\":{\"type\":\"string\"},\"colorLow\":{\"type\":\"string\"},\"colorPoint\":{\"type\":\"string\"},\"colorTitle\":{\"type\":\"string\"},\"colorFooter\":{\"type\":\"string\"},\"colorHeader\":{\"type\":\"string\"},\"colorAxis\":{\"type\":\"string\"},\"colorGrid\":{\"type\":\"string\"},\"colorFonts\":{\"type\":\"string\"},\"colorStripe\":{\"type\":\"string\"},\"colorOpen\":{\"type\":\"string\"},\"colorClose\":{\"type\":\"string\"},\"colorVerticalGrid\":{\"type\":\"string\"},\"colorHorizontalGrid\":{\"type\":\"string\"},\"colorUp\":{\"type\":\"string\"},\"colorDown\":{\"type\":\"string\"},\"colorHighLowLine\":{\"type\":\"string\"},\"colorCollection\":{\"type\":\"string\"},\"gridHorizontalStyle\":{\"type\":\"string\",\"enum\":[\"SOLID\",\"DOT\",\"DASH\",\"DASH_DOT\",\"DASH_DOT_DOT\"]},\"gridVerticalStyle\":{\"type\":\"string\",\"enum\":[\"SOLID\",\"DOT\",\"DASH\",\"DASH_DOT\",\"DASH_DOT_DOT\"]},\"gridHorizontalWidth\":{\"type\":\"integer\"},\"gridVerticalWidth\":{\"type\":\"integer\"},\"colorFrame\":{\"type\":\"string\"},\"frameBorder\":{\"type\":\"string\"},\"formatPriceLine\":{\"type\":\"string\"},\"formatVolume\":{\"type\":\"string\"},\"formatDate\":{\"type\":\"string\"},\"gradeBackground\":{\"type\":\"boolean\"},\"gradeBackwall\":{\"type\":\"boolean\"},\"waterMark\":{\"type\":\"string\"},\"waterMarkTopMargin\":{\"type\":\"integer\"},\"waterMarkLeftMargin\":{\"type\":\"integer\"},\"waterMarkTransparency\":{\"type\":\"integer\"},\"pointSize\":{\"type\":\"number\"},\"stackVariationLabels\":{\"type\":\"boolean\"},\"showAxisLabelInLegend\":{\"type\":\"boolean\"},\"lineWidth\":{\"type\":\"integer\"},\"splitPercent\":{\"type\":\"integer\"},\"showHigh\":{\"type\":\"boolean\"},\"showLow\":{\"type\":\"boolean\"},\"showOpen\":{\"type\":\"boolean\"},\"showClose\":{\"type\":\"boolean\"},\"showVolume\":{\"type\":\"boolean\"},\"showUpVariation\":{\"type\":\"boolean\"},\"showDownVariation\":{\"type\":\"boolean\"},\"showLegend\":{\"type\":\"boolean\"},\"variationYear\":{\"type\":\"integer\"},\"volumeDivider\":{\"type\":\"integer\"},\"volumeTextOffset\":{\"type\":\"integer\"},\"priceTextOffset\":{\"type\":\"integer\"},\"frameType\":{\"type\":\"string\",\"enum\":[\"NONE\",\"COLONIAL\",\"COMMON\",\"EMBED\",\"EMBOSS\",\"FRAME_OPEN_RIGHT\",\"FRAME_OPEN_RL\",\"ONE_BAR_GRADIENT\",\"ROUNDED_UP\",\"SLIM_ROUNDED_SHADOWED\"]},\"projection\":{\"type\":\"string\",\"enum\":[\"ORTHOGONAL\",\"ORTHOGONAL_ELEVATED\",\"ORTHOGONAL_HORIZONTAL_LEFT\",\"ORTHOGONAL_HORIZONTAL_RIGHT\",\"ORTHOGONAL_HALF\",\"ORTHOGONAL_HALF_HORIZONTAL_LEFT\",\"ORTHOGONAL_HALF_HORIZONTAL_RIGHT\",\"ORTHOGONAL_HALF_ROTATED\",\"ORTHOGONAL_HALF_ELEVATED\",\"PERSPECTIVE\",\"PERSPECTIVE_HORIZONTAL_LEFT\",\"PERSPECTIVE_HORIZONTAL_RIGHT\",\"PERSPECTIVE_ROTATED\",\"PERSPECTIVE_ELEVATED\",\"PERSPECTIVE_TILTED\"]},\"marginTop\":{\"type\":\"integer\"},\"marginBottom\":{\"type\":\"integer\"},\"marginLeft\":{\"type\":\"integer\"},\"marginRight\":{\"type\":\"integer\"},\"fontFamily\":{\"type\":\"string\"},\"fontSizeHeader\":{\"type\":\"integer\"},\"fontSizeFooter\":{\"type\":\"integer\"},\"height\":{\"type\":\"number\"},\"width\":{\"type\":\"number\"},\"zoomPercent\":{\"type\":\"integer\"},\"legendBox\":{\"type\":\"boolean\"},\"colorLegendBackground\":{\"type\":\"string\"},\"colorLegendBorder\":{\"type\":\"string\"},\"legendVerticalPosition\":{\"type\":\"integer\"},\"legendHorizontalPosition\":{\"type\":\"integer\"},\"reload\":{\"type\":\"boolean\"},\"showPriceChartLabels\":{\"type\":\"boolean\"},\"tickPrecision\":{\"type\":\"string\",\"enum\":[\"TICK\",\"MILLISECOND\",\"SECOND\",\"MINUTE\",\"HOUR\",\"DAY\",\"WEEK\",\"MONTH\"]},\"tickPeriods\":{\"type\":\"integer\"},\"waterMarkHorizontalAlign\":{\"type\":\"string\",\"enum\":[\"CENTER\",\"LEFT\",\"RIGHT\"]},\"lightScheme\":{\"type\":\"string\",\"enum\":[\"NONE\",\"SOFT_TOP_LEFT\",\"SOFT_FRONTAL\",\"SOFT_TOP_RIGHT\",\"SHINY_TOP_LEFT\",\"SHINY_FRONTAL\",\"SHINY_TOP_RIGHT\",\"METALLIC_LUSTRE\",\"NORTHERN_LIGHTS\"]},\"fontSizeLegend\":{\"type\":\"integer\"},\"fontSizeAxes\":{\"type\":\"integer\"},\"fontSizeTitle\":{\"type\":\"integer\"},\"daysForHourDisplay\":{\"type\":\"integer\"},\"daysForDayDisplay\":{\"type\":\"integer\"},\"daysForWeekDisplay\":{\"type\":\"integer\"},\"daysForBiWeeklyDisplay\":{\"type\":\"integer\"},\"daysForMonthDisplay\":{\"type\":\"integer\"},\"daysForQuarterDisplay\":{\"type\":\"integer\"},\"daysForSemiAnnualDisplay\":{\"type\":\"integer\"},\"daysForAnnualDisplay\":{\"type\":\"integer\"},\"daysForBiAnnualDisplay\":{\"type\":\"integer\"},\"daysForPentaAnnualDisplay\":{\"type\":\"integer\"}}},\"periodType\":{\"type\":\"string\"},\"startTime\":{\"type\":\"string\"},\"endTime\":{\"type\":\"string\"},\"width\":{\"type\":\"integer\"},\"height\":{\"type\":\"integer\"},\"title\":{\"type\":\"string\"},\"style\":{\"type\":\"string\",\"enum\":[\"LINE\",\"CANDLE\",\"STICK\",\"AREA\",\"PERCENTAGE\",\"LINE_PERCENTAGE\",\"CANDLE_PERCENTAGE\",\"STICK_PERCENTAGE\",\"AREA_PERCENTAGE\"]},\"url\":{\"type\":\"string\"}}}}}}}}";
    mockedDescriptor = BodyMetaProviderTest.getSoapBodyDescriptor();
    doReturn(mockedDescriptor).when(providerUT)
        .getSoapBodyDescriptor(any(String.class), any(String.class), any(String.class));

  }

  // Delete generated resources folder
  @AfterAll
  public static void cleanup() throws IOException {
    File generatedJavaDir = new File(AppConstants.GENERATED_RESOURCES_DIR);
    FileUtils.deleteDirectory(generatedJavaDir);
  }

  @Test
  public void getMetaModel() {
    JsonObject metaModel = providerUT.getMetaModel(config);
    System.out.println(metaModel);
    assertEquals(jsonSchema,metaModel.toString());
  }

  @Test
  public void deserialize() throws IOException {
    JsonObject json = Json.createObjectBuilder()
        .add("foo", "bar")
        .add("baz", "baf")
        .build();
    byte[] byteArray = json.toString().getBytes();
    JsonObject deserialized = providerUT.deserialize(byteArray);
    assertTrue(deserialized.toString().equals(json.toString()));
  }

  // Creates SoapBodyDescriptor instance to mock up remote WSDL call
  public static SoapBodyDescriptor getSoapBodyDescriptor() {
    WSDLParser parser = new WSDLParser();
    Definitions defs = parser.parse(WSDL_URL);

    BindingOperation bindingOperation = defs.getBinding("XigniteCurrenciesSoap")
        .getOperation("GetCurrencyIntradayChartCustom");

    Message inputMessage = (Message) bindingOperation.getInput().getProperty("message");
    Message outputMessage = (Message) bindingOperation.getOutput().getProperty("message");

    assertEquals(inputMessage.getParts().get(0).getElement().getName(),
        "GetCurrencyIntradayChartCustom");

    assertEquals(outputMessage.getParts().get(0).getElement().getName(),
        "GetCurrencyIntradayChartCustomResponse");

    String inputElementName = inputMessage.getParts().get(0).getElement().getName();
    String outputElementName = outputMessage.getParts().get(0).getElement().getName();

    SoapBodyDescriptor soapBodyDescriptor = new SoapBodyDescriptor.Builder()
        .setBindingName("XigniteCurrenciesSoap").setOperationName("GetCurrencyIntradayChartCustom")
        .setRequestBodyElementName(inputElementName)
        .setRequestBodyClassName("com.xignite.services.GetCurrencyIntradayChartCustom")
        .setResponseBodyElementName(outputElementName)
        .setResponseBodyClassName("com.xignite.services.GetCurrencyIntradayChartCustomResponse")
        .build();

    return soapBodyDescriptor;
  }
}
