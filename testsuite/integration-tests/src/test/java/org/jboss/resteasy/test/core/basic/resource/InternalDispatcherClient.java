package org.jboss.resteasy.test.core.basic.resource;

import org.jboss.resteasy.test.core.basic.InternalDispatcherTest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

@Path("/")
public interface InternalDispatcherClient {
   @GET
   @Path("basic")
   @Produces("text/plain")
   String getBasic();

   @PUT
   @Path("basic")
   @Consumes("text/plain")
   void putBasic(String body);

   @GET
   @Path("forward/basic")
   @Produces("text/plain")
   String getForwardBasic();

   @PUT
   @Path("forward/basic")
   @Consumes("text/plain")
   void putForwardBasic(String body);

   @POST
   @Path("forward/basic")
   @Consumes("text/plain")
   void postForwardBasic(String body);

   @DELETE
   @Path("/forward/basic")
   void deleteForwardBasic();

   @GET
   @Produces("text/plain")
   @Path("/forward/object/{id}")
   Response getForwardedObject(@PathParam("id") Integer id);

   @GET
   @Produces("text/plain")
   @Path("/object/{id}")
   Response getObject(@PathParam("id") Integer id);

   @GET
   @Produces("text/plain")
   @Path("/infinite-forward")
   int infiniteForward();

   @GET
   @Path(InternalDispatcherTest.PATH + "/basic")
   @Produces("text/plain")
   String getComplexBasic();

   @GET
   @Path(InternalDispatcherTest.PATH + "/forward/basic")
   @Produces("text/plain")
   String getComplexForwardBasic();
}
