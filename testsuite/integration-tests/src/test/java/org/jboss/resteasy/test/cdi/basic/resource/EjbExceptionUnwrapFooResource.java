package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.Local;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Local
@Path("/exception")
@Produces("text/plain")
public interface EjbExceptionUnwrapFooResource {
   @GET
   void testException();
}
