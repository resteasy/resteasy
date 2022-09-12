package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;


@Path("/form")
public class FormParamResource {

    @Path("/split")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String string(@FormParam("valueA") String value) {
        return value;
    }
}
