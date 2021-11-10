package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/superclass")
public class JsonFilterChildResource {
   @GET
   @Produces("application/json")
   @Path("{id}")
   public JsonFilterChild getProduct(@PathParam("id") int id) {
      return new JsonFilterChild(PersonType.CUSTOMER, id, "Melissa");
   }
}
