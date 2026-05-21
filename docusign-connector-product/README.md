 # DocuSign Connector

 DocuSign allows organizations to sign any document electronically on different systems.

 ![DocuSign Connector](images/application.png)

 This connector integrates DocuSign eSignature features into Axon Ivy processes, enabling you to send signature requests, present embedded signing UIs, and retrieve signed documents directly from your application.

 ## Key features

 - Send signature requests and manage envelopes directly from Axon Ivy processes.
 - Start embedded or remote signing flows with an easy-to-use signing UI.
 - Generate recipient signing links for embedded signing and return URLs for recipients.
 - Retrieve and download signed documents programmatically for archival or processing.
 - Inspect envelope documents and metadata to support tracking and automation.
 - Authenticate against DocuSign using integration keys and OAuth2 for secure API access.

 ## Demo

 Check the demo implementations provided in the `docusign-connector-demo` module. They demonstrate embedded and remote signing, an inbox overview, and example upload/sign flows.

 ### Demo workflows

 #### docusign-connector-demo (docusign-connector-demo)

 ##### 1. Initiate a digital document signing workflow
 1. Launch the demo from the demo menu.
 2. Upload or select a document to be signed.

 ![eSign Document Process](images/eSignDocumentProcess.png)

 3. Start the signature request; choose embedded signing or send an email invitation.
 4. After signing completes, download the signed document from the inbox.

 ##### 2. digital document inbox overview
 1. Launch the Digital Document Inbox demo.
 2. View finished and pending documents.
 3. Open a document to inspect details or download the signed PDF.

 ##### 3. Demo for Embedded and Remote signing function
 1. Launch the Embedded and Remote Signing demo.
 2. Choose Embedded to sign inside the app or Remote to send an email link.
 3. Complete the signing flow and verify the stored documents.

 ## Setup

 - **Roles:** Everybody (configured in config/roles.xml)

 - **OpenAPI:** https://github.com/docusign/eSign-OpenAPI-Specification/raw/master/esignature.rest.swagger-v2.1.json (Namespace: com.docusign.esign.model)

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

 - No information was delivered for this section.

 ## Components

 ### Connector Processes

 #### Envelopes.p.json

 - **createEnvelope(com.docusign.esign.model.EnvelopeDefinition envelopeDefinition) -> envelopeId: String, error: ch.ivyteam.ivy.bpm.error.BpmError**
     - Input:
         - `envelopeDefinition` (com.docusign.esign.model.EnvelopeDefinition) - 
     - Result:
         - `envelopeId` (String) - 
         - `error` (ch.ivyteam.ivy.bpm.error.BpmError) - 

 - **createRecipientView(String envelopeId, com.docusign.esign.model.Signer signer, String returnPage) -> signingUrl: String, error: ch.ivyteam.ivy.bpm.error.BpmError**
     - Input:
         - `envelopeId` (String) - 
         - `signer` (com.docusign.esign.model.Signer) - 
         - `returnPage` (String) - 
     - Result:
         - `signingUrl` (String) - 
         - `error` (ch.ivyteam.ivy.bpm.error.BpmError) - 

 - **readDocuments(String envelopeId) -> documents: java.util.List<com.docusign.esign.model.EnvelopeDocument>, error: ch.ivyteam.ivy.bpm.error.BpmError**
     - Input:
         - `envelopeId` (String) - 
     - Result:
         - `documents` (java.util.List<com.docusign.esign.model.EnvelopeDocument>) - 
         - `error` (ch.ivyteam.ivy.bpm.error.BpmError) - 

 - **getSignedDocContentStream(String envelopeId, String signedDocumentId) -> signedDocumentEntity: Object, error: ch.ivyteam.ivy.bpm.error.BpmError**
     - Input:
         - `envelopeId` (String) - 
         - `signedDocumentId` (String) - 
     - Result:
         - `signedDocumentEntity` (Object) - 
         - `error` (ch.ivyteam.ivy.bpm.error.BpmError) - 

 ### Form Components

 #### DocuSignPopup — Embedded signing popup
 - **Namespace:** com.axonivy.connector.docusign.connector.components.DocuSignPopup
 - **Component type:** JSF Composite Component
 - **Fields:**
    - `integrationKey` (String) — 
    - `event` (String) — 
    - `ivyToken` (String) — 
 - **UI attributes:**
    - `useIFrame` — To embedded your url inside a native iframe tag or use DocuSign JS plugin
    - `signingURL` — The recipient view url that return by accounts/placeholder/envelopes/{envelopeId}/views/recipient endpoint
    - `documentName` — To display on the header-text of signing popup
    - `callbackActionOnSigningComplete` — A client side callback to execute after signing requests completed successfully
 - **Where used:** DocuSignPopupProcess (HTML_DIALOG)
 - **Purpose:** Display an embedded DocuSign signing UI inside a dialog.

 ### Maven artifacts

 1. com.axonivy.connector.docusign:docusign-connector:@version@

 ```xml
 <dependency>
   <groupId>com.axonivy.connector.docusign</groupId>
   <artifactId>docusign-connector</artifactId>
   <version>@version@</version>
   <type>iar</type>
 </dependency>
 ```

 2. com.axonivy.connector.docusign:docusign-connector-demo:@version@

 ```xml
 <dependency>
   <groupId>com.axonivy.connector.docusign</groupId>
   <artifactId>docusign-connector-demo</artifactId>
   <version>@version@</version>
   <type>iar</type>
 </dependency>
 ```
