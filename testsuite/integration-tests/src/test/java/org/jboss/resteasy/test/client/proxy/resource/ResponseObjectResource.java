package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;

@Path("test")
public class ResponseObjectResource {

   @GET
   @Produces("text/plain")
   public String get() {
      return "ABC";
   }

   @GET
   @Path("/link-header")
   public Response getWithHeader(@Context UriInfo uri) {
      URI subUri = uri.getAbsolutePathBuilder().path("next-link").build();
      Link link = Link.fromUri(subUri).rel("nextLink").build();
      return Response.noContent().header("Link", link.toString()).build();
   }

   @GET
   @Produces("text/plain")
   @Path("/link-header/next-link")
   public String getHeaderForward() {
      return "forwarded";
   }
}
