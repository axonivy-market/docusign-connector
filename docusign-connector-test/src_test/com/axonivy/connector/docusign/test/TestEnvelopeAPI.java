package com.axonivy.connector.docusign.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.Assumptions;

import com.axonivy.connector.docusign.test.constants.DocusignTestConstants;
import com.axonivy.connector.docusign.test.context.MultiEnvironmentContextProvider;
import com.axonivy.connector.docusign.test.utils.DocusignTestUtils;
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
    DocusignTestUtils.setUpConfigForContext(context.getDisplayName(), fixture);
  }

  private interface Start {
    BpmElement CREATE_ENVELOPE = ENVELOPES_REQUEST.elementName("createEnvelope(EnvelopeDefinition)");
  }

  @TestTemplate
  void callSubProcess_createEnvelope(BpmClient bpmClient, ExtensionContext context) throws IOException, NoSuchFieldException {
    Assumptions.assumeTrue(context.getDisplayName().equals(DocusignTestConstants.REAL_CALL_CONTEXT_DISPLAY_NAME), "Skipping test in PROD environment");
    var result = bpmClient.start().subProcess(Start.CREATE_ENVELOPE).execute(new EnvelopeDefinition());
    String redirectUrl = result.http().redirectLocation();
    assertTrue(ObjectUtils.isNotEmpty(redirectUrl));
    assertThat(redirectUrl).containsSubsequence(
        "https://account-d.docusign.com/oauth/auth?response_type=code&scope=signature+impersonation&client_id=",
        "&redirect_uri=", "oauth2%2Fcallback&state=");
  }
}
