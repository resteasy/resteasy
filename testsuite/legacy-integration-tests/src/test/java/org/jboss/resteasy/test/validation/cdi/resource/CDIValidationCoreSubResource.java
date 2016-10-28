package org.jboss.resteasy.test.validation.cdi.resource;

import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class CDIValidationCoreSubResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{subparam}")
    @Min(17)
    public int submethod(@Min(13) @PathParam("subparam") int subparam) {
        return subparam;
    }
}