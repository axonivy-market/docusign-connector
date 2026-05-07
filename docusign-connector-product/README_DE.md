# DocuSign Connector
Der [DocuSign](https://www.docusign.com/products/electronic-signature) Connector von Axon Ivy integriert elektronische Signaturen in deine Prozessanwendung. DocuSign eSignature ermöglicht das schnelle und einfache Signieren von Dokumenten und die Integration in bestehende Systeme. 

### Wichtigste Funktionen
- Ermöglicht dir, DocuSign eSignature in Axon Ivy-Prozesse zu integrieren.
- Stellt einfache Serviceaufrufe bereit, um Envelopes zu erstellen, Signieransichten zu öffnen und unterschriebene Dokumente abzurufen.
- Enthält eine einbettbare UI-Komponente `DocuSignPopup` für integriertes In‑App‑Signieren (iframe oder DocuSign JS).
- Unterstützt sowohl eingebettete als auch Remote‑Signatur‑Workflows (Demo enthält Schritt‑für‑Schritt‑Beispiele).
- Bietet eine OpenAPI‑Spezifikation und einen vorkonfigurierten REST‑Client für einfache Integration und Tests.
- Ermöglicht optionale Systemauthentifizierung (JWT) für serverseitige/service‑account‑Szenarien.

## Demo

1. Lade ein Dokument hoch und weise die **Unterzeichnenden** zu.  
![Signaturablauf](images/eSignDocumentProcess.png)

1. Die Unterzeichnenden erhalten eine E‑Mail und durchlaufen den webbasierten Signierfluss.  
![Signatur abschließen](images/docuSign_finish.png)

### Eingebettetes und Remote‑Signieren

Die Demo enthält zwei Signierszenarien:

1. Eingebettetes Signieren — öffnet die Signieroberfläche innerhalb deiner Anwendung über eine Empfängeransicht.
2. Remote‑Signieren — lädt Empfänger per E‑Mail ein; die Unterzeichnenden schließen den Ablauf auf DocuSign ab.

Nutze die Demo‑Einstiegspunkte `Embedded and Remote signing`, um beide Abläufe auszuprobieren und zu sehen, wie unterschriebene Dokumente an Cases angehängt werden.

## Einrichtung

Bevor deine Axon Ivy Engine und die DocuSign eSignature‑Dienste Signiervorgänge ausführen können, musst du sie miteinander verbinden. So geht's:

1. Erstelle ein kostenloses DocuSign‑Entwicklerkonto: https://account-d.docusign.com/#/username
2. Erstelle eine neue `application` unter https://admindemo.docusign.com/authenticate?goTo=apiIntegratorKey
   - Notiere die **User ID**.
   - Notiere die **API Account ID**. 
   ![Anwendung erstellen](images/appsAndKeys.png)
3. Bearbeite die erstellte `application`:
   - Notiere den **Integration Key**.
   - Scrolle zu **Authentication**, wähle `Authorization Code Grant`, klicke `Add Secret Key`, und notiere den **Secret Key**.
   - Scrolle zu **Additional settings** und konfiguriere eine `Redirect URI` für Axon Ivy. Die Redirect‑URI muss auf die Authentifizierungs‑Callback‑URI von Axon Ivy `.../oauth2/callback` zeigen.
     Für den Axon Ivy Designer ist das normalerweise `http://localhost:8081/oauth2/callback`.
   - Speichere die geänderten Anwendungseinstellungen.  
   ![Anwendung bearbeiten](images/application.png)
4. Führe `start.ivp` des DemoESign‑Prozesses aus, um deine Einrichtung zu testen. Deine Einrichtung ist korrekt, wenn du zur Autorisierung mit einem DocuSign‑Konto aufgefordert wirst.  
   ![DocuSign‑Autorisierung](images/docuSign_auth.png)
5. Einholen der Consent‑URL:

Du kannst das Browserfenster eines Benutzers auf den GET‑Endpunkt `/oauth/auth` weiterleiten, um Zustimmung einzuholen. Dieser Endpunkt hat je nach Parametern unterschiedliche Funktionen:

* Hole individuelle oder Admin‑Zustimmung in den jeweiligen Authentifizierungsszenarien.
* Erhalte einen Autorisierungscode für den Authorization Code Grant.
* Erhalte direkt ein Access Token mit dem Implicit Grant.

Die Syntax und die Parameter für den Aufruf dieses Endpunkts im Browser sehen wie folgt aus:
```
https://account-d.docusign.com/oauth/auth?
     response_type=CODE_OR_TOKEN
     &scope=YOUR_REQUESTED_SCOPES
     &client_id=YOUR_INTEGRATION_KEY
     &state=YOUR_CUSTOM_STATE
     &redirect_uri=YOUR_REDIRECT_URI
 ```
Nach einem erfolgreichen Aufruf prüft der Authentication Service, ob die Client‑Anwendung gültig ist und Zugriff auf die angeforderten Scopes hat. Wenn ja, liefert er die angeforderten Daten als Query‑Parameter an die angegebene Redirect‑URI zurück:

- Im Implicit‑Grant‑Szenario liefert er Access‑Tokens und Metadaten.
- Im Authorization‑Code‑Grant‑Szenario liefert er den Authorization‑Code und ggf. den State.

### Variablen

Um dieses Produkt zu nutzen, musst du mehrere Variablen konfigurieren.

Füge den folgenden Block in die Datei `config/variables.yaml` deines Business‑Projekts ein, das dieses Produkt verwenden soll. Danach trage die zuvor gesammelten Werte ein.
(Hinweis: Im Designer können diese Variablen in jedem Projekt definiert werden, daher ist es nicht nötig, das Demo‑Projekt auszupacken.)

```
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
    # This property provides a call back that after the signer completes or ends the signing ceremony, DocuSign redirects the user's browser back to your app via the returnUrl that you supplied in the request.
    returnPage: 'http://localhost:8081/'

    # This property is a string array which must include your site’s URL along with https://apps-d.docusign.com/send/ - opens in new window if your app is in the demo environment or https://apps.docusign.com - opens in new window if it is in production. Your domain must have a valid SSL certificate (such as https://my.site.com) for embedding in production environments. You can use http://localhost for development and testing.
    frameAncestors: 'http://localhost:8081/, https://apps-d.docusign.com'
    
    # This property must include https://apps-d.docusign.com/send/ - opens in new window if your app is in the demo environment or https://apps.docusign.com - opens in new window if it is in production.
    messageOrigins: 'https://apps-d.docusign.com'

```

> [!NOTE]
> Der Variablenpfad `docusign-connector` wurde ab Version 13 in `docusignConnector` umbenannt.

### Optional: System‑Authentifizierung (JWT)

Der Demo‑Prozess enthält einen abschließenden Service‑Teil, in dem die Axon Ivy‑Plattform im Namen eines Benutzers agiert.  
![Systemgesteuerter Prozess](images/systemDrivenProcess.png)

Für diese Interaktion ist eine JSON‑Web‑Token‑(JWT)‑Authentifizierung erforderlich:

1. Bearbeite die DocuSign‑`application` wie in Schritt 3 der allgemeinen Einrichtung.
2. Im Abschnitt `Authentication` klicke auf `Generate RSA`, um ein sicheres Schlüsselpaar zu erstellen.  
 ![RSA‑Schlüssel erzeugen](images/authenticationGenerateRSA.png)

3. Speichere den generierten privaten Schlüssel:
    1. Kopiere den erzeugten 'Private Key' in deine Zwischenablage.
    2. Speichere die geänderten Anwendungseinstellungen.
    3. Erstelle eine neue leere Textdatei namens `docusign.pem` im `configuration`‑Verzeichnis deines Designers.
    4. Füge den Inhalt der Zwischenablage in die Datei `docusign.pem` ein.
    5. Du kannst auch einen anderen Speicherort für die PEM‑Datei verwenden. Passe die Variable `docusignConnector.jwt.keyFile` entsprechend an. Sie sollte ein relativer Pfad zum `configuration`‑Verzeichnis oder ein absoluter Pfad auf deinem System sein.  
![docusign‑pem](images/docuSignPem.png)

4. Lege einen Benutzer als Service‑Account fest:
    1. Öffne die `Users`‑Übersicht und wähle den gewünschten Service‑Benutzer.
    2. Kopiere die `API Username (id)` von der Benutzerdetailseite.
    3. Trage diesen Wert in die Variable `docusignConnector.jwt.userId` ein.
    
5. JWT wird automatisch für Prozesse verwendet, die vom Systembenutzer ausgeführt werden. Wenn du JWT generell verwenden möchtest, setze die Variable `docusignConnector.jwt.use` auf `true`.

6. Fertig. Starte einen Signierprozess. Sobald alle Empfänger unterschrieben haben, hängt der Systemservice das unterschriebene Dokument an den ursprünglichen Case an.

## Komponenten

### Exponierte CALLABLE_SUB‑Prozesse

Dieses Produkt liefert keine CALLABLE_SUB‑Prozesse.

#### docusign-connector/processes/Processes/Envelopes.p.json

- Signatur: createEnvelope
  Eingabe: envelopeDefinition: com.docusign.esign.model.EnvelopeDefinition
  Ergebnis: envelopeId: String, error: ch.ivyteam.ivy.bpm.error.BpmError

- Signatur: createRecipientView
  Eingabe: envelopeId: String, signer: com.docusign.esign.model.Signer, returnPage: String
  Ergebnis: signingUrl: String, error: ch.ivyteam.ivy.bpm.error.BpmError

- Signatur: readDocuments
  Eingabe: envelopeId: String
  Ergebnis: documents: java.util.List<com.docusign.esign.model.EnvelopeDocument>, error: ch.ivyteam.ivy.bpm.error.BpmError

- Signatur: getSignedDocContentStream
  Eingabe: envelopeId: String, signedDocumentId: String
  Ergebnis: signedDocumentEntity: Object, error: ch.ivyteam.ivy.bpm.error.BpmError


### Formularkomponenten

#### DocuSignPopupData

- **Namensraum**: com.axonivy.connector.docusign.connector.components.DocuSignPopup
- **Pfad:**
  - xhtml: docusign-connector/src_hd/com/axonivy/connector/docusign/connector/components/DocuSignPopup/DocuSignPopup.xhtml
- **Komponententyp**: HTML_DIALOG
- **Parameter**:
  - integrationKey (String)
  - event (String)
  - ivyToken (String)

### OpenAPI‑Ressourcen

![DocuSign](https://github.com/docusign/eSign-OpenAPI-Specification/raw/master/esignature.rest.swagger-v2.1.json)

### Maven‑Artefakte

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
