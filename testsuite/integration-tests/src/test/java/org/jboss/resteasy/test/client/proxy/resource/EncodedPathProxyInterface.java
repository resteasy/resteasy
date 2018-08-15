package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

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
