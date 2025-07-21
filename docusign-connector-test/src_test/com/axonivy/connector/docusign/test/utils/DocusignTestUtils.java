package com.axonivy.connector.docusign.test.utils;

import com.axonivy.connector.docusign.test.constants.DocusignTestConstants;

import ch.ivyteam.ivy.environment.AppFixture;

public class DocusignTestUtils {
  public static void setUpConfigForContext(String contextName, AppFixture fixture) throws Exception {
    switch (contextName) {
      case DocusignTestConstants.REAL_CALL_CONTEXT_DISPLAY_NAME:
        setUpConfigForApiTest(fixture);
        break;
      default:
        break;
    }
  }

  public static void setUpConfigForApiTest(AppFixture fixture) {
    String intergrationKey = System.getProperty(DocusignTestConstants.INTERGRATION_KEY);
    String secretKey = System.getProperty(DocusignTestConstants.SECRET_KEY);
    String accountId = System.getProperty(DocusignTestConstants.ACCOUNT_ID);
    fixture.var("docusignConnector.integrationKey", intergrationKey);
    fixture.var("docusignConnector.secretKey", secretKey);
    fixture.var("docusignConnector.accountId", accountId);
    fixture.var("returnPage", "http://localhost:8080/");
    fixture.var("frameAncestors", "http://localhost:8080/, https://apps-d.docusign.com");
  }

}
