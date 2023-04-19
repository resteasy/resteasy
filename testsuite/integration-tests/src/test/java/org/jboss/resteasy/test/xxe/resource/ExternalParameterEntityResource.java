package org.jboss.resteasy.test.xxe.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("")
public class ExternalParameterEntityResource {
    @POST
    @Path("test")
    @Consumes(MediaType.APPLICATION_XML)
    public String post(ExternalParameterEntityWrapper wrapper) {
        return wrapper.getName();
    }
}
