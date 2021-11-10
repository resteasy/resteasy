package org.jboss.resteasy.embedded.test.interceptor.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("test")
public class PriorityExecutionResource {
   @GET
   public String get() {
      return "test";
   }
}
