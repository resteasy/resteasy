package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("test")
public class EncodedPathProxyResource implements EncodedPathProxyInterface {

   @GET
   @Path("/encode/{path:.+}")
   // Note. @Encoded isn't placed on path parameter: it is used on the client side
   //       by the proxy mechanism.
   public Response encode(@PathParam("path") String path, @Context UriInfo uri) {
      return Response.ok(uri.getRequestUri().toString()).build();
   }

   @GET
   @Path("/noencode/{path:.+}")
   public Response noencode(@PathParam("path") String path, @Context UriInfo uri) {
      return Response.ok(uri.getRequestUri().toString()).build();
   }
}
