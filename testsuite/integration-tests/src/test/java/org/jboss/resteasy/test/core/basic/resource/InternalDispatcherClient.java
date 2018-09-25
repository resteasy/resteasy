package org.jboss.resteasy.test.core.basic.resource;

import org.jboss.resteasy.test.core.basic.InternalDispatcherTest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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
