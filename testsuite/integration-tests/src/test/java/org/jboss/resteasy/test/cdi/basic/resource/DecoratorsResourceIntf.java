package org.jboss.resteasy.test.cdi.basic.resource;


import org.jboss.resteasy.test.cdi.util.Constants;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public interface DecoratorsResourceIntf {
   @POST
   @Path("create")
   @Consumes(Constants.MEDIA_TYPE_TEST_XML)
   @Produces(MediaType.TEXT_PLAIN)
   @DecoratorsFilterBinding
   @DecoratorsResourceBinding
   Response createBook(EJBBook book);

   @GET
   @Path("book/{id:[0-9][0-9]*}")
   @Produces(Constants.MEDIA_TYPE_TEST_XML)
   @DecoratorsFilterBinding
   @DecoratorsResourceBinding
   EJBBook lookupBookById(@PathParam("id") int id);

   @POST
   @Path("test")
   @Produces(MediaType.TEXT_PLAIN)
   Response test();

}
