package org.jboss.resteasy.test.resource.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/form")
public class FormParamResource {

    @Path("/split")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String string(@FormParam("valueA") String value) {
        return value;
    }
}
