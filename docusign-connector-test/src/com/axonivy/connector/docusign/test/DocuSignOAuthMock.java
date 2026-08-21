package com.axonivy.connector.docusign.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import ch.ivyteam.ivy.application.app.Application;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@PermitAll
@Path(DocuSignServiceMock.PATH_SUFFIX + "/oauth")
public class DocuSignOAuthMock {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("auth")
  public Response authCode_1(@QueryParam("redirect_uri") String redirectUri) { // redirect
    URI bpmEngineContinue = UriBuilder.fromUri(URI.create(redirectUri))
            .queryParam("code", "my-test-code")
            .build();
    return Response.seeOther(bpmEngineContinue).build();
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("token")
  public String accessToken_2(@SuppressWarnings("all") String payload) {
    return load("json/token.json");
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("userinfo")
  public String userInfo_3() {
    String info = load("json/userinfo.json");
    URI myUri = ch.ivyteam.ivy.request.EngineUriResolver.instance().local();
    info = StringUtils.replace(info, "http://localhost:!port!/mock",
            myUri.toASCIIString() + "/" + Application.current().getName() + "/api/docuSignMock");
    return info;
  }

  private static String load(String path) {
    try (InputStream is = DocuSignOAuthMock.class.getResourceAsStream(path)) {
      return IOUtils.toString(is, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new RuntimeException("Failed to read resource: " + path);
    }
  }
}
