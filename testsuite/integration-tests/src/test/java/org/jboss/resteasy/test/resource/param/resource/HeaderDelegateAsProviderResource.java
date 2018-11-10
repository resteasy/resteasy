package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

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
