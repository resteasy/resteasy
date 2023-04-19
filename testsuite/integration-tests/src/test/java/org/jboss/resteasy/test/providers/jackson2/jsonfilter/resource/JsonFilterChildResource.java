package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/superclass")
public class JsonFilterChildResource {
    @GET
    @Produces("application/json")
    @Path("{id}")
    public JsonFilterChild getProduct(@PathParam("id") int id) {
        return new JsonFilterChild(PersonType.CUSTOMER, id, "Melissa");
    }
}
