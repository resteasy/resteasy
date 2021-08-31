package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/person")
public class Jackson2PersonResource {
   @GET
   @Produces("application/json")
   @Path("{id}")
   public Jackson2Person getPerson(@PathParam("id") int id) {
      return new Jackson2Person("Melissa", id, "Brno", PersonType.TURTLE);
   }
}
