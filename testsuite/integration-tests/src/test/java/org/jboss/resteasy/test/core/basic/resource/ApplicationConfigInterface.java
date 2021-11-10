package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/myinterface")
public interface ApplicationConfigInterface {
   @GET
   @Produces("text/plain")
   String hello();

}
