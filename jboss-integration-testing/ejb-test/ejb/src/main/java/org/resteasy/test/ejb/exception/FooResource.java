package org.resteasy.test.ejb.exception;

import javax.ejb.Local;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Local
@Path("/exception")
@Produces("text/plain")
public interface FooResource
{
   @GET
   void testException();
}
