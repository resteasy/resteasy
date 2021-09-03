package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;

@Path("/")
public class StatsResource {
   @Path("locator")
   public Object getLocator() {
      return null;
   }

   @Path("entry/{foo:.*}")
   @PUT
   @Produces("text/xml")
   @Consumes("application/json")
   public void put() {

   }

   @Path("entry/{foo:.*}")
   @POST
   @Produces("text/xml")
   @Consumes("application/json")
   public void post() {

   }

   @DELETE
   @Path("resource")
   public void delete() {
   }

   @HEAD
   @Path("resource")
   public void head() {
   }
}
