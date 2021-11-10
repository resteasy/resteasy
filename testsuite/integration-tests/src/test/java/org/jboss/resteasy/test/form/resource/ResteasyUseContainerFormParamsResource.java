package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

@Path("/")
public class ResteasyUseContainerFormParamsResource {

    @POST
    @Path("/form")
    public Response form(Form form) {
        return map(form.asMap());
    }

    @POST
    @Path("/map")
    public Response map(MultivaluedMap<String, String> map) {
        return map.isEmpty()
                ? Response.serverError().entity("no parameters\n").build()
                : Response.ok().build();
    }
}
