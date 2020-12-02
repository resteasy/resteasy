package org.jboss.resteasy.test.cdi.basic.resource;

import jakarta.ws.rs.Path;

@Path("/")
public interface EjbExceptionUnwrapLocatingResource {
   @Path("locating")
   EjbExceptionUnwrapSimpleResource getLocating();
}
