# DocuSign Connector

The DocuSign connector integrates Axon Ivy processes with DocuSign to send signature requests, embed signing flows in your UI, and retrieve signed documents automatically. It helps automate envelope creation, recipient view generation, and document retrieval from within your Axon Ivy applications.

**Key features**

- Send envelopes and manage signatures directly from your Axon Ivy processes, reducing manual effort.
- Embed DocuSign signing flows into your application for seamless in-app signing experiences.
- Programmatically create recipient signing views to support custom return pages and embedded signing.
- Retrieve and store signed documents automatically for downstream processing and archiving.
- Support configurable authentication (Integration Key and optional JWT) via project variables.
- Includes demo workflows demonstrating embedded vs. remote signing and a digital document inbox.

## Demo

Check the demo implementations provided in the demo module; they showcase embedded and remote signing flows, the document inbox, and sample integrations.

![Embedded signing](images/eSignDocumentProcess.png)

### Demo Workflows

#### docusign-connector-demo (docusign-connector-demo)

##### Initiate a digital document signing workflow
1. Launch the "Initiate a digital document signing workflow" demo from the menu.
2. Upload a document using the provided upload dialog.
3. Configure signers and recipient details, then start the signing request.
4. Monitor signing progress and download completed documents.

##### digital document inbox overview
1. Open the digital document inbox demo.
2. Review finished and pending documents listed in the inbox.
3. Open a signed document to preview or download it.
4. Use the provided filters or search to locate specific documents.

##### Demo for Embedded and Remote signing function
1. Start the Embedded and Remote Signing demo.
2. Choose embedded signing (in-app) or remote signing (email link) and follow the prompts.
3. Complete the signing ceremony and return to the application.
4. Download or store the signed documents as needed.

## Setup

- **Roles:** Everybody (configured in config/roles.xml)
- **OpenAPI:** https://github.com/docusign/eSign-OpenAPI-Specification/raw/master/esignature.rest.swagger-v2.1.json (Namespace: com.docusign.esign.model)

### Variables

```
@variables.yaml@
```


## Components

### Callable Subprocesses

#### Envelopes.p.json

- **Signature**: createEnvelope(com.docusign.esign.model.EnvelopeDefinition envelopeDefinition) -> envelopeId: String
    - Input:
        - `envelopeDefinition` (com.docusign.esign.model.EnvelopeDefinition)
    - Result:
        - `envelopeId` (String)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError)

- **Signature**: createRecipientView(String envelopeId, com.docusign.esign.model.Signer signer, String returnPage) -> signingUrl: String
    - Input:
        - `envelopeId` (String)
        - `signer` (com.docusign.esign.model.Signer)
        - `returnPage` (String)
    - Result:
        - `signingUrl` (String)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError)

- **Signature**: readDocuments(String envelopeId) -> documents: java.util.List<com.docusign.esign.model.EnvelopeDocument>
    - Input:
        - `envelopeId` (String)
    - Result:
        - `documents` (java.util.List<com.docusign.esign.model.EnvelopeDocument>)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError)

- **Signature**: getSignedDocContentStream(String envelopeId, String signedDocumentId) -> signedDocumentEntity: Object
    - Input:
        - `envelopeId` (String)
        - `signedDocumentId` (String)
    - Result:
        - `signedDocumentEntity` (Object)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError)

### Dialog Components

#### DocuSignPopup — Embedded signing & popup component
- **Namespace:** com.axonivy.connector.docusign.connector.components.DocuSignPopup
- **Component type:** Component dialog
- **Fields:**
    - (none)
- **UI attributes:**
    - `useIFrame` — Whether to embed the signing URL inside a native iframe or to use the DocuSign JavaScript plugin.
    - `signingURL` — The recipient view URL returned by the accounts/{accountId}/envelopes/{envelopeId}/views/recipient endpoint.
    - `documentName` — Text displayed in the signing popup header.
    - `callbackActionOnSigningComplete` — Client-side callback executed after the signing flow completes successfully.
- **Purpose:** Provides an embeddable signing dialog that mounts the DocuSign signing UI inside your application.

### Web Services

- https://github.com/docusign/eSign-OpenAPI-Specification/raw/master/esignature.rest.swagger-v2.1.json

### Maven Artifacts

1. docusign-connector

```xml
<dependency>
  <groupId>com.axonivy.connector.docusign</groupId>
  <artifactId>docusign-connector</artifactId>
  <type>iar</type>
</dependency>
```

2. docusign-connector-demo

```xml
<dependency>
  <groupId>com.axonivy.connector.docusign</groupId>
  <artifactId>docusign-connector-demo</artifactId>
  <type>iar</type>
</dependency>
```

