package org.jboss.resteasy.test.client.resource;

import org.jboss.resteasy.annotations.cache.Cache;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/cache")
public interface ClientCacheProxy {
   @GET
   @Produces("text/plain")
   String get();

   @Path("/etag/always/good")
   @GET
   @Produces("text/plain")
   String getAlwaysGoodEtag();

   @Path("/etag/never/good")
   @GET
   @Produces("text/plain")
   String getNeverGoodEtag();

   @Path("/etag/always/validate")
   @GET
   @Produces("text/plain")
   String getValidateEtagged();

   @Path("/cacheit/{id}")
   @GET
   @Produces("text/plain")
   @Cache(maxAge = 3000)
   String getCacheit(@PathParam("id") String id);
}
