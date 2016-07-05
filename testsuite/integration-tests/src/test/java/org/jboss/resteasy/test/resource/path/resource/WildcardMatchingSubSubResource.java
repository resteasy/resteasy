package org.jboss.resteasy.test.resource.path.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("main/{key}/{subkey}")
public class WildcardMatchingSubSubResource {
    @GET
    public String subresource() {
        return this.getClass().getSimpleName();
    }
}
