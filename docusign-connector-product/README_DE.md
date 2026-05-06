# DocuSign Connector

Der [DocuSign](https://www.docusign.com/products/electronic-signature)-Konnektor von Axon Ivy integriert elektronische Signaturen in deine Prozessanwendung. DocuSign eSignature ermöglicht das schnelle und einfache Unterzeichnen von Dokumenten und die Integration in bestehende Systeme.

### Wichtigste Funktionen

- Ermöglicht dir als Low‑Code‑Entwickler, DocuSign eSignature in Axon Ivy‑Prozesse zu integrieren.
- Bietet einfache Serviceaufrufe zum Erstellen von Envelopes, Öffnen von Signieransichten und Abrufen signierter Dokumente.
- Enthält die einbettbare UI‑Komponente `DocuSignPopup` für integriertes In‑App‑Signing (Iframe oder DocuSign JS).
- Unterstützt sowohl Embedded‑ als auch Remote‑Signing‑Workflows (Demo enthält Schritt‑für‑Schritt‑Beispiele).
- Liefert eine OpenAPI‑Spezifikation und einen vorkonfigurierten REST‑Client für einfache Integration und Tests.
- Bietet optionale Systemauthentifizierung (JWT) für serverseitige/service‑Account‑Szenarien.

## Demo

1. Lade ein Dokument hoch und weise Signierende zu.  
![signing-process](images/eSignDocumentProcess.png)

2. Die Signierenden erhalten eine E‑Mail und werden in den webbasierten Signiervorgang geführt.  
![place-signature](images/docuSign_finish.png)

## Einrichtung

Bevor Signiervorgänge zwischen der Axon Ivy Engine und DocuSign eSignature ausgeführt werden können, müssen beide Systeme miteinander verbunden werden. Das geht in wenigen Schritten:

1. Erstelle ein kostenloses DocuSign‑Entwicklerkonto: https://account-d.docusign.com/#/username
2. Erstelle eine neue `application` unter https://admindemo.docusign.com/authenticate?goTo=apiIntegratorKey
	 - Notiere die **User ID**.
	 - Notiere die **API Account ID**.  
	 ![create-app](images/appsAndKeys.png)
3. Bearbeite die erstellte Anwendung:
	 - Notiere den **Integration Key**.
	 - Scrolle zu **Authentication**, wähle `Authorization Code Grant`, klicke auf `Add Secret Key` und notiere das **Secret Key**.
	 - Scrolle zu **Additional settings** und konfiguriere eine `Redirect URI` für Axon Ivy. Die Redirect‑URI muss auf die Authentifizierungs‑Callback‑URI von Axon Ivy zeigen: `.../oauth2/callback`. Für den Axon Ivy Designer ist das normalerweise `http://localhost:8081/oauth2/callback`.
	 - Speichere die Anwendungseinstellungen.  
	 ![edit-app](images/application.png)

4. Starte `start.ivp` des DemoESign‑Demoprozesses, um die Einrichtung zu testen. Deine Einrichtung ist korrekt, wenn du aufgefordert wirst, dich mit einem DocuSign‑Konto zu autorisieren.  
![docusign-auth](images/docuSign_auth.png)
   
5. Zustimmungs‑Endpunkt (Consent)

Du kannst den Browser eines Benutzers zum GET‑Endpunkt `/oauth/auth` weiterleiten, um die Zustimmung zu erhalten. Das ist der erste Schritt in mehreren Authentifizierungs‑Szenarien.

Wenn du den Endpunkt im Browser aufrufst, kannst du ihn verwenden, um:

- individuelle oder Administratorzustimmung zu erhalten,
- einen Autorisierungscode für den Authorization Code Grant zu erhalten,
- oder direkt ein Zugriffstoken mit dem Implicit Grant zu erhalten.

Die Syntax und Parameter für den Aufruf im Browser sehen zum Beispiel so aus:

```
https://account-d.docusign.com/oauth/auth?
		 response_type=CODE_OR_TOKEN
		 &scope=YOUR_REQUESTED_SCOPES
		 &client_id=YOUR_INTEGRATION_KEY
		 &state=YOUR_CUSTOM_STATE
		 &redirect_uri=YOUR_REDIRECT_URI
```

Nach einem erfolgreichen Aufruf prüft der Authentication Service, ob die Client‑Anwendung gültig ist und die angeforderten Berechtigungen besitzt. Wenn ja, werden die angeforderten Daten als Query‑Parameter an die angegebene Redirect‑URI zurückgegeben:

- Beim Implicit Grant: Zugriffstoken und Metadaten.
- Beim Authorization Code Grant: den Autorisierungscode und optional den State.

```
@variables.yaml@
```

## Komponenten

### Exponierte CALLABLE_SUB‑Prozesse

 # Callable Sub Connector Starts

 ## docusign-connector/processes/Processes/Envelopes.p.json

- Signature: createEnvelope
	Input: envelopeDefinition: com.docusign.esign.model.EnvelopeDefinition
	Result: envelopeId: String, error: ch.ivyteam.ivy.bpm.error.BpmError
# DocuSign Connector

Der [DocuSign](https://www.docusign.com/products/electronic-signature) Konnektor von Axon Ivy integriert elektronische Signaturen in deine Prozessanwendung. DocuSign eSignature ermöglicht das schnelle und einfache Unterzeichnen von Dokumenten und die Integration in bestehende Systeme.

### Wichtigste Funktionen

- Ermöglicht dir als Low‑Code‑Entwickler, DocuSign eSignature in Axon Ivy‑Prozesse zu integrieren.
- Bietet einfache Serviceaufrufe zum Erstellen von Envelopes, Öffnen von Signieransichten und Abrufen signierter Dokumente.
- Enthält die einbettbare UI‑Komponente `DocuSignPopup` für integriertes In‑App‑Signing (iframe oder DocuSign JS).
- Unterstützt sowohl Embedded‑ als auch Remote‑Signing‑Workflows (Demo enthält Schritt‑für‑Schritt‑Beispiele).
- Liefert eine OpenAPI‑Spezifikation und einen vorkonfigurierten REST‑Client für einfache Integration und Tests.
- Bietet optionale Systemauthentifizierung (JWT) für serverseitige/service‑Account‑Szenarien.

## Demo

1. Lade ein Dokument hoch und weise Signierende zu.  
![Signiervorgang](images/eSignDocumentProcess.png)

1. Die Signierenden erhalten eine E‑Mail und werden in den webbasierten Signiervorgang eingebunden.  
![Signaturabschluss](images/docuSign_finish.png)

## Einrichtung

Bevor Signiervorgänge zwischen der Axon Ivy Engine und den DocuSign eSignature‑Diensten ausgeführt werden können, müssen beide Systeme miteinander verbunden werden. So geht's:

1. Erstelle ein kostenloses DocuSign‑Entwicklerkonto: https://account-d.docusign.com/#/username
2. Erstelle eine neue `application` unter https://admindemo.docusign.com/authenticate?goTo=apiIntegratorKey
	 - Notiere die **User ID**.
	 - Notiere die **API Account ID**.  
	 ![Apps und Keys](images/appsAndKeys.png)
3. Bearbeite die erstellte Anwendung:
	 - Notiere den **Integration Key**.
	 - Scrolle zu **Authentication**, wähle `Authorization Code Grant`, klicke auf `Add Secret Key` und notiere das **Secret Key**.
	 - Scrolle zu **Additional settings** und konfiguriere eine `Redirect URI` für Axon Ivy. Die Redirect‑URI muss auf die Authentifizierungs‑Callback‑URI von Axon Ivy zeigen: `.../oauth2/callback`. Für den Axon Ivy Designer ist das normalerweise `http://localhost:8081/oauth2/callback`.
	 - Speichere die Anwendungseinstellungen.  
	 ![Anwendung bearbeiten](images/application.png)

4. Starte `start.ivp` des DemoESign‑Demoprozesses, um die Einrichtung zu testen. Deine Einrichtung ist korrekt, wenn du aufgefordert wirst, dich mit einem DocuSign‑Konto zu autorisieren.  
![DocuSign‑Autorisierung](images/docuSign_auth.png)
   
5. Zustimmungs‑Endpunkt (Consent)

Du kannst den Browser eines Benutzers zum GET‑Endpunkt `/oauth/auth` weiterleiten, um Zustimmung zu erhalten. Das ist der erste Schritt in mehreren Authentifizierungs‑Szenarien.

Wenn du den Endpunkt im Browser aufrufst, kannst du ihn verwenden, um:

- individuelle oder Administratorzustimmung zu erhalten,
- einen Autorisierungscode für den Authorization Code Grant zu erhalten,
- oder direkt ein Zugriffstoken mit dem Implicit Grant zu erhalten.

Die Syntax und Parameter für den Aufruf im Browser sehen zum Beispiel so aus:

```
https://account-d.docusign.com/oauth/auth?
		 response_type=CODE_OR_TOKEN
		 &scope=YOUR_REQUESTED_SCOPES
		 &client_id=YOUR_INTEGRATION_KEY
		 &state=YOUR_CUSTOM_STATE
		 &redirect_uri=YOUR_REDIRECT_URI
```

Nach einem erfolgreichen Aufruf prüft der Authentication Service, ob die Client‑Anwendung gültig ist und die angeforderten Berechtigungen besitzt. Wenn ja, werden die angeforderten Daten als Query‑Parameter an die angegebene Redirect‑URI zurückgegeben:

- Beim Implicit Grant: Zugriffstoken und Metadaten.
- Beim Authorization Code Grant: den Autorisierungscode und optional den State.

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
> Der Variable‑Pfad `docusign-connector` wurde ab Version 13 in `docusignConnector` umbenannt.

### Optional: System‑Authentifizierung (JWT)

Der Demo‑Prozess enthält einen optionalen Abschnitt, bei dem die Axon Ivy Plattform im Namen eines Benutzers handelt.  
![Systemgesteuerter Prozess](images/systemDrivenProcess.png)

Diese Interaktion benötigt ein JSON Web Token (JWT)‑Setup:

1. Bearbeite die DocuSign‑Anwendung wie in Schritt 3 der allgemeinen Einrichtung.
2. Erzeuge im Bereich `Authentication` einen RSA‑Schlüssel (Generate RSA).  
 ![RSA‑Schlüssel generieren](images/authenticationGenerateRSA.png)

3. Speichere den generierten privaten Schlüssel:
	1. Kopiere den Private Key in die Zwischenablage.
	2. Speichere die Anwendungseinstellungen.
	3. Lege in deinem Designer‑`configuration`‑Verzeichnis eine neue Datei `docusign.pem` an.
	4. Füge den Schlüsselinhalt in diese Datei ein.
	5. Du kannst einen anderen Speicherort verwenden; passe dann `docusignConnector.jwt.keyFile` entsprechend an.
	![docusign‑pem](images/docuSignPem.png)

4. Lege einen Benutzer für das Service‑Konto an, kopiere dessen `API Username (id)` und setze ihn in `docusignConnector.jwt.userId`.

5. Setze `docusignConnector.jwt.use` auf `true`, falls du JWT als Standard verwenden möchtest.

6. Fertig. Starte einen Signiervorgang. Sobald alle Empfänger signiert haben, wird das signierte Dokument dem Ursprungs‑Case angehängt.

## Komponenten

### Exponierte CALLABLE_SUB‑Prozesse

#### docusign-connector/processes/Processes/Envelopes.p.json

- Signature: createEnvelope
	Input: envelopeDefinition: com.docusign.esign.model.EnvelopeDefinition
	Result: envelopeId: String, error: ch.ivyteam.ivy.bpm.error.BpmError

- Signature: createRecipientView
	Input: envelopeId: String, signer: com.docusign.esign.model.Signer, returnPage: String
	Result: signingUrl: String, error: ch.ivyteam.ivy.bpm.error.BpmError

- Signature: readDocuments
	Input: envelopeId: String
	Result: documents: java.util.List<com.docusign.esign.model.EnvelopeDocument>, error: ch.ivyteam.ivy.bpm.error.BpmError

- Signature: getSignedDocContentStream
	Input: envelopeId: String, signedDocumentId: String
	Result: signedDocumentEntity: Object, error: ch.ivyteam.ivy.bpm.error.BpmError


### Formularkomponenten

#### DocuSignPopupData

- **Name Space**: com.axonivy.connector.docusign.connector.components.DocuSignPopup
- **Paths:**
	- xhtml: docusign-connector/src_hd/com/axonivy/connector/docusign/connector/components/DocuSignPopup/DocuSignPopup.xhtml
- **Component type**: HTML_DIALOG
- **Parameter**:
	- integrationKey (String)
	- event (String)
	- ivyToken (String)
- **Main logic/feature included in that UI**: Component dialog with start method 'start'
	- process: docusign-connector/src_hd/com/axonivy/connector/docusign/connector/components/DocuSignPopup/DocuSignPopupProcess.p.json

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

