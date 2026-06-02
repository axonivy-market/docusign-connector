# DocuSign Connector

Der DocuSign Connector integriert Axon Ivy Prozesse mit DocuSign, damit du Signaturanfragen senden, Signaturabläufe in deine UI einbetten und signierte Dokumente automatisch abrufen kannst.

**Wichtigste Funktionen**

- Sende Umschläge direkt aus deinen Axon Ivy Workflows und reduziere manuelle Übergaben.
- Biete eingebettete und remote Signaturerlebnisse passend zu deinem Prozess.
- Erzeuge Empfänger-Signieransichten programmatisch für individuelle Return-Page-Flows.
- Verfolge signierte Dokumente und speichere sie für die weitere Verarbeitung im Case-Kontext.
- Konfiguriere die Authentifizierung mit Integration Key und optionalem JWT über Projektvariablen.
- Nutze sofort lauffähige Demo-Workflows für Signatur, Inbox-Übersicht und Abschlussprüfung.

## Demo

Nutze das Demo-Modul, um End-to-End-Signaturszenarien zu testen, eingebettetes und remote Signieren zu vergleichen und abgeschlossene Dokumente zu prüfen.

![Embedded signing](images/eSignDocumentProcess.png)

### Demo Workflows

#### docusign-connector-demo (docusign-connector-demo)

##### 1. Starte einen Workflow zur digitalen Dokumentensignatur
1. Starte den Workflow aus dem Demo-Menü.
2. Lade ein Dokument hoch, das signiert werden soll.
3. Starte die Signaturanfrage und warte auf den Abschluss durch die unterzeichnende Person.
4. Prüfe und lade das signierte Dokument aus dem Case-Kontext herunter.

##### 2. Übersicht für den digitalen Dokumenten-Posteingang
1. Öffne den Workflow zur Dokumenten-Posteingang-Übersicht im Demo-Menü.
2. Sieh ausstehende und abgeschlossene Signaturaufgaben an einem Ort.
3. Öffne einen abgeschlossenen Eintrag und prüfe die verfügbaren Dateien.
4. Fahre mit dem nächsten fachlichen Schritt basierend auf dem Signaturstatus fort.

##### 3. Demo für eingebettete und remote Signaturfunktion
1. Starte die Demo für eingebettetes und remote Signieren.
2. Wähle eingebettetes Signieren für In-App-Flow oder remote Signieren für E-Mail-Flow.
3. Schließe den Signaturablauf ab und kehre in deine Anwendung zurück.
4. Bestätige den Abschluss und führe die weitere Dokumentenverarbeitung aus.

## Setup

- **Rollen:** Everybody (konfiguriert in config/roles.xml)
- **OpenAPI:** https://github.com/docusign/eSign-OpenAPI-Specification/raw/master/esignature.rest.swagger-v2.1.json

1. Öffne in DocuSign den Bereich Apps and Keys und übernimm Integration Key sowie Secret Key.
2. Konfiguriere die Connector-Werte in `config/variables.yaml` für deine Umgebung.
3. Wenn du JWT verwendest, setze `docusignConnector.jwt.use` auf `true` und hinterlege `userId` sowie `keyFile`.
4. Starte einen Demo-Workflow, um Authentifizierung, Signaturablauf und Callback-Verhalten zu prüfen.

### Variables

```
@variables.yaml@
```

## Components

### Callable Subprocesses

#### Envelopes.p.json

- **Signatur**: createEnvelope(com.docusign.esign.model.EnvelopeDefinition envelopeDefinition) -> envelopeId: String
    - Eingaben:
        - `envelopeDefinition` (com.docusign.esign.model.EnvelopeDefinition)
    - Ergebnis:
        - `envelopeId` (String)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError)

- **Signatur**: createRecipientView(String envelopeId, com.docusign.esign.model.Signer signer, String returnPage) -> signingUrl: String
    - Eingaben:
        - `envelopeId` (String)
        - `signer` (com.docusign.esign.model.Signer)
        - `returnPage` (String)
    - Ergebnis:
        - `signingUrl` (String)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError)

- **Signatur**: readDocuments(String envelopeId) -> documents: java.util.List<com.docusign.esign.model.EnvelopeDocument>
    - Eingaben:
        - `envelopeId` (String)
    - Ergebnis:
        - `documents` (java.util.List<com.docusign.esign.model.EnvelopeDocument>)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError)

- **Signatur**: getSignedDocContentStream(String envelopeId, String signedDocumentId) -> signedDocumentEntity: Object
    - Eingaben:
        - `envelopeId` (String)
        - `signedDocumentId` (String)
    - Ergebnis:
        - `signedDocumentEntity` (Object)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError)

### Dialogkomponenten

#### DocuSignPopup — Eingebettete Signatur- und Popup-Komponente
- **Namespace:** com.axonivy.connector.docusign.connector.components.DocuSignPopup
- **Komponententyp:** Component dialog
- **Felder:**
    - (none)
- **UI-Attribute:**
    - `useIFrame` — Steuert, ob die Signing-URL in einem nativen iframe oder über das DocuSign JavaScript Plugin eingebettet wird.
    - `signingURL` — Die Recipient-View-URL aus dem Endpoint accounts/{accountId}/envelopes/{envelopeId}/views/recipient.
    - `documentName` — Text, der im Header des Signatur-Popups angezeigt wird.
    - `callbackActionOnSigningComplete` — Client-seitiger Callback nach erfolgreichem Abschluss des Signaturablaufs.
- **Zweck:** Wiederverwendbare Popup-Komponente für eingebettetes DocuSign-Signieren in deiner Anwendung.

### Web-Services

- https://github.com/docusign/eSign-OpenAPI-Specification/raw/master/esignature.rest.swagger-v2.1.json

### Maven-Artefakte

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
