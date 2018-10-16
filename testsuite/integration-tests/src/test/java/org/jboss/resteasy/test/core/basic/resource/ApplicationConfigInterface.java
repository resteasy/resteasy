package org.jboss.resteasy.test.core.basic.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/myinterface")
public interface ApplicationConfigInterface {
   @GET
   @Produces("text/plain")
   String hello();

}
