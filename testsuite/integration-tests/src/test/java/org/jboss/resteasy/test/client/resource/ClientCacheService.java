package org.jboss.resteasy.test.client.resource;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.test.client.ClientCacheTest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;

@Path("/cache")
public class ClientCacheService {
   @GET
   @Produces("text/plain")
   @Cache(maxAge = 2)
   public String get() {
      ClientCacheTest.count.incrementAndGet();
      return "hello world" + ClientCacheTest.count;
   }

   @Path("/etag/always/good")
   @GET
   @Produces("text/plain")
   public Response getEtagged(@Context Request request) {
      ClientCacheTest.count.incrementAndGet();
      Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag("42"));
      CacheControl cc = new CacheControl();
      cc.setMaxAge(2);
      if (builder != null) {
         return builder.cacheControl(cc).build();
      }
      return Response.ok("hello" + ClientCacheTest.count.get()).cacheControl(cc).tag("42").build();
   }

   @Path("/etag/never/good")
   @GET
   @Produces("text/plain")
   public Response getEtaggedNeverGood(@Context Request request) {
      ClientCacheTest.count.incrementAndGet();
      Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag("42"));
      if (builder != null) {
         return Response.serverError().build();
      }
      CacheControl cc = new CacheControl();
      cc.setMaxAge(2);
      return Response.ok("hello" + ClientCacheTest.count.get()).cacheControl(cc).tag("32").build();
   }

   @Path("/etag/always/validate")
   @GET
   @Produces("text/plain")
   public Response getValidateEtagged(@Context Request request) {
      ClientCacheTest.count.incrementAndGet();
      Response.ResponseBuilder builder = request.evaluatePreconditions(new EntityTag("42"));
      if (builder != null) {
         return builder.build();
      }
      return Response.ok("hello" + ClientCacheTest.count.get()).tag("42").build();
   }

   @Path("/cacheit/{id}")
   @GET
   @Produces("text/plain")
   @Cache(maxAge = 3000)
   public String getCacheit(@PathParam("id") String id) {
      ClientCacheTest.count.incrementAndGet();
      return "cachecache" + ClientCacheTest.count.get();
   }

}
