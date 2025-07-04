package com.axonivy.connector.docusign.test;

import static com.axonivy.connector.docusign.constant.DocuSignConstants.DEFAULT_ENVELOPE_STATUS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.axonivy.connector.docusign.auth.OAuth2Feature.Property;
import com.axonivy.connector.docusign.event.EnvelopeCompleted;
import com.axonivy.connector.docusign.test.context.MultiEnvironmentContextProvider;
import com.axonivy.connector.docusign.test.utils.DocusignUtils;
import com.axonivy.connector.docusign.util.DocUtils;
import com.axonivy.connector.docusign.util.EnvelopeDefinitionUtils;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;

@IvyProcessTest(enableWebServer = true)
//@ExtendWith(MultiEnvironmentContextProvider.class)
public class TestEnvelopes {
	private static final BpmProcess ENVELOPES_REQUEST = BpmProcess.name("Envelopes");

  @BeforeEach
  void beforeEach(AppFixture fixture, IApplication app) throws Exception {
    fixture.config("RestClients.'DocuSign (DocuSign REST API)'.Url", DocuSignServiceMock.URI);
//    var clients = RestClients.of(app);
//    var docuSign = clients.find(EnvelopeCompleted.DOCU_SIGN_CLIENT_ID);
//    var mockClient = mockClient(docuSign);
//    clients.set(mockClient);
    
//    DocusignUtils.setUpConfigForContext(DEFAULT_ENVELOPE_STATUS, fixture, app);
  }

  private static RestClient mockClient(RestClient docuSign) throws URISyntaxException {
    Path testKeyFile = Path.of(TestDocuSignDemo.class.getResource("testKey.pem").toURI());
    var mockClient = docuSign.toBuilder()
      .feature(CsrfHeaderFeature.class.getName())
      .property(Property.INTEGRATION_KEY, "test-key")
      .property(Property.SECRET_KEY, "not-my-secret")
      .property(Property.JWT_USE, Boolean.FALSE.toString())
      .property(Property.JWT_USER_ID, "test-user")
      .property(Property.JWT_KEY_FILE, testKeyFile.toAbsolutePath().toString())
      .property(Property.AUTH_BASE_URI, DocuSignServiceMock.URI + "/oauth")
      .property("PATH.accountId", "placeholder")
      .toRestClient();
    return mockClient;
  }

	private interface Start {
		BpmElement CREATE_ENVELOPE = ENVELOPES_REQUEST.elementName("createEnvelope(EnvelopeDefinition)");
	}

	@Test
	void callSubProcess_getDocumentContent(BpmClient bpmClient) throws IOException, NoSuchFieldException {
		EnvelopeDefinition mockEnvelopeDefinition = mockEnvelopeDefinition();

		var result = bpmClient.start().subProcess(Start.CREATE_ENVELOPE)
				.withParam("envelopeDefinition", mockEnvelopeDefinition).execute();
		Ivy.log().warn(result.data().last());
//		assertThat(result.data().last().get("envelopeId").equals("b4bf237a-c39b-4484-8216-e9d238d49e96"));
//		assertTrue(ObjectUtils.isNotEmpty(result.data().last()));
	}

	private EnvelopeDefinition mockEnvelopeDefinition() throws IOException {
		SignHere signHere = new SignHere();
		Signer signer = new Signer();
		signer.recipientId("1234");
		signer.setEmail("OctopusTeamEnv@gmail.com");
		signer.setName("OctopusTeam");

		File abs = new File("src_test/Test.pdf");
		InputStream is = new FileInputStream(abs);

		var envelopeDefinition = new EnvelopeDefinition();
		envelopeDefinition.setStatus(DEFAULT_ENVELOPE_STATUS);
		envelopeDefinition.setEmailSubject(
				Ivy.cms().co("/Dialogs/com/axonivy/connector/docusign/connector/demo/EmbeddedSigning/MailSubject"));
		envelopeDefinition.setDocuments(List.of(DocUtils.create(is, abs.getName())));
		Recipients recipients = new Recipients();
		recipients.setSigners(List.of(EnvelopeDefinitionUtils.unifySignerData(signer, signHere)));
		envelopeDefinition.setRecipients(recipients);
		return envelopeDefinition;
	}
}
