package org.jboss.resteasy.test.client.exception.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/test")
public interface UnauthorizedExceptionInterface {
   @POST
   @Consumes("text/plain")
   void postIt(String msg);
}
