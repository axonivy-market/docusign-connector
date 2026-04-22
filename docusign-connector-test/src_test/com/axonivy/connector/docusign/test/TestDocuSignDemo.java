package com.axonivy.connector.docusign.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.docusign.auth.OAuth2Feature.Property;
import com.axonivy.connector.docusign.event.EnvelopeCompleted;

import ch.ivyteam.ivy.application.IApplication;
import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.ExecutionResult;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;
import ch.ivyteam.ivy.rest.client.RestClient;
import ch.ivyteam.ivy.rest.client.RestClients;
import ch.ivyteam.ivy.rest.client.security.CsrfHeaderFeature;
import ch.ivyteam.ivy.security.ISession;
import ch.ivyteam.ivy.workflow.ICase;
import ch.ivyteam.ivy.workflow.ITask;
import ch.ivyteam.ivy.workflow.TaskState;

@IvyProcessTest(enableWebServer = true)
public class TestDocuSignDemo {
  private static final String SYSTEM_USER = "System user";
  @BeforeEach
  void beforeEach(AppFixture fixture, IApplication app) throws Exception {
	fixture.config("RestClients.'DocuSign (DocuSign REST API)'.Url", DocuSignServiceMock.URI);

	var clients = RestClients.of(app);
	var docuSign = clients.find(EnvelopeCompleted.DOCU_SIGN_CLIENT_ID);
	var mockClient = mockClient(docuSign);
	clients.set(mockClient);
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
      .property("PATH.accountId", "test-account-id")
      .toRestClient();
    return mockClient;
  }

  @Test
  public void main(BpmClient bpmClient, ISession session, IApplication app) throws Exception {
    ExecutionResult result = userFlow(bpmClient, session);
    com.axonivy.connector.docusign.connector.demo.Data docuSign = result.data().last();
    assertThat(docuSign.getEnvelopes()).hasSize(1);

    ITask signTask = result.workflow().activeTasks().get(0);
    assertThat(signTask.getState())
      .isEqualTo(TaskState.SUSPENDED);

    ITask system2Signing = result.workflow().activeTasks().stream()
      .filter(task -> Objects.equals(task.responsibles().displayName(), SYSTEM_USER))
      .findFirst().orElseThrow();
    assertThat(system2Signing.getState()).isEqualTo(TaskState.SUSPENDED);
    bpmClient.start()
      .task(system2Signing)
      .as().systemUser()
      .execute();

    ICase activeCase = result.workflow().activeCase();
    assertThat(activeCase.documents().getAll()).isEmpty();
  }

  private ExecutionResult userFlow(BpmClient bpmClient, ISession session) throws IOException {
	bpmClient.mock().element(BpmProcess.name("DemoESign").elementName("Upload Document"))
		.with((params, results) -> {});

	bpmClient.mock().element(BpmProcess.name("DemoESign").elementName("read envelopes")).with((params, results) -> {
		var envelope = new com.docusign.esign.model.Envelope();
		try {
			results.set("envelopes", List.of(envelope));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	});

	bpmClient.mock().element(BpmProcess.name("DemoESign").elementName("create envelope")).with(ctx -> {
		try {
			ctx.set("envelopeId", "env-test-1");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return ctx;
	});

	ExecutionResult result = bpmClient.start().process("DemoESign/startWf.ivp").as().session(session).execute();
	return result;
  }
}
