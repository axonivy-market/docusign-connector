# DocuSign Connector

DocuSign ermöglicht Organisationen, jedes Dokument elektronisch auf unterschiedlichen Systemen zu signieren. Der DocuSign Connector integriert die eSignature-Funktionen von DocuSign in Axon Ivy-Prozesse, sodass du Umschläge erstellen, Signaturen anfordern und signierte Dokumente direkt aus deinen Workflows abrufen kannst.

Siehe unsere [Dokumentation](docusign-connector-product/README.md).

![Overview](images/systemDrivenProcess.png)

## Wichtigste Funktionen

- Sende Dokumente zur Signatur und erstelle Umschläge direkt aus deinen Prozessen.
- Erzeuge Empfänger-Signing-Views für eingebettetes und Remote-Signing.
- Lese und lade signierte Dokumente und Dokument-Inhaltsströme herunter.
- Lade und verwalte Dokumente; speichere signierte PDFs in Falldokumenten.
- Enthält Demo-Workflows für Embedded/Remote-Signing, Dokument-Upload und Postfach-Übersicht.
- Unterstützt konfigurierbare Authentifizierung (JWT oder Benutzer-Grant) und Integration-Keys über Variablen.

## Demo

Startet eine Anforderung, um ein Dokument zu signieren. Die Demo zeigt zwei Optionen: eingebettetes Signieren (Embedded) und Remote-Signing. Für Demozwecke führt die Demo durch das Hochladen eines Dokuments, das Senden an DocuSign und das Abrufen des signierten Dokuments.

### Demo-Workflows

#### docusign-connector-demo (docusign-connector-demo)

##### 1. Initiate a digital document signing workflow
1. Starte die Demo 'Initiate a digital document signing workflow' über das Demo-Menü.
2. Lade ein Dokument via 'Upload Document'-Dialog hoch.
3. Die Demo sendet das Dokument an DocuSign; warte auf den Abschluss durch die Unterzeichner.
4. Nach der Signatur wird das signierte PDF in den Falldokumenten gespeichert und ist im Postfach verfügbar.

##### 2. digital document inbox overview
1. Starte die Demo 'digital document inbox overview'.
2. Du siehst eine Liste mit ausstehenden und fertigen Dokumenten.
3. Klicke ein Dokument an, um das signierte PDF herunterzuladen oder anzusehen.

##### 3. Demo for Embedded and Remote signing
1. Starte die Demo für Embedded und Remote Signing.
2. Wähle 'Embedded', um innerhalb der App zu signieren, oder 'Remote', um eine Signatur-E-Mail zu senden.
3. Folge den Anweisungen auf dem Bildschirm, um die Signatur abzuschließen.
4. Lade das signierte Dokument herunter oder überprüfe es.

## Einrichtung

- **Rollen:** Everybody (konfiguriert in config/roles.xml)

- **OpenAPI:** https://github.com/docusign/eSign-OpenAPI-Specification/raw/master/esignature.rest.swagger-v2.1.json

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
![Generate RSA key](images/authenticationGenerateRSA.png)

## Komponenten

### Connector-Prozesse

#### Envelopes.p.json

- **createEnvelope(com.docusign.esign.model.EnvelopeDefinition envelopeDefinition)**
    - Eingabe:
        - `envelopeDefinition` (com.docusign.esign.model.EnvelopeDefinition) - (keine Beschreibung)
    - Ergebnis:
        - `envelopeId` (String) - (keine Beschreibung)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) - (keine Beschreibung)

- **createRecipientView(String envelopeId, com.docusign.esign.model.Signer signer, String returnPage)**
    - Eingabe:
        - `envelopeId` (String)
        - `signer` (com.docusign.esign.model.Signer)
        - `returnPage` (String)
    - Ergebnis:
        - `signingUrl` (String)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError)

- **readDocuments(String envelopeId)**
    - Eingabe:
        - `envelopeId` (String)
    - Ergebnis:
        - `documents` (java.util.List<com.docusign.esign.model.EnvelopeDocument>)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError)

- **getSignedDocContentStream(String envelopeId, String signedDocumentId)**
    - Eingabe:
        - `envelopeId` (String)
        - `signedDocumentId` (String)
    - Ergebnis:
        - `signedDocumentEntity` (Object)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError)

### Formularkomponenten

#### DocuSignPopup — Eingebettetes Signing-Popup für DocuSign
- **Namespace:** com.axonivy.connector.docusign.connector.components.DocuSignPopup
- **Komponententyp:** JSF Composite Component
- **Felder:**
   - `integrationKey` (String)
   - `event` (String)
   - `ivyToken` (String)
- **UI-Attribute:**
   - `useIFrame` — Für die Einbettung per iframe oder die DocuSign JS-Integration
   - `signingURL` (erforderlich) — Die Empfänger-View-URL von DocuSign
   - `documentName` — Anzeigename für den Header des Signing-Popups
   - `callbackActionOnSigningComplete` — Client-seitiger Callback nach erfolgreichem Signing
- **Verwendung:** Sign Document Task (DialogCall), Embedded and Remote Signing Demo
- **Zweck:** Stellt einen eingebetteten Signing-Dialog bereit, der das DocuSign JS-Widget nutzt.

#### DocUpload — Upload-Dialog
- **Namespace:** com.axonivy.connector.docusign.connector.demo.DocUpload
- **Komponententyp:** Dialog
- **Felder:**
   - `file` (File)

#### DocSign — Signing-Dialog
- **Namespace:** com.axonivy.connector.docusign.connector.demo.DocSign
- **Komponententyp:** Dialog
- **Felder:**
   - `file` (File)
   - `envelopeId` (String)
   - `uri` (String)

### Maven-Artefakte

1. com.axonivy.connector.docusign:docusign-connector *(erforderlich)*

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