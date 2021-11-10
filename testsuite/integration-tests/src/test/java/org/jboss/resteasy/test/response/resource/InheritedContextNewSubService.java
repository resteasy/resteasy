package org.jboss.resteasy.test.response.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("subsub")
public class InheritedContextNewSubService extends InheritedContextNewService {
   @Path("test/{level}")
   @GET
   public String test(@PathParam("level") String level) {
      return Boolean.toString(level.equals("SomeSubService") && testContexts());
   }
}
