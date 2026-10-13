package org.jboss.resteasy.test.cdi.ejb.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/ejb-provider")
public class EjbProviderResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get() {
        return "ok";
    }
}
