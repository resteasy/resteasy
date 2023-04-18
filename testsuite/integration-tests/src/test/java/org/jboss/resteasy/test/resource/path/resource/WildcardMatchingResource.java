package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("main")
public class WildcardMatchingResource {
    @GET
    public String subresource() {
        return this.getClass().getSimpleName();
    }
}
