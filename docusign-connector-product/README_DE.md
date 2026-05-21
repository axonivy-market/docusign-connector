# DocuSign Connector

DocuSign ermöglicht Organisationen, beliebige Dokumente elektronisch auf verschiedenen Systemen zu signieren.

![Anwendungsübersicht](images/application.png)

Der DocuSign-Connector integriert die DocuSign eSignature-Dienste in deine Axon Ivy-Prozesse. Du kannst damit Signaturanfragen senden, Empfänger-Signiersitzungen erstellen, signierte Dokumente auflisten und herunterladen sowie das Signiererlebnis in deine Anwendung einbetten. Siehe die DocuSign REST API: https://www.docusign.net/restapi

## Wichtigste Funktionen

- Sende Signaturanfragen und verwalte Empfänger direkt aus deinen Axon Ivy-Prozessen.
- Bette Signatur-Sitzungen in deine Anwendung ein oder leite Nutzer zur DocuSign-Signierseite weiter.
- Liste und lade signierte Dokumente und den Inhalt von Umschlägen programmgesteuert herunter.
- Unterstützung für OAuth2 und optionale JWT-Authentifizierung mit konfigurierbarem Integration Key und Key-Datei.
- Beinhaltet Demo-Workflows für eingebettetes und Remote-Signing, Posteingangsübersicht und Dokumentenupload.
- Stellt eine OpenAPI-Spezifikation und REST-Client-Integration für erweiterte Automatisierung bereit.

## Demo

Probiere die bereitgestellten Demo-Implementierungen für den DocuSign-Connector aus, darunter eingebettetes und Remote-Signing, eine Posteingangsübersicht und Dokumentenupload-Demos.

### Demo-Workflows

#### docusign-connector-demo (docusign-connector-demo)

##### 1. Starte einen digitalen Dokument-Signatur-Workflow
1. Starte im Demo-Menü den digitalen Dokument-Signatur-Workflow.
2. Lade ein Dokument hoch oder wähle ein vorhandenes Dokument zum Signieren aus.
3. Füge einen oder mehrere Unterzeichner (Name + E-Mail) hinzu und starte die Signaturanfrage.
4. Prüfe den Signiervorgang und lade das signierte Dokument herunter, sobald er abgeschlossen ist.

![eSign-Dokumentprozess](images/eSignDocumentProcess.png)

##### 2. Posteingangsübersicht für digitale Dokumente
1. Öffne die Demo "Posteingangsübersicht", um fertiggestellte und ausstehende Dokumente zu prüfen.
2. Wähle ein Dokument, um Details und verfügbare Aktionen anzuzeigen.
3. Lade signierte Dokumente herunter oder öffne sie.

![Systemgesteuerter Vorgang](images/systemDrivenProcess.png)

##### 3. Eingebettetes und Remote-Signing
1. Starte die Demo "Eingebettetes und Remote-Signing".
2. Wähle zwischen eingebettetem Signing (in der App) oder Remote-Signing (Weiterleitung zu DocuSign).
3. Gib Teilnehmerdaten ein und starte die Signiersitzung.
4. Bestätige den Abschluss der Signatur und kehre zur Anwendung zurück.

![Signatur abgeschlossen](images/docuSign_finish.png)

## Einrichtung

- **Rollen:** Jeder (konfiguriert in config/roles.xml).
- **OpenAPI:** https://github.com/docusign/eSign-OpenAPI-Specification/raw/master/esignature.rest.swagger-v2.1.json  (Namespace: com.docusign.esign.model)

### Variablen

```
@variables.yaml@
```

## Komponenten

### Connector-Prozesse

#### Envelopes.p.json

- **createEnvelope(com.docusign.esign.model.EnvelopeDefinition envelopeDefinition) -> envelopeId: String**
    - Eingabe:
        - `envelopeDefinition` (com.docusign.esign.model.EnvelopeDefinition) — (keine Beschreibung verfügbar)
    - Ergebnis:
        - `envelopeId` (String) — (keine Beschreibung verfügbar)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) — (keine Beschreibung verfügbar)

- **createRecipientView(String envelopeId, com.docusign.esign.model.Signer signer, String returnPage) -> signingUrl: String**
    - Eingabe:
        - `envelopeId` (String) — (keine Beschreibung verfügbar)
        - `signer` (com.docusign.esign.model.Signer) — (keine Beschreibung verfügbar)
        - `returnPage` (String) — (keine Beschreibung verfügbar)
    - Ergebnis:
        - `signingUrl` (String) — (keine Beschreibung verfügbar)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) — (keine Beschreibung verfügbar)

- **readDocuments(String envelopeId) -> documents: java.util.List<com.docusign.esign.model.EnvelopeDocument>**
    - Eingabe:
        - `envelopeId` (String) — (keine Beschreibung verfügbar)
    - Ergebnis:
        - `documents` (java.util.List<com.docusign.esign.model.EnvelopeDocument>) — (keine Beschreibung verfügbar)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) — (keine Beschreibung verfügbar)

- **getSignedDocContentStream(String envelopeId, String signedDocumentId) -> signedDocumentEntity: Object**
    - Eingabe:
        - `envelopeId` (String) — (keine Beschreibung verfügbar)
        - `signedDocumentId` (String) — (keine Beschreibung verfügbar)
    - Ergebnis:
        - `signedDocumentEntity` (Object) — (keine Beschreibung verfügbar)
        - `error` (ch.ivyteam.ivy.bpm.error.BpmError) — (keine Beschreibung verfügbar)

### Formular-Komponenten

#### DocuSignPopup — Eingebetteter Signatur-Dialog (HTML dialog / JSF Composite)
- **Namespace:** com.axonivy.connector.docusign.connector.components.DocuSignPopup
- **Komponententyp:** HTML_DIALOG / JSF Composite Component
- **Felder:**
   - `integrationKey` (String) — (keine Beschreibung verfügbar)
   - `event` (String) — (keine Beschreibung verfügbar)
   - `ivyToken` (String) — (keine Beschreibung verfügbar)
- **Verwendet in:** DocuSignPopup HTML dialog process
- **Zweck:** Stellt einen eingebetteten Signatur-Dialog bereit, der entweder ein iframe oder das DocuSign JS-Plugin zur Durchführung des eingebetteten Signings verwendet.

### Maven-Artefakte

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
