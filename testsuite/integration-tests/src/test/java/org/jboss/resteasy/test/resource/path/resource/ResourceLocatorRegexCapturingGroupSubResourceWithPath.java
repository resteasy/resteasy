package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * User: rsearls
 * Date: 2/17/17
 */
@Produces("text/plain")
public class ResourceLocatorRegexCapturingGroupSubResourceWithPath {
    private String name;

    public ResourceLocatorRegexCapturingGroupSubResourceWithPath(String name) {
        this.name = name;
    }

    @GET
    @Path("/test")
    public Response get() {
        return Response.ok(name + " test").build();
    }
}
