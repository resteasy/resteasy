package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("resource")
public class DuplicateDeploymentResource {
    @GET
    @Produces("text/plain")
    public String get() {
        return "hello world";
    }
}
