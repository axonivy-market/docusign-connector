# DocuSign Connector

DocuSign allows organizations to sign any document electronically on different systems.

![Application overview](images/application.png)

The DocuSign connector integrates DocuSign eSignature services into your Axon Ivy processes, enabling you to send signature requests, create recipient signing sessions, list and download signed documents, and embed the signing experience in your application. See the DocuSign REST API: https://www.docusign.net/restapi

## Key features

- Send signature requests and manage recipients directly from your Axon Ivy processes.
- Embed signing sessions in your application or redirect users to DocuSign's signing page.
- List and download signed documents and envelope contents programmatically.
- Support for OAuth2 and optional JWT authentication with configurable integration key and key file.
- Includes demo workflows for embedded and remote signing, inbox overview, and document upload.
- Provides an OpenAPI spec and REST client integration for advanced automation.

## Demo

Check the demo implementations provided for the DocuSign connector, including embedded and remote signing, an inbox overview, and document upload demos.

### Demo workflows

#### docusign-connector-demo (docusign-connector-demo)

##### 1. Initiate a digital document signing workflow
1. Launch the "Initiate a digital document signing workflow" demo from the demo menu.
2. Upload or select a document to sign.
3. Add one or more signers (name + email) and start the signing request.
4. Review the signing process and download the signed document when complete.

![eSign document process](images/eSignDocumentProcess.png)

##### 2. digital document inbox overview
1. Open the digital document inbox demo to inspect finished and pending documents.
2. Select a document to view details and available actions.
3. Download or open signed documents from the inbox.

![System driven process](images/systemDrivenProcess.png)

##### 3. Embedded and Remote signing
1. Launch the Embedded and Remote signing demo.
2. Choose between embedded signing (in-app) or remote signing (redirect to DocuSign).
3. Fill in participant details and start the signing session.
4. Confirm signing completion and return to the application.

![Signing finished](images/docuSign_finish.png)

## Setup

- **Roles:** Everybody (configured in config/roles.xml).
- **OpenAPI:** https://github.com/docusign/eSign-OpenAPI-Specification/raw/master/esignature.rest.swagger-v2.1.json  (Namespace: com.docusign.esign.model)

### Variables

```
@variables.yaml@
```

## Components

### Connector Processes

#### Envelopes.p.json

- **createEnvelope(com.docusign.esign.model.EnvelopeDefinition envelopeDefinition) -> envelopeId: String**
    - Input:
        - `envelopeDefinition` (com.docusign.esign.model.EnvelopeDefinition) — (no description available)
    - Result:
        - `envelopeId` (String) — (no description available)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) — (no description available)

- **createRecipientView(String envelopeId, com.docusign.esign.model.Signer signer, String returnPage) -> signingUrl: String**
    - Input:
        - `envelopeId` (String) — (no description available)
        - `signer` (com.docusign.esign.model.Signer) — (no description available)
        - `returnPage` (String) — (no description available)
    - Result:
        - `signingUrl` (String) — (no description available)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) — (no description available)

- **readDocuments(String envelopeId) -> documents: java.util.List<com.docusign.esign.model.EnvelopeDocument>**
    - Input:
        - `envelopeId` (String) — (no description available)
    - Result:
        - `documents` (java.util.List<com.docusign.esign.model.EnvelopeDocument>) — (no description available)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) — (no description available)

- **getSignedDocContentStream(String envelopeId, String signedDocumentId) -> signedDocumentEntity: Object**
    - Input:
        - `envelopeId` (String) — (no description available)
        - `signedDocumentId` (String) — (no description available)
    - Result:
        - `signedDocumentEntity` (Object) — (no description available)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) — (no description available)

### Form Components

#### DocuSignPopup — Embedded signing dialog (HTML dialog / JSF composite)
- **Namespace:** com.axonivy.connector.docusign.connector.components.DocuSignPopup
- **Component type:** HTML_DIALOG / JSF Composite Component
- **Fields:**
   - `integrationKey` (String) — (no description available)
   - `event` (String) — (no description available)
   - `ivyToken` (String) — (no description available)
- **Where used:** DocuSignPopup HTML dialog process
- **Purpose:** Provides an embedded signing dialog that can use either an iframe or the DocuSign JS plugin to perform embedded signing.

### Maven artifacts

1. docusign-connector

```xml
<dependency>
  <groupId>com.axonivy.connector.docusign</groupId>
  <artifactId>docusign-connector</artifactId>
  <version>@version@</version>
  <type>iar</type>
</dependency>
```

2. docusign-connector-demo *(optional)*

```xml
<dependency>
  <groupId>com.axonivy.connector.docusign</groupId>
  <artifactId>docusign-connector-demo</artifactId>
  <version>@version@</version>
  <type>iar</type>
</dependency>
```
