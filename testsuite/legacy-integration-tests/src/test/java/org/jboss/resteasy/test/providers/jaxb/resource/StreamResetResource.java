package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test")
public class StreamResetResource {
    @GET
    @Produces("application/xml")
    public String get() {
        return "<person name=\"bill\"/>";
    }
}
