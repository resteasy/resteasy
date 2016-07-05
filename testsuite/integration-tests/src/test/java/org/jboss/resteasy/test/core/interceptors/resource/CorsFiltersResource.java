package org.jboss.resteasy.test.core.interceptors.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
public class CorsFiltersResource {
    @Path("test")
    @GET
    @Produces("text/plain")
    public String get() {
        return "hello";
    }
}
