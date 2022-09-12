package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("sub")
public class InheritedContextNewService extends InheritedContextService {
   @Path("test/{level}")
   @GET
   public String test(@PathParam("level") String level) {
      return Boolean.toString(level.equals("SomeService") && testContexts());
   }
}
