package org.jboss.resteasy.test.core.spi.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/pure")
public class ResourceClassProcessorPureEndPointEJB {
    @GET
    @Path("pure")
    @Produces("text/plain")
    public String getLocating() {
        return "<a></a>";
    }
}
