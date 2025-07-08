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
    String intergrationKey = System.getProperty(DocusignConstant.INTERGRATION_KEY);
    String secretKey = System.getProperty(DocusignConstant.SECRET_KEY);
    String accountId = System.getProperty(DocusignConstant.ACCOUNT_ID);
    fixture.var("docusign-connector.integrationKey", intergrationKey);
    fixture.var("docusign-connector.secretKey", secretKey);
    fixture.var("docusign-connector.accountId", accountId);
    fixture.var("returnPage", "http://localhost:8080/");
    fixture.var("frameAncestors", "http://localhost:8080/, https://apps-d.docusign.com");
  }

}
