 # DocuSign Connector

 DocuSign ermöglicht Organisationen, jedes Dokument elektronisch auf verschiedenen Systemen zu unterschreiben.

 ![DocuSign Connector](images/application.png)

 Dieser Connector integriert DocuSign eSignature‑Funktionen in Axon Ivy‑Prozesse, sodass du Signaturanfragen senden, eingebettete Signatur‑UIs bereitstellen und signierte Dokumente direkt in deiner Anwendung abrufen kannst.

 ## Wichtigste Funktionen

 - Signaturanfragen senden und Umschläge direkt aus Axon Ivy‑Prozessen verwalten.
 - Eingebettete oder entfernte Signatur‑Abläufe mit einer einfach zu nutzenden Signatur‑UI starten.
 - Empfänger‑Signaturlinks für eingebettetes Signieren und Rücksprung‑URLs erzeugen.
 - Signierte Dokumente programmatisch abrufen und herunterladen (z. B. zur Archivierung oder Weiterverarbeitung).
 - Umschlag‑Dokumente und Metadaten prüfen, um Nachverfolgung und Automatisierung zu unterstützen.
 - Anmeldung gegen DocuSign über Integration Keys und OAuth2 für sicheren API‑Zugriff.

 ## Demo

 Sieh dir die Demo‑Implementierungen im Modul `docusign-connector-demo` an. Sie zeigen eingebettetes und entferntes Signieren, eine Inbox‑Übersicht und Beispiel‑Upload/Sign‑Abläufe.

 ### Demo‑Abläufe

 #### docusign-connector-demo (docusign-connector-demo)

 ##### 1. Starte einen digitalen Dokument‑Signatur‑Workflow
 1. Starte den Demo‑Ablauf über das Demo‑Menü.
 2. Lade ein Dokument hoch oder wähle ein Dokument aus, das signiert werden soll.

 ![eSign‑Dokumentenprozess](images/eSignDocumentProcess.png)

 3. Starte die Signaturanfrage; wähle eingebettetes Signieren oder sende eine E‑Mail‑Einladung.
 4. Nach Abschluss lade das signierte Dokument aus der Inbox herunter.

 ##### 2. Überblick über die digitale Dokumenten‑Inbox
 1. Öffne die Demo "Digital Document Inbox".
 2. Sieh dir abgeschlossene und ausstehende Dokumente an.
 3. Öffne ein Dokument, um Details zu prüfen oder die signierte PDF herunterzuladen.

 ##### 3. Demo für eingebettetes und Remote‑Signieren
 1. Starte die Demo für eingebettetes und Remote‑Signieren.
 2. Wähle "Embedded", um innerhalb der App zu signieren, oder "Remote", um einen E‑Mail‑Link zu senden.
 3. Schließe den Signaturvorgang ab und prüfe die gespeicherten Dokumente.

 ## Einrichtung

 - **Rollen:** Everybody (konfiguriert in `config/roles.xml`)

 - **OpenAPI:** https://github.com/docusign/eSign-OpenAPI-Specification/raw/master/esignature.rest.swagger-v2.1.json (Namespace: com.docusign.esign.model)

 ### Variablen

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

 - Zu diesem Abschnitt wurden keine weiteren Informationen geliefert.

 ## Komponenten

 ### Connector‑Prozesse

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

 ### Formular‑Komponenten

 #### DocuSignPopup — Eingebetteter Signatur‑Popup
 - **Namespace:** com.axonivy.connector.docusign.connector.components.DocuSignPopup
 - **Komponententyp:** JSF Composite Component
 - **Felder:**
    - `integrationKey` (String) — 
    - `event` (String) — 
    - `ivyToken` (String) — 
 - **UI‑Attribute:**
    - `useIFrame` — To embedded your url inside a native iframe tag or use DocuSign JS plugin
    - `signingURL` — The recipient view url that return by accounts/placeholder/envelopes/{envelopeId}/views/recipient endpoint
    - `documentName` — To display on the header-text of signing popup
    - `callbackActionOnSigningComplete` — A client side callback to execute after signing requests completed successfully
 - **Where used:** DocuSignPopupProcess (HTML_DIALOG)
 - **Zweck:** Zeigt eine eingebettete DocuSign‑Signatur‑UI in einem Dialog an.

 ### Maven‑Artefakte

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
