package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

public class ReaderWriterSub {
    @Path("/without")
    @GET
    @Produces("text/plain")
    public String get() {
        return "hello";
    }
}
