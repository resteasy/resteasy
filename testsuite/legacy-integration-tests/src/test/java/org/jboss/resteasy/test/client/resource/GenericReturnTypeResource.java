package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("")
public class GenericReturnTypeResource implements GenericReturnTypeInterface<String> {
    @GET
    @Path("t")
    @Produces("text/plain")
    public String t() {
        return "abc";
    }
}
