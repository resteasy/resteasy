package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/person")
public class Jackson2PersonResource {
    @GET
    @Produces("application/json")
    @Path("{id}")
    public Jackson2Person getPerson(@PathParam("id") int id) {
        return new Jackson2Person("Melissa", id, "Brno", PersonType.TURTLE);
    }
}
