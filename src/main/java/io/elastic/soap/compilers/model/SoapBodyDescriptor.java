package io.elastic.soap.compilers.model;

/**
 * Class represents a structure for storing data about correlations of WSDL's binding, operation,
 * and corresponding generated class name and body element name. Since these values can have
 * different values.
 * E.g.:
 * Binding: BLZServiceSOAP12Binding
 * Operation: getBank Element Name: getBank
 * Element Type: tns:getBankType
 */
public class SoapBodyDescriptor {

  private String requestBodyElementName;
  private String responseBodyElementName;
  private String requestBodyClassName;
  private String responseBodyClassName;
  private String requestBodyPackageName;
  private String responseBodyPackageName;
  private String requestBodyNameSpace;
  private String responseBodyNameSpace;
  private String operationName;
  private String soapAction;
  private String soapEndPoint;
  private String bindingName;

  public SoapBodyDescriptor() {
  }

  public String getSoapEndPoint() {
    return soapEndPoint;
  }

  public void setSoapEndPoint(String soapEndPoint) {
    this.soapEndPoint = soapEndPoint;
  }

  public String getSoapAction() {
    return soapAction;
  }

  public void setSoapAction(String soapAction) {
    this.soapAction = soapAction;
  }

  public String getRequestBodyNameSpace() {
    return requestBodyNameSpace;
  }

  public String getResponseBodyNameSpace() {
    return responseBodyNameSpace;
  }

  public void setRequestBodyNameSpace(String requestBodyNameSpace) {
    this.requestBodyNameSpace = requestBodyNameSpace;
  }

  public void setResponseBodyNameSpace(String responseBodyNameSpace) {
    this.responseBodyNameSpace = responseBodyNameSpace;
  }

  public String getRequestBodyPackageName() {
    return requestBodyPackageName;
  }

  public String getResponseBodyPackageName() {
    return responseBodyPackageName;
  }

  public void setResponseBodyPackageName(String responseBodyPackageName) {
    this.responseBodyPackageName = responseBodyPackageName;
  }

  public void setRequestBodyPackageName(String requestBodyPackageName) {
    this.requestBodyPackageName = requestBodyPackageName;
  }

  public String getRequestBodyElementName() {
    return requestBodyElementName;
  }

  public void setRequestBodyElementName(String requestBodyElementName) {
    this.requestBodyElementName = requestBodyElementName;
  }

  public String getResponseBodyElementName() {
    return responseBodyElementName;
  }

  public void setResponseBodyElementName(String responseBodyElementName) {
    this.responseBodyElementName = responseBodyElementName;
  }

  public String getRequestBodyClassName() {
    return requestBodyClassName;
  }

  public void setRequestBodyClassName(String requestBodyClassName) {
    this.requestBodyClassName = requestBodyClassName;
  }

  public String getResponseBodyClassName() {
    return responseBodyClassName;
  }

  public void setResponseBodyClassName(String responseBodyClassName) {
    this.responseBodyClassName = responseBodyClassName;
  }

  public String getOperationName() {
    return operationName;
  }

  public void setOperationName(String operationName) {
    this.operationName = operationName;
  }

  public String getBindingName() {
    return bindingName;
  }

  public void setBindingName(String bindingName) {
    this.bindingName = bindingName;
  }

  @Override
  public String toString() {
    return "SoapBodyDescriptor{" +
        "requestBodyElementName='" + requestBodyElementName + '\'' +
        ", responseBodyElementName='" + responseBodyElementName + '\'' +
        ", requestBodyClassName='" + requestBodyClassName + '\'' +
        ", responseBodyClassName='" + responseBodyClassName + '\'' +
        ", requestBodyPackageName='" + requestBodyPackageName + '\'' +
        ", responseBodyPackageName='" + responseBodyPackageName + '\'' +
        ", requestBodyNameSpace='" + requestBodyNameSpace + '\'' +
        ", responseBodyNameSpace='" + responseBodyNameSpace + '\'' +
        ", operationName='" + operationName + '\'' +
        ", soapAction='" + soapAction + '\'' +
        ", bindingName='" + bindingName + '\'' +
        '}';
  }
}
