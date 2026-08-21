package com.axonivy.connector.docusign.test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

import ch.ivyteam.api.API;
import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@PermitAll
@Path(DocuSignServiceMock.PATH_SUFFIX + "/restapi/v2.1/accounts/{accountId}/")
public class DocuSignServiceMock {

  static final String PATH_SUFFIX = "docuSignMock";
  // URI where this mock can be reached: to be referenced in tests that use it!
  public static final String URI = "{ivy.app.baseurl}/api/" + PATH_SUFFIX;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("envelopes")
  public String envelopes() {
    return load("json/envelopes.json");
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("envelopes")
  public Response createEnvelope(String payload) {
    API.checkParameterNotNull(payload, "payload");
    return Response.status(201)
            .entity(load("json/createDraft.json"))
            .build();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("envelopes/{envId}/documents")
  public Response envelopeDocs(@PathParam("envelopeId") String id) {
    return Response.ok()
            .entity(load("json/envDocs.json"))
            .header("envId", id)
            .build();
  }

  @GET
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  @Path("envelopes/{envId}/documents/{docId}")
  public Response downloadDoc(@PathParam("envId") String envId, @PathParam("docId") String docId)
          throws IOException {
    java.nio.file.Path signed = Files.createTempFile("signedDoc", ".pdf.txt");
    Files.writeString(signed, "thanks for signing!", StandardOpenOption.CREATE);
    return Response.ok()
            .entity(signed.toFile())
            .header("docId", docId)
            .header("envId", envId)
            .build();
  }

  private static String load(String path) {
    try (InputStream is = DocuSignServiceMock.class.getResourceAsStream(path)) {
      return IOUtils.toString(is, StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new RuntimeException("Failed to read resource: " + path);
    }
  }
}
