package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.FormParam;

@Path("/")
public class FormContainerRequestFilterResource {
    @POST
    @Path("a")
    @Consumes("application/x-www-form-urlencoded")
    public String a(@FormParam("fp") String fp) {
        return fp;
    }
    @PUT
    @Path("b")
    @Consumes("application/x-www-form-urlencoded")
    public String b(@FormParam("fp") String fp) {
        return fp;
    }
}

