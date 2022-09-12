package org.jboss.resteasy.test.cdi.basic.resource;


import org.jboss.resteasy.test.cdi.util.Constants;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/")
public interface EJBResourceParent {
   @GET
   @Path("verifyScopes")
   int verifyScopes();

   @GET
   @Path("verifyInjection")
   int verifyInjection();

   @POST
   @Path("create")
   @Consumes(Constants.MEDIA_TYPE_TEST_XML)
   int createBook(EJBBook book);

   @GET
   @Path("book/{id:[0-9][0-9]*}")
   @Produces(Constants.MEDIA_TYPE_TEST_XML)
   EJBBook lookupBookById(@PathParam("id") int id);

   @GET
   @Path("uses/{count}")
   int testUse(@PathParam("count") int count);

   @GET
   @Path("reset")
   void reset();
}
