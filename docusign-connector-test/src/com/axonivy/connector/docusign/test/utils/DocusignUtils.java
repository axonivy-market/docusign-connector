package com.axonivy.connector.docusign.test.utils;

import com.axonivy.connector.docusign.test.constants.DocusignConstant;

import ch.ivyteam.ivy.environment.AppFixture;

public class DocusignUtils {
  public static void setUpConfigForContext(String contextName, AppFixture fixture) throws Exception {
    switch (contextName) {
      case DocusignConstant.REAL_CALL_CONTEXT_DISPLAY_NAME:
        setUpConfigForApiTest(fixture);
        break;
      default:
        break;
    }
  }

  public static void setUpConfigForApiTest(AppFixture fixture) {

    String integrationKey = "";
    String secretKey = "";
    String accountId = "";
    fixture.var("docusign-connector.integrationKey", integrationKey);
    fixture.var("docusign-connector.secretKey", secretKey);
    fixture.var("docusign-connector.accountId", accountId);
  }


}
