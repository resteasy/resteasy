package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("")
public class GenericReturnTypeResource implements GenericReturnTypeInterface<String> {
   @GET
   @Path("t")
   @Produces("text/plain")
   public String t() {
      return "abc";
   }
}
