package org.jboss.resteasy.test.exception.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("resource")
public class AbstractMapperResource {
    @GET
    @Path("custom")
    public String custom() throws Throwable {
        throw new AbstractMapperException("hello");
    }
}
