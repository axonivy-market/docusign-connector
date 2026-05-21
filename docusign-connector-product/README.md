# DocuSign Connector

DocuSign allows organizations to sign any document electronically on different systems. The DocuSign Connector integrates DocuSign's eSignature features into Axon Ivy processes, enabling you to create envelopes, request signatures, and retrieve signed documents directly from your workflows.

Read our [documentation](docusign-connector-product/README.md).

![Overview](images/systemDrivenProcess.png)

## Key features

- Send documents for signature and create envelopes directly from your processes.
- Generate recipient signing views for embedded signing and remote signing workflows.
- Read and download signed documents and document content streams.
- Upload and manage documents, storing signed PDFs to case documents.
- Includes demo workflows for embedded/remote signing, document upload, and inbox overview.
- Supports configurable authentication (JWT or user grant) and integration keys via variables.

## Demo

Starts a request to **sign a document**. The demo shows two options: Embedded signing and Remote signing. For demo purposes, the demo guides you through uploading a document, sending it to DocuSign, and retrieving the signed document.

### Demo workflows

#### docusign-connector-demo (docusign-connector-demo)

##### 1. Initiate a digital document signing workflow
1. Launch the demo 'Initiate a digital document signing workflow' from the demo menu.
2. Upload a document using the 'Upload Document' dialog.
3. The demo sends the document to DocuSign; wait for signers to complete.
4. After signing, the signed PDF is stored in the case documents and is available in the digital inbox.

##### 2. digital document inbox overview
1. Launch the 'digital document inbox overview' demo.
2. You'll see a list of pending and finished documents.
3. Click a document to download or view the signed PDF.

##### 3. Demo for Embedded and Remote signing
1. Launch the Embedded and Remote Signing demo.
2. Choose 'Embedded' to sign within the app, or 'Remote' to send a signing email.
3. Follow the on-screen prompts to complete signing.
4. Download or review signed documents.

## Setup

- **Roles:** Everybody (configured in config/roles.xml)

- **OpenAPI:** https://github.com/docusign/eSign-OpenAPI-Specification/raw/master/esignature.rest.swagger-v2.1.json

### Variables

```yaml
# yaml-language-server: $schema=https://json-schema.axonivy.com/app/13.2.0/variables.json
# == Variables ==
# 
# You can define here your project Variables.
# If you want to define/override a Variable for a specific Environment, 
# add an additional ‘variables.yaml’ file in a subdirectory in the ‘Config’ folder: 
# '<project>/Config/_<environment>/variables.yaml
#
Variables:
  docusignConnector:
    # Integration key from your applications settings in the DocuSign eSignature "Apps and Keys" page.
    integrationKey: ''
    
    # Secret key from your applications settings in the DocuSign eSignature "Apps and Keys" page.
    # [password]
    secretKey: ''
    
    # If set, use a specific account id, otherwise use the default account of the user. (Probably only makes sense for JWT Token grant.)
    accountId: ''
    
    # Scope of grant.
    scope: signature impersonation
    
    # Docusign base url for authentication.
    baseUri: https://account-d.docusign.com/oauth
    
    jwt:
      # If 'true' JWT token grant else user grant (default).
      use: false
      
      # User ID from your eSignature "Apps and Keys" page.
      userId: ''
      
      # Name of the key file from your applications settings in the DocuSign eSignature "Apps and Keys" page relative to the "configuration" directory.
      keyFile: 'docusign.pem'
    
    # This property provides a callback that after the signer completes or ends the signing ceremony, DocuSign redirects the user's browser back to your app via the returnUrl that you supplied in the request.
    returnPage: 'http://localhost:8081/'
    
    # This property is a string array which must include your site’s URL along with https://apps-d.docusign.com/send/ - opens in new window if your app is in the demo environment or https://apps.docusign.com - opens in new window if it is in production. Your domain must have a valid SSL certificate (such as https://my.site.com) for embedding in production environments. You can use http://localhost for development and testing.
    frameAncestors: 'http://localhost:8081/, https://apps-d.docusign.com'
    
    # This property must include https://apps-d.docusign.com/send/ - opens in new window if your app is in the demo environment or https://apps.docusign.com - opens in new window if it is in production.
    messageOrigins: 'https://apps-d.docusign.com'
```

![Apps and Keys](images/appsAndKeys.png)
![Generate RSA key](images/authenticationGenerateRSA.png)

## Components

### Connector Processes

#### Envelopes.p.json

- **createEnvelope(com.docusign.esign.model.EnvelopeDefinition envelopeDefinition)**
    - Input:
        - `envelopeDefinition` (com.docusign.esign.model.EnvelopeDefinition) - (none)
    - Result:
        - `envelopeId` (String) - (none)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) - (none)

- **createRecipientView(String envelopeId, com.docusign.esign.model.Signer signer, String returnPage)**
    - Input:
        - `envelopeId` (String) - (none)
        - `signer` (com.docusign.esign.model.Signer) - (none)
        - `returnPage` (String) - (none)
    - Result:
        - `signingUrl` (String) - (none)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) - (none)

- **readDocuments(String envelopeId)**
    - Input:
        - `envelopeId` (String) - (none)
    - Result:
        - `documents` (java.util.List<com.docusign.esign.model.EnvelopeDocument>) - (none)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) - (none)

- **getSignedDocContentStream(String envelopeId, String signedDocumentId)**
    - Input:
        - `envelopeId` (String) - (none)
        - `signedDocumentId` (String) - (none)
    - Result:
        - `signedDocumentEntity` (Object) - (none)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) - (none)

### Form Components

#### DocuSignPopup — Embedded signing popup for DocuSign
- **Namespace:** com.axonivy.connector.docusign.connector.components.DocuSignPopup
- **Component type:** JSF Composite Component
- **Fields:**
   - `integrationKey` (String)
   - `event` (String)
   - `ivyToken` (String)
- **UI attributes:**
   - `useIFrame` — To embed the url inside a native iframe tag or use DocuSign JS plugin
   - `signingURL` (required) — The recipient view url returned by DocuSign
   - `documentName` — Display name for the signing popup header
   - `callbackActionOnSigningComplete` — Client-side callback executed after signing completes
- **Where used:** Sign Document Task (DialogCall), Embedded and Remote Signing demo
- **Purpose:** Provides an embedded signing dialog that mounts DocuSign's JS signing widget.

#### DocUpload — Upload dialog
- **Namespace:** com.axonivy.connector.docusign.connector.demo.DocUpload
- **Component type:** Dialog
- **Fields:**
   - `file` (File)
- **Where used:** Demo upload workflow

#### DocSign — Signing dialog
- **Namespace:** com.axonivy.connector.docusign.connector.demo.DocSign
- **Component type:** Dialog
- **Fields:**
   - `file` (File)
   - `envelopeId` (String)
   - `uri` (String)
- **Where used:** Demo signing workflow

### Maven artifacts

1. com.axonivy.connector.docusign:docusign-connector *(required)*

```xml
<dependency>
  <groupId>com.axonivy.connector.docusign</groupId>
  <artifactId>docusign-connector</artifactId>
  <version>@version@</version>
  <type>iar</type>
</dependency>
```

2. com.axonivy.connector.docusign:docusign-connector-demo *(optional)*

```xml
<dependency>
  <groupId>com.axonivy.connector.docusign</groupId>
  <artifactId>docusign-connector-demo</artifactId>
  <version>@version@</version>
  <type>iar</type>
</dependency>
```