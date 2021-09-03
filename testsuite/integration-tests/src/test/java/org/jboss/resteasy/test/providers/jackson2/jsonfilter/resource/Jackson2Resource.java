package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
/**
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 *
 */
@Path("/products")
public class Jackson2Resource {
   @GET
   @Produces("application/json")
   @Path("{id}")
   public Jackson2Product getProduct(@PathParam("id") int id) {
      return new Jackson2Product(id, "Iphone");
   }
}
