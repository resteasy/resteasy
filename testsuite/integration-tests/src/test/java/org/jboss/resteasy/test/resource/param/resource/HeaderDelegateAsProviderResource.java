package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

@Path("")
public class HeaderDelegateAsProviderResource {

   @Context HttpHeaders headers;

   @GET
   @Path("server")
   public Response testServer() {
      ResponseBuilder builder = Response.ok().header("HeaderTest", new HeaderDelegateAsProviderHeader("abc", "xyz"));
      return builder.build();
   }

   @GET
   @Path("client/header")
   public String testClient(@HeaderParam("HeaderTest") HeaderDelegateAsProviderHeader header) {
      return header.getMajor() + "|" + header.getMinor();
   }

   @GET
   @Path("client/headers")
   public String testServerHeaders() {
      String header = headers.getRequestHeader("HeaderTest").get(0);
      return header;
   }
}
