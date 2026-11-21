package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * An abstract root resource with no concrete subclass in the deployment. It is never invoked.
 */
@Path("/abstract")
public abstract class AbstractCdiResource {

    @GET
    public String get() {
        return "abstract";
    }
}
