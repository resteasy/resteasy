package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ws.rs.Path;

@Path("/")
public interface EjbExceptionUnwrapLocatingResource {
    @Path("locating")
    EjbExceptionUnwrapSimpleResource getLocating();
}
