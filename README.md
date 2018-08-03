# soap-component-java
## Description
The SOAP Component provides the SOAP Web Services work opportunity within a elastic.io flow.

### Purpose

As integration platform, elastic.io should has opportuity to invoke SOAP Web services over HTTP.

### How works
#### Step 1 - Find and select Soap component from repository
![Step 1](https://user-images.githubusercontent.com/13310949/43515103-5de72b58-958a-11e8-88ce-5870003867a1.png)
#### Step 2 - Create and select credentials
![Step 2](https://user-images.githubusercontent.com/13310949/43514620-3c2b9efa-9589-11e8-9d9e-c82b1d66e5eb.png)
#### Step 3 - Specify WSDL URL, choose binding and operation
![Step 3](https://user-images.githubusercontent.com/13310949/43522182-365e9fbe-95a1-11e8-8226-3e3679afbe17.png)
#### Step 4 - Configure input data and click "Continue"
![Step 4](https://user-images.githubusercontent.com/13310949/43514773-9036472a-9589-11e8-83d6-95759f1a2cc9.png)
#### Step 5 - Retrieve sample or add sample manually
![Step 5: Retrieve sample](https://user-images.githubusercontent.com/13310949/43514839-bace8e16-9589-11e8-92d2-e54890472dbb.png)

#### Step 6 - Retrieve sample result
![Step 6: Retrieve sample result](https://user-images.githubusercontent.com/13310949/43515232-aca5be76-958a-11e8-95a0-c723f9323e4f.png)
### Requirements
The platform supports next SOAP protocol versions:
* SOAP 1.1
* SOAP 1.2

Component supports next wsdl styles:
* RPC/Literal
* Document/Encoded
* Document/Literal

#### Environment variables
``` EIO_REQUIRED_RAM_MB - recommended value of memory is 2056 Mb ```
## Credentials

### Type
You can select next authorization type:
* **No Auth**
* **Basic Auth**
* **API Key Auth** <span style="color:red">(*has not supported yet*)</span>.
### Username (Basic auth type)
Username for Basic authorization header
### Password (Basic auth type)
Password for Basic authorization header

## Actions
### Call
Make a call to SOAP over HTTP service using public WSDL URL

#### Input fields description
* **WSDL URI** - public URL address of wsdl
* **Binding** - Binding described in WSDL, which you want to use for SOAP call
* **Operation** - Operation in selected above binding, which you want to use for SOAP call

#### Input json schema location
Input json schema  generates from specified early wsdl, binding and operation dynamically, using [Apache Axis2](http://axis.apache.org/axis2/java/core/) core engine and [FasterXML JsonSchemaGenerator](https://github.com/FasterXML/jackson-module-jsonSchema) .
#### Output json schema location (if exists)
Output json schema  generates from specified early wsdl, binding and operation dynamically, using [Apache Axis2](http://axis.apache.org/axis2/java/core/) core engine and [FasterXML JsonSchemaGenerator](https://github.com/FasterXML/jackson-module-jsonSchema) .

## Additional info
<span style="color:red">You should specify action fields in next sequence:</span>.
* WSDL URI
* Binding
* Operation

### Current limitations
The following are limitations of this connector:

* RPC/SOAP-Encoded styles are not supported.

`
All major frameworks for web services support Document/literal messages. Most of the popular frameworks also have some support for rpc/encoded, so developers can still use it to create encoded-only services.
As a result it is hard to estimate how many web services, in production use, work only with SOAP encoded messages.
However there is a tendency to move away from RPC/encoded towards Document/literal.
This is so, because the SOAP encoding specification does not guarantee 100% interoperability and there are vendor deviations in the implementation of RPC/encoded.
`

* Only self-containing wsdls are supported now.
* WS-Security header isn`t supported.
* WS-Addressing osn`t supported now.
* There is no ability to set values in SOAP headers.
* The WSDL and associated schemas must be accessible via a publicly accessible URL. File upload of the WSDL and/or XSD schemas is not supported.
* Component doesn't support multipart in SOAP request element. Only first part of request element will be processed.

## API and Documentation links
* [Apache Axis2](http://axis.apache.org/axis2/java/core/)
* [FasterXML JsonSchemaGenerator](https://github.com/FasterXML/jackson-module-jsonSchema)