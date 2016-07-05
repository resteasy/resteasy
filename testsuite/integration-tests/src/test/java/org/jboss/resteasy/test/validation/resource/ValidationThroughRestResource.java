package org.jboss.resteasy.test.validation.resource;

import javax.ejb.Stateless;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/hikes")
@Stateless
public class ValidationThroughRestResource {

    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    @POST
    @Path("/createHike")
    @Consumes("application/json")
    @Produces({"application/json", "text/plain"})
    public void createHike(@Min(1) long id, String from, String to) {
        // nothing to do
    }
}
