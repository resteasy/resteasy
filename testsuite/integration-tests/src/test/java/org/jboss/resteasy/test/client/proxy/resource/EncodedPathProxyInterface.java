package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("test")
public interface EncodedPathProxyInterface
{
   @GET
   @Path("/encode/{path:.+}")
   Response encode(@Encoded @PathParam("path") String path, @Context UriInfo info);

   @GET
   @Path("/noencode/{path:.+}")
   Response noencode(@PathParam("path") String path, @Context UriInfo info);
}
