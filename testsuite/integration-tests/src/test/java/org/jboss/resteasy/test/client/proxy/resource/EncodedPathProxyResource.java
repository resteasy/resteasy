package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
