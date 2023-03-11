package org.jboss.resteasy.test.validation.resource;

import jakarta.ejb.Stateless;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

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
    @Produces({ "application/json", "text/plain" })
    public void createHike(@Min(1) long id, String from, String to) {
        // nothing to do
    }
}
