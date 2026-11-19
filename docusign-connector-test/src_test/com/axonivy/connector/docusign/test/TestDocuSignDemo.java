package com.axonivy.connector.docusign.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.axonivy.connector.docusign.auth.OAuth2Feature.Property;
import com.axonivy.connector.docusign.event.EnvelopeCompleted;

import ch.ivyteam.ivy.application.app.Application;
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

@IvyProcessTest(enableWebServer = true)
public class TestDocuSignDemo {
  @BeforeEach
  void beforeEach(AppFixture fixture, Application app) throws Exception {
    fixture.config("RestClients.'DocuSign (DocuSign REST API)'.Url", DocuSignServiceMock.URI);
    var clients = RestClients.of(app);
    var docuSign = clients.find(EnvelopeCompleted.REST_CLIENT_NAME);
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
  public void main(BpmClient bpmClient, ISession session) {
    ExecutionResult result = userFlow(bpmClient, session);

    ICase activeCase = result.workflow().activeCase();
    assertThat(activeCase.documents().getAll()).isEmpty();
  }

  private ExecutionResult userFlow(BpmClient bpmClient, ISession session) {
    var demoESign = BpmProcess.name("DemoESign");
    bpmClient.mock().element(demoESign.elementName("Upload Document")).with((params, results) -> {});

    bpmClient.mock().element(demoESign.elementName("read envelopes")).with((params, results) -> {
      var envelope = new com.docusign.esign.model.Envelope();
      try {
        results.set("envelopes", new ArrayList<>(List.of(envelope)));
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      }
    });

    bpmClient.mock().element(demoESign.elementName("create envelope")).with(ctx -> {
      try {
        ctx.set("envelopeId", "env-test-1");
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      }
      return ctx;
    });

    return bpmClient.start().process("DemoESign/startWf.ivp").as().session(session).execute();
  }
}
