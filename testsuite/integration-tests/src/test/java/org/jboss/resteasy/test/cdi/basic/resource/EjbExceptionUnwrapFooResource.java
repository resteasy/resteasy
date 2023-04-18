package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.Local;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Local
@Path("/exception")
@Produces("text/plain")
public interface EjbExceptionUnwrapFooResource {
    @GET
    void testException();
}
