package org.jboss.resteasy.test.form.resource;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

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
