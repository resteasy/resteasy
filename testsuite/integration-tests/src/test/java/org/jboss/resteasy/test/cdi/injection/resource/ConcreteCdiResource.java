package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * A root resource deployed next to {@link AbstractCdiResource}.
 */
@Path("/concrete")
@RequestScoped
public class ConcreteCdiResource {

    @GET
    public String get() {
        return "concrete";
    }
}
