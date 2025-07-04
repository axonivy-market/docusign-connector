package com.axonivy.connector.docusign.test.utils;



import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import com.axonivy.connector.docusign.auth.OAuth2Feature.Property;
import com.axonivy.connector.docusign.event.EnvelopeCompleted;
import com.axonivy.connector.docusign.test.DocuSignServiceMock;
// import com.axonivy.connector.docusign.test.TestDocuSignDemo;
import com.axonivy.connector.docusign.test.constants.DocusignConstant;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

public class DocusignUtils {
	public static void setUpConfigForContext(String contextName, AppFixture fixture, IApplication app , URI path) throws Exception {
		switch (contextName) {
			case DocusignConstant.REAL_CALL_CONTEXT_DISPLAY_NAME:
				setUpConfigForApiTest(fixture);
				break;
			case DocusignConstant.MOCK_SERVER_CONTEXT_DISPLAY_NAME:
				setUpConfigForMockServer(fixture, app, path);
				break;
			default:
				break;
		}
	}

	public static void setUpConfigForApiTest(AppFixture fixture) {

	}

	public static void setUpConfigForMockServer(AppFixture fixture, IApplication app, URI path) throws Exception {
		// fixture.config("RestClients.'DocuSign (DocuSign REST API)'.Url", DocuSignServiceMock.URI);

		fixture.config("RestClients.'DocuSign (DocuSign REST API)'.Url", DocuSignServiceMock.URI);
		var clients = RestClients.of(app);
		var docuSign = clients.find(EnvelopeCompleted.DOCU_SIGN_CLIENT_ID);
		var mockClient = mockClient(docuSign,path);
		clients.set(mockClient);
	}

	private static RestClient mockClient(RestClient docuSign, URI path) throws URISyntaxException {
		 Path testKeyFile = Path.of(path);
		var mockClient = docuSign.toBuilder().feature(CsrfHeaderFeature.class.getName())
				.property(Property.INTEGRATION_KEY, "test-key").property(Property.SECRET_KEY, "not-my-secret")
				.property(Property.JWT_USE, Boolean.FALSE.toString()).property(Property.JWT_USER_ID, "test-user")
				 .property(Property.JWT_KEY_FILE, testKeyFile.toAbsolutePath().toString())
				.property(Property.AUTH_BASE_URI, DocuSignServiceMock.URI + "/oauth").property("PATH.accountId", "placeholder")
				.toRestClient();
		return mockClient;
	}
}
