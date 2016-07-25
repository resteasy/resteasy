package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
