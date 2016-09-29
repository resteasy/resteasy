package org.jboss.resteasy.test.spring.inmodule.resource;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

@Path("/")
public class SpringMvcHttpResponseCodesResource {
    @POST
    @Path("/test/json")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SpringMvcHttpResponseCodesPerson postJson(SpringMvcHttpResponseCodesPerson person) {
        return person;
    }

    @GET
    @Path("/secured/json")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SpringMvcHttpResponseCodesPerson postJsonSecured(SpringMvcHttpResponseCodesPerson person) {
        return person;
    }
}
