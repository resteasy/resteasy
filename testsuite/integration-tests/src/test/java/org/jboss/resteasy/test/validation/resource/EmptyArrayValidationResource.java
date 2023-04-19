package org.jboss.resteasy.test.validation.resource;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("")
public class EmptyArrayValidationResource {

    @POST
    @Path("emptyarray")
    @Consumes(MediaType.APPLICATION_JSON)
    public void test(@Valid EmptyArrayValidationFoo foo) {
    }
}
