package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/unlimited{param:.*}")
public class PathLimitedUnlimitedOnPathResource {
   @GET
   public String hello() {
      return "hello world";
   }
}
