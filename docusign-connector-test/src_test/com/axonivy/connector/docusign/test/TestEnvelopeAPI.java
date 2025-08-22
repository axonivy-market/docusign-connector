package com.axonivy.connector.docusign.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static com.axonivy.utils.e2etest.enums.E2EEnvironment.REAL_SERVER;

import java.io.IOException;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.Assumptions;

import com.axonivy.connector.docusign.test.constants.DocusignTestConstants;
import com.axonivy.utils.e2etest.context.MultiEnvironmentContextProvider;
import com.axonivy.utils.e2etest.utils.E2ETestUtils;
import com.docusign.esign.model.EnvelopeDefinition;

import ch.ivyteam.ivy.bpm.engine.client.BpmClient;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmElement;
import ch.ivyteam.ivy.bpm.engine.client.element.BpmProcess;
import ch.ivyteam.ivy.bpm.exec.client.IvyProcessTest;
import ch.ivyteam.ivy.environment.AppFixture;

@IvyProcessTest(enableWebServer = true)
@ExtendWith(MultiEnvironmentContextProvider.class)
public class TestEnvelopeAPI {
  private static final BpmProcess ENVELOPES_REQUEST = BpmProcess.name("Envelopes");

  @BeforeEach
  void beforeEach(AppFixture fixture, ExtensionContext context) throws Exception {
    E2ETestUtils.determineConfigForContext(context.getDisplayName(), runRealEnv(fixture), runMockEnv());
  }

  private interface Start {
    BpmElement CREATE_ENVELOPE = ENVELOPES_REQUEST.elementName("createEnvelope(EnvelopeDefinition)");
  }

  @TestTemplate
  void callSubProcess_createEnvelope(BpmClient bpmClient, ExtensionContext context)
      throws IOException, NoSuchFieldException {
    Assumptions.assumeTrue(context.getDisplayName().equals(REAL_SERVER.getDisplayName()),
        "Skipping test in PROD environment");
    var result = bpmClient.start().subProcess(Start.CREATE_ENVELOPE).execute(new EnvelopeDefinition());
    String redirectUrl = result.http().redirectLocation();
    assertTrue(ObjectUtils.isNotEmpty(redirectUrl));
    assertThat(redirectUrl).containsSubsequence(
        "https://account-d.docusign.com/oauth/auth?response_type=code&scope=signature+impersonation&client_id=",
        "&redirect_uri=", "oauth2%2Fcallback&state=");
  }

  private Runnable runRealEnv(AppFixture fixture) {
    return () -> {
      String intergrationKey = System.getProperty(DocusignTestConstants.INTERGRATION_KEY);
      String secretKey = System.getProperty(DocusignTestConstants.SECRET_KEY);
      String accountId = System.getProperty(DocusignTestConstants.ACCOUNT_ID);
      fixture.var("docusignConnector.integrationKey", intergrationKey);
      fixture.var("docusignConnector.secretKey", secretKey);
      fixture.var("docusignConnector.accountId", accountId);
      fixture.var("returnPage", "http://localhost:8080/");
      fixture.var("frameAncestors", "http://localhost:8080/, https://apps-d.docusign.com");
    };
  }

  private Runnable runMockEnv() {
    return () -> {};
  }
}
